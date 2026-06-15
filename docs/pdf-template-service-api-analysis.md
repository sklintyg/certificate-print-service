# PDF Template Service – API Analysis

This document analyses all logic performed by `CertificatePdfGenerator` (and its supporting
classes in the `pdfbox-generator` module) that must be communicated through the new API when
PDFBox-based rendering is moved to a separate service. Use this as a guide when writing the
API specification for the new template-fill service.

---

## 1. Purpose

`CertificatePdfGenerator` takes a `Certificate` domain object and `PdfGeneratorOptions`, and
returns a filled PDF document as bytes together with a file name. It does so by:

1. Selecting a PDF template file.
2. Generating a flat list of form-field values.
3. Sanitizing the field values.
4. Handling overflow content (text that exceeds a field's capacity).
5. Writing free-form overlay texts directly on the page canvas.
6. Post-processing the document (flatten, remove security, set title).

All of these concerns, except the initial domain-to-field mapping, will be responsibilities of
the new service. The API must therefore carry enough information for the service to perform each
step without accessing domain objects.

---

## 2. Template Selection

The service selects one of two pre-bundled PDF template variants:

| Condition | Template used |
|-----------|---------------|
| `citizenFormat = true` | No-address template (`pdfNoAddressTemplatePath`) |
| Certificate has been sent (`sent.sentAt != null`) | No-address template |
| Otherwise | Full template with address (`pdfTemplatePath`) |

**API requirement**: The caller must tell the service which template to use (either by sending
the template bytes directly, or by specifying a well-known template identifier).

---

## 3. Form Fields

All form fields are represented by `PdfField`, which has these attributes:

| Attribute | Type | Semantics |
|-----------|------|-----------|
| `id` | String | AcroForm field name in the PDF |
| `value` | String | Value to write into the field |
| `append` | Boolean (default false) | When `true`, this field's value is accumulated with other fields sharing the same `id` (used for overflow/appendix content) |
| `patientField` | Boolean (default false) | Marks patient-ID fields; used to repeat the patient ID on dynamically added overflow pages |
| `unitField` | Boolean (default false) | Marks the unit contact-information field; triggers `normalized` instead of `sanitized` text processing |
| `appearance` | String (nullable) | PDF default-appearance string to override font/size for a specific field (used for diagnosis descriptions) |
| `offset` | Integer (default 0) | Adjusts the field height before writing; used when a value is shorter than the default field and height must be reduced |

The API needs to accept a list of these fields (or an equivalent structure).

### 3.1 Field Categories

Fields are produced by four generators:

#### 3.1.1 Patient fields

- One `PdfField` per field ID listed in `TemplatePdfSpecification.patientIdFieldIds`.
- All receive the same value: the patient's personal-identity number **without dashes**.
- All have `patientField = true`.

#### 3.1.2 Unit contact-information field

- One field with the ID from `PdfSignature.contactInformation`.
- Value is a multiline string:
  ```
  {unit name}
  {address}, {zip} {city}
  Telefon: {phone}
  ```
- Has `unitField = true`.

#### 3.1.3 Signature fields (only when status is SIGNED)

Filled from `PdfSignature` field IDs:

| Field purpose | Source data |
|---------------|-------------|
| `signedDateFieldId` | `certificate.signed` formatted as ISO date (`yyyy-MM-dd`) |
| `signedByNameFieldId` | Issuer full name |
| `paTitleFieldId` | PA-title codes joined by `", "` (omitted when null) |
| `specialtyFieldId` | Speciality values joined by `", "` (omitted when null) |
| `hsaIdFieldId` | Issuer HSA-ID |
| `workplaceCodeFieldId` | Issuing unit workplace code (omitted when null) |

#### 3.1.4 Certificate element-value fields

Each question element in the certificate model is mapped to zero or more `PdfField` entries
according to its `PdfConfiguration`. The table below lists the mapping. The API must convey all
resulting fields after this mapping has been applied by `certificate-service`.

| ElementValue type | Produces |
|-------------------|----------|
| `ElementValueText` | One text field. If content exceeds `maxLength`: either truncated with `"..."` (no overflow sheet) or split with first part in main field and remainder as `append=true` fields in the overflow sheet |
| `ElementValueBoolean` (checkbox) | One checked (`"1"`) or unchecked (`"Off"`) checkbox field depending on the boolean value and whether a false-checkbox field exists |
| `ElementValueBoolean` (radio) | One radio-group field set to the matching option value |
| `ElementValueCode` (checkbox) | One checked field for the matching code's field ID |
| `ElementValueCode` (radio) | One radio-group field set to the matching code's field ID |
| `ElementValueCode` (dropdown) | One text field set to the display label of the selected code |
| `ElementValueCodeList` | One checked field per selected code |
| `ElementValueDate` | One text field with ISO-date string (`yyyy-MM-dd`) |
| `ElementValueDateRange` | Up to two text fields: `from` date and `to` date |
| `ElementValueDateList` | Per checked date: one checkbox field (`"1"`) + one date text field |
| `ElementValueDateRangeList` | Per date-range: one checkbox + optional `from`-date + optional `to`-date |
| `ElementValueDiagnosisList` | Per diagnosis: one name field (with possible overflow split) + one field per character of the diagnosis code; overflow label prepended as `append=true` when overflow exists |
| `ElementValueMedicalInvestigationList` | Per investigation: optional date field, optional source text field, optional investigation-type field (mapped to display label) |
| `ElementValueIcf` | One text field from the simplified text representation of the ICF value (ICF codes + free-text combined); same overflow handling as `ElementValueText` |
| `ElementValueInteger` | One text field with the integer value as a string |

---

## 4. Field Sanitization

Before fields are written to the PDF, their values are sanitized based on font capabilities.
This logic must remain in the service (since it requires access to the PDF font data), but the
API must preserve enough information to trigger it correctly:

- **`unitField = true` or `append = true`**: value goes through `normalizePrintableCharacters`
  (replaces characters not supported by the font with visually similar alternatives).
- **All other fields**: value goes through `sanitizeText` (strips characters not representable
  in the font).

The field-type flags (`unitField`, `append`, `patientField`) must therefore be present in the API.

---

## 5. Overflow Handling

When one or more fields with `append = true` exist, overflow processing is triggered.

### 5.1 Overflow sheet (pre-existing template page)

The template contains an optional overflow page at a known page index
(`TemplatePdfSpecification.overFlowPageIndex`). If **no** `append` fields are produced by the
value generators, this page is **removed** from the document before returning.

**API requirement**: The caller must indicate the page index of the overflow page (or signal
that no overflow page exists).

### 5.2 Single overflow page

All `append` fields sharing the same AcroForm field ID are grouped, paginated to fit inside the
field's bounding rectangle, and written into the first overflow page.

### 5.3 Additional overflow pages

If the grouped content does not fit on the first overflow page, additional pages are dynamically
appended. Each extra page:

- Is a blank copy of the overflow page template.
- Has the overflowing text written as free-form text starting at the same X/Y position as the
  overflow field, with the same font size and font from that field's default appearance.
- Receives the patient ID printed at the same position as the patient-ID field on the original
  overflow page.
- Gets an accessibility structure tag added.

**API requirement**: The service needs the overflow field ID and starting coordinates, but these
can be derived from the PDF template itself. The caller needs to indicate which field ID is the
"overflow accumulator".

---

## 6. Overlay Texts (canvas-drawn, not form fields)

These texts are drawn directly onto the page content stream and are not part of the AcroForm.
They require document-level metadata from the caller.

### 6.1 Draft watermark

- Drawn when status is `DRAFT`.
- Text: `"UTKAST"` (large diagonal watermark on every page).

**API requirement**: A flag or status value indicating the certificate is a draft.

### 6.2 Digital signature text

- Drawn when status is `SIGNED`.
- Text: `"Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren."`
- Positioned to the right of and just below the signed-date AcroForm field, using a fixed
  padding (`x + 60`, `y + 2` relative to the field's rectangle).
- Placed on the signature page (`PdfSignature.signaturePageIndex`).
- Uses one of two accessibility tag indices (`signatureWithAddressTagIndex` or
  `signatureWithoutAddressTagIndex`) depending on whether the address template is used.

**API requirement**: Whether the certificate is signed, which page index the signature is on,
and which tag index to use (with/without address variant). The X/Y position is derived from the
PDF field geometry so no explicit coordinates are needed.

### 6.3 Sent text

- Drawn on the first page when `sent.sentAt != null`.
- Line 1 (always): `"Intyget har skickats digitalt till {recipient name}"` at position (40, 685).
- Line 2 (only when `availableForCitizen = true`): `"Du kan se intyget genom att logga in på 1177.se"` at position (40, 665).

**API requirement**: Whether the certificate has been sent, the recipient name, and whether the
certificate is available for citizens.

### 6.4 Margin text (certificate ID + additional info)

- Drawn in the margin of every page when status is `SIGNED`.
- Text: `"Intygsid: {certificateId}. {additionalInfoText}"`

**API requirement**: The certificate ID and the `additionalInfoText` string (which comes from
`PdfGeneratorOptions.additionalInfoText` and is typically the reference to the issuing
organisation's system).

### 6.5 Page numbers

- Drawn in the upper-right corner of every page.
- Text format: `"X (Y)"` where X is the current page number and Y is the total number of pages.
- Only drawn when `TemplatePdfSpecification.hasPageNbr = false` (i.e., the template itself does
  not already contain a page-number element).

**API requirement**: A flag indicating whether the service should add page numbers.

---

## 7. Post-Processing

After all fields and texts have been applied, the service performs:

1. **AcroForm flattening** – makes all form fields non-interactive (merged into the page content).
2. **Remove all security** – `document.setAllSecurityToBeRemoved(true)`.
3. **Set document title** – set to the generated file name (see section 8).

These are internal to the PDF service and require no additional API inputs beyond the file name.

---

## 8. File Name Generation

The file name is derived from the certificate model's `name` field (not the recipient's general
name, which is used by `GeneralPdfGenerator`):

```
{certificateName}_{yy-MM-dd_HHmm}
```

Transformations applied (in order):
1. Replace `å` → `a`
2. Replace `ä` → `a`
3. Replace `ö` → `o`
4. Replace ` ` → `_`
5. Remove `–` (en-dash)
6. Replace `__` → `_`
7. Convert to lower case
8. Append timestamp: `_{yy-MM-dd_HHmm}` (server-side timestamp at generation time)

**Note**: The timestamp is appended by `CertificatePdfGenerator` after name normalization.
Depending on architecture choices, the caller may pass the desired file name or the service may
generate it from a provided certificate name.

---

## 9. Summary of Required API Inputs

The table below summarises all inputs the new API must expose to fully replicate the current
behaviour of `CertificatePdfGenerator`.

| Input | Usage |
|-------|-------|
| PDF template (bytes or identifier) | Loaded and filled by the service |
| Template variant flag (`withAddress` / `withoutAddress`) | Selects which template path to use |
| List of `PdfField` entries (`id`, `value`, `append`, `patientField`, `unitField`, `appearance`, `offset`) | AcroForm fields to fill |
| Certificate status (`DRAFT` / `SIGNED` / other) | Controls watermark, signature text, margin text, sent text |
| Sent info: `isSent` (boolean), `recipientName` (String) | Controls sent-text overlay |
| `availableForCitizen` (boolean) | Controls second sent-text line |
| `certificateId` (String) | Used in margin text |
| `additionalInfoText` (String) | Used in margin text |
| `hasPageNbr` (boolean, from spec) | Controls whether page numbers are drawn |
| `overflowPageIndex` (Integer, nullable) | Index of overflow page to remove if unused |
| Signature info: `signaturePageIndex` (int), tag-index variant (with/without address) | Required for digital signature overlay placement |
| Certificate name (String) | For file name generation |

---

## 10. What Stays in certificate-service

The following logic is **not** part of the new service and remains in `certificate-service`:

- Mapping `ElementValue` → `PdfField` list (all `Pdf*ValueGenerator` classes).
- Overflow-split decision and splitting of text into main-field part and overflow part (this
  determines which fields get `append = true` and what values they carry).
- Template path selection logic (resolving which template path to pass to the service).
- File name normalisation of the certificate name (the Swedish character replacements can be
  applied before sending the name to the service, or kept as is and documented as a service
  responsibility).
