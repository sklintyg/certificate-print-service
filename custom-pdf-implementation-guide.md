# Custom PDF Implementation Guide — Certificate Print Service (CPS)

This guide describes how CPS should process the `CustomPrintRequestDTO` payload when generating a
PDF for a certificate. The certificate-service sends pre-computed field values and metadata; CPS is
responsible for filling the PDF template and handling all overflow logic.

---

## Overview of the Request

The request contains three top-level parts:

- **`template`** — Base64-encoded PDF template bytes.
- **`metadata`** — Overlay texts, accessibility metadata, draft watermark flag, margin text.
- **`fields`** — A `Map<String, CustomPdfFieldDTO>` where each key is a PDF AcroForm field ID and
  the value contains the data needed to fill that field.

---

## Updated DTOs (Target State)

These are the DTOs as they should look after all planned fields are added.

### `CustomPrintRequestDTO`

```java
public record CustomPrintRequestDTO(
    String template,
    CustomPdfMetadataDTO metadata,
    Map<String, CustomPdfFieldDTO> fields) {}
```

### `CustomPdfMetadataDTO`

```java
public record CustomPdfMetadataDTO(
    List<CustomTextDTO> customTexts,
    AccessibilityMetadataDTO accessibilityMetadata,
    String rightMarginText,
    boolean addDraftWatermark,
    Integer overflowPageIndex,       // NEW — index of the overflow page in the template
    boolean addPageNumbers) {}        // NEW — true means CPS must stamp page numbers
```

### `CustomPdfFieldDTO`

```java
public record CustomPdfFieldDTO(
    String value,
    Integer offset,
    String appearance,
    Integer maxLength,               // NEW (nullable) — character limit; null means no length check
    boolean shouldRemoveLineBreaks,  // NEW — true = strip \n before processing
    OverflowConfigDTO overflow) {}   // NEW (nullable) — null means no overflow sheet handling

public record OverflowConfigDTO(
    String overflowFieldId,                  // NEW (nullable) — null means truncate only
    String overflowLabel) {}                 // NEW — label written before overflow text
```

---

## `CustomPdfFieldDTO` — Field Reference

| Field             | Type               | Nullable | Description                                                                               |
|-------------------|--------------------|----------|-------------------------------------------------------------------------------------------|
| `value`           | `String`           | No       | The value to write into the PDF field.                                                    |
| `offset`          | `Integer`          | Yes      | Additional pixel height to add when adjusting the field's bounding box. Null means no adjustment. |
| `appearance`      | `String`           | Yes      | A PDF default appearance string (e.g. `/Helvetica 9 Tf 0 g`). Null means use the template default. |
| `maxLength`       | `Integer`          | Yes      | Character limit for this field. If non-null and `value.length() > maxLength`, overflow/truncation handling is required. Null means no length limit — write value directly. |
| `shouldRemoveLineBreaks` | `boolean` | No       | Whether to strip `\n` characters from `value` before writing to the main field. Does **not** apply to text written to overflow sheets. Defaults to false if not present in JSON. |
| `overflow`        | `OverflowConfigDTO`| Yes      | Overflow configuration. Null means no overflow sheet — if maxLength is exceeded, truncate with `"..."`. |

### `OverflowConfigDTO` — Nested Overflow Fields

| Field                    | Type      | Nullable | Description                                                                               |
|--------------------------|-----------|----------|-------------------------------------------------------------------------------------------|
| `overflowFieldId`        | `String`  | Yes      | The AcroForm field ID of the overflow sheet field. Null means no overflow sheet — truncate and discard. |
| `overflowLabel`          | `String`  | Yes      | The label to write to the overflow sheet before the overflowed text. This is the question name as displayed in the certificate UI (e.g., "Funktionsnedsättning"). Must be non-null when `overflowFieldId` is non-null. |

> **Note:** `OverflowConfigDTO` is planned but not yet in the codebase.
> `overflowPageIndex` and `addPageNumbers` are planned for `CustomPdfMetadataDTO`. This guide describes
> the intended final state.

---

## Processing Order — Use a LinkedHashMap

The `fields` map **must be processed in insertion order**. Multiple questions from the same
certificate can contribute to the same overflow field, and the order of contributions must match
the order the fields appear in the certificate model.

When receiving the `fields` map, CPS must convert it to a `LinkedHashMap` (or equivalent
ordered structure) before processing. Using a default `HashMap` will produce non-deterministic
overflow sheet output.

> **Jackson note:** Jackson's default `ObjectMapper` deserializes JSON objects into `LinkedHashMap`,
> preserving key order. CPS must not reconfigure the ObjectMapper to use an unordered Map
> implementation for this DTO. Certificate-service guarantees that the JSON payload is serialized
> from a `LinkedHashMap` with fields in document order.

---

## Scenario 1 — Simple Field (no overflow)

**Trigger:** `maxLength` is null (no length checking needed).

Write `value` directly into the AcroForm field identified by the map key.

**Why:** Most fields (dates, booleans, codes, short texts) never overflow. No additional
processing is needed.

---

## Scenario 2 — Field with Appearance Override

**Trigger:** `appearance` is non-null.

Before setting the field value, update the field's default appearance string with the value of
`appearance`. This affects font, size, and colour for that specific field.

**Why:** Some fields in the PDF template (e.g. diagnosis description fields) use a smaller or
different font than the template default. The certificate-service sends the correct appearance
string; CPS must apply it before writing the value to ensure the text renders correctly.

---

## Scenario 3 — Field with Height Offset

**Trigger:** `offset` is non-null and the field is a variable text field.

Adjust the field's bounding-box height using the formula:

```
new height = default height + fontSize - 1 + offset
```

The `fontSize` must be parsed from the field's existing default appearance string in the PDF
template (e.g. parsing `"/Helvetica 9 Tf 0 g"` gives `fontSize = 9`). The default appearance is
read from the AcroForm field before any modifications.

This adjustment must be applied before the value is written, and only for variable text fields
(not for non-text AcroForm field types such as checkboxes or radio buttons).

**Why:** Certain text fields in the FK PDF templates are sized for single-line content by default.
When a multi-line value is written the box needs to be taller to display correctly. The `offset`
fine-tunes the expansion beyond the standard font-size-based adjustment.

---

## Scenario 4 — Text Overflow Without an Overflow Sheet

**Trigger:** `maxLength` is non-null **and** `value.length() > maxLength` **and** (`overflow` is null **or** `overflow.overflowFieldId` is null).

1. If `shouldRemoveLineBreaks` is true, strip `\n` from `value`.
2. Split the text at the last space character at or before `maxLength - suffix.length()`
   characters, where `suffix` is always `"..."`.
3. Append `"..."` to the first part.
4. Write the first part (with suffix) to the field.
5. Discard the remainder — there is no sheet to put it on.

**Why:** Some questions have a `maxLength` configured but no corresponding overflow sheet in the
PDF template. The text is truncated to fit. The `"..."` suffix signals to the reader that the
field was truncated. Without this truncation the PDF AcroForm library may silently clip or reject
values that exceed the field's internal capacity.

---

## Scenario 5 — Text Overflow With an Overflow Sheet

**Trigger:** `maxLength` is non-null **and** `overflow` is non-null **and** `overflow.overflowFieldId` is non-null **and** `value.length() > maxLength`.

### Determine the suffix for the main field

The suffix appended to the first part of the split text depends on `maxLength`:

- If `maxLength > 22`: suffix = `"... Se fortsättningsblad!"`
- If `maxLength ≤ 22`: suffix = `"..."`

### Splitting

1. If `shouldRemoveLineBreaks` is true, strip `\n` from `value` for the purpose of the main field.
   The **original unstripped value** is used when determining the remainder for the overflow sheet.
2. Compute `effectiveLimit = maxLength - suffix.length()`.
3. Split the text at the last space character at or before `effectiveLimit` characters.
4. The first part gets the suffix appended: `firstPart + " " + suffix`.
5. The second part (the remainder) is prefixed with `"... "`: `"... " + remainder`.

### Writing

- Write the first part (with suffix) to the main field (the map key).
- Accumulate the following two entries to the overflow field identified by `overflow.overflowFieldId`:
  1. `overflow.overflowLabel` — the question name, used as a heading in the overflow sheet.
  2. The second part (the remainder text, **not** line-break-stripped) followed by `"\n"`.

Both overflow entries are **appended** to any existing content in `overflow.overflowFieldId`. See
[Scenario 6](#scenario-6--accumulating-multiple-overflow-contributions) for how accumulation works.

### Validation

If `overflow.overflowFieldId` is non-null but `overflow.overflowLabel` is null, this is an
**invalid state**. CPS should reject the request or log an error — every overflow contribution
must have a label so the continuation page remains readable.

**Why:** FK certificates have a dedicated overflow/continuation page (`fortsättningsblad`) in the
PDF template. When a clinician writes more text than the main field allows, the excess must appear
on this page. The label is written first so the reader knows which question the text belongs to.
The suffix on the main field signals that more text exists on the continuation page.

---

## Scenario 6 — Accumulating Multiple Overflow Contributions

**Trigger:** Two or more fields in the `fields` map have the same `overflow.overflowFieldId`.

All contributions to a given `overflowFieldId` are gathered and written as a single concatenated
value to that overflow field. The order of contributions must follow the insertion order of the
`fields` map (see the LinkedHashMap requirement above).

The final value written to the overflow field is:

```
[label1]\n[remainder1]\n[label2]\n[remainder2]\n...
```

Each question contributes a label entry followed by its overflow text, separated by newlines.

**Why:** Multiple questions on the same certificate can all overflow to the same continuation page
field. If they were written independently the later writes would overwrite earlier ones. The
accumulated content must be written once, in document order, so that the continuation page reads
naturally from top to bottom.

---

## Scenario 7 — `shouldRemoveLineBreaks` Flag

**Trigger:** `shouldRemoveLineBreaks` is true.

Replace all `\n` characters in `value` with an empty string. This stripping is applied
**unconditionally** when the flag is true — even if the resulting value fits within `maxLength`
and no split is needed, the stripped value (without line breaks) is what gets written to the main
field.

> **Important:** The stripping only affects the value written to the **main field**. If the text
> overflows to an overflow sheet (Scenario 5), the remainder text written to the overflow field
> is **not** stripped — it retains any original line breaks so the continuation page remains
> readable.

**Why:** CPS does not know the original value type (text area, diagnosis description, etc.).
Diagnosis description fields contain plain names that should not retain line breaks in the main
field. Free-text fields entered by clinicians may contain intentional line breaks that should
be preserved. Because CPS cannot infer this from the field ID or value alone, the certificate-service
explicitly signals the desired behaviour via this flag.

---

## Scenario 8 — Overflow Page Capacity Exceeded (Pagination)

**Trigger:** The total accumulated text for an `overflowFieldId` exceeds the physical height of the
overflow field's bounding box in the PDF template.

The PDF template contains exactly one overflow/continuation page with a fixed-height AcroForm text
field. When the combined text from all overflow contributions is too tall to fit in that field, CPS
must dynamically add more continuation pages to the document.

### Overflow page index

The metadata includes an `overflowPageIndex` field that tells CPS which page in the template is
the overflow/continuation page. This page is used as the source when cloning new continuation pages.

### How to determine if pagination is needed

After accumulating all overflow contributions for a given `overflowFieldId`:

1. Load the overflow field's rectangle from the PDF AcroForm (position and dimensions).
2. Using the field's font and font size (read from the field's default appearance), measure how many
   wrapped lines of text fit within the rectangle height, after subtracting the top margin
   (`Y_MARGIN_APPENDIX_PAGE`).
3. Wrap each line of the accumulated text using the field's pixel width and font metrics to get the
   actual rendered line count.
4. If the actual line count exceeds the available lines, pagination is required.

### First page (the template page)

Fill the overflow AcroForm field in the template with as many lines as fit. This uses the standard
AcroForm `setValue` mechanism.

### Additional pages (dynamically added)

For each batch of lines that does not fit on the previous page:

1. Clone the overflow page from the template (the page at the configured overflow page index) to
   create a blank continuation page.
2. Append the new page to the PDF document.
3. Render the text for this page as **free-form positioned text** (not as an AcroForm field value)
   at coordinates derived from the overflow field's rectangle, offset by the page margins.
4. Add accessibility tags for the new page content. CPS should follow the same tagging structure as
   the template's overflow page (Page > Sect > Div hierarchy) to maintain PDF/UA compliance.
5. Stamp the **patient ID** on every new continuation page. The patient ID value is taken from the
   field that was written via `patientIdFieldIds` in the specification. CPS determines the position
   for the patient ID stamp on new pages using its own layout logic — this is not provided in the
   API since it requires font metrics and page geometry that only CPS has access to.

The capacity of each additional page is determined the same way as the first, but using the full
field height (no existing content to account for).

**Why:** The PDF template is a static file with a single overflow page of fixed dimensions.
Certificate-service does not load or inspect the template — it has no way to know whether the text
will fit. CPS is the only party that has the template bytes, the font metrics, and the field
geometry needed to make this determination and act on it.

---

## Scenario 9 — Page Number Stamping

**Trigger:** The metadata includes `addPageNumbers = true` (indicating the template
does not have pre-rendered page numbers).

After all fields have been filled and any overflow pages have been added, CPS must stamp a page
number on **every page** in the final document (including dynamically added overflow pages).

### Format

```
X (Y)
```

Where `X` is the current page number (1-based) and `Y` is the total number of pages in the final
document.

### Position

The page number is rendered as free-form text in the top-right corner of each page (right margin,
near the top edge). CPS determines the exact coordinates from the page's media box dimensions.

### When `addPageNumbers = false`

The template already contains pre-rendered page numbers. CPS must **not** add additional page
numbers.

> **Invariant:** When `addPageNumbers = false`, the template has pre-rendered page numbers and
> will never have overflow fields configured. CPS can assume that no pagination (dynamic overflow
> pages) is needed in this case. This means the "stamp overflow pages regardless" scenario does
> not arise.

**Why:** Some PDF templates (e.g. FK7804) do not include page numbers in the template itself. Since
CPS can dynamically add pages (overflow pagination), the total page count is not known until all
processing is complete. Page numbers must therefore be added as the final step, after the document
structure is finalized.

> **Note:** `addPageNumbers` is planned but not yet added to `CustomPdfMetadataDTO`.

---

## Summary — Decision Tree for Each Field

```
Receive field (id → CustomPdfFieldDTO)
│
├── appearance non-null?
│     └── YES → set default appearance on AcroForm field before writing
│
├── shouldRemoveLineBreaks true?
│     └── YES → strip \n from value (always, regardless of length)
│
├── maxLength null?
│     └── YES → write value directly → done
│
├── value.length() ≤ maxLength?
│     └── YES → write value directly → done
│
└── value.length() > maxLength (overflow/truncation required)
      │
      ├── overflow null OR overflow.overflowFieldId null?
      │     └── YES → truncate with "...", write first part, discard rest → done
      │
      └── overflow.overflowFieldId non-null
            └── split text, write first part + suffix to main field,
                accumulate label + remainder to overflowFieldId → done

After processing ALL fields:
└── For each overflowFieldId with accumulated content
      └── measure rendered line count against overflow field rectangle height
            ├── fits → write via AcroForm setValue
            └── does not fit → fill first page via AcroForm, add new pages
                              with free-form text + patient ID stamp per page
```

> **Note on `offset`:** Applied independently of overflow logic. If `offset` is non-null and the
> field is a variable text field, adjust the field height before writing the value regardless of
> whether overflow handling was required.
