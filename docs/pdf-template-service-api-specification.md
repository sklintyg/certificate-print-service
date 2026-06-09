# PDF Template Service – API Specification

This document defines the REST API contract for the `certificate-print-service` template-fill
endpoint. The service receives a pre-loaded PDF template together with all data needed to fill
it, and returns the filled PDF as bytes.

**Endpoint**: `POST /api/v1/fill`  
**Content-Type**: `application/json`  
**Response Content-Type**: `application/json`

---

## Design Principles

- The caller (`certificate-service`) owns all domain knowledge: it decides which form fields to
  fill, what values they get, and whether overflow content is needed.
- The print service owns all PDFBox-specific rendering: font-metric calculations, text sanitization
  against the embedded fonts, overlay text drawing, dynamic overflow page creation, accessibility
  (tagged-PDF) structure-tree extension, AcroForm flattening, and security removal.
- The API surface is intentionally small: two request fields (`metadata` and `fields`) plus the
  PDF template bytes.

---

## Request: `FillPdfRequest`

```json
{
  "template": "<base64-encoded PDF bytes>",
  "metadata": { ... },
  "fields": {
    "<fieldId>": [ { ... } ]
  }
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `template` | `string` (base64) | Yes | The raw PDF template file to fill. The caller selects the correct template variant (with/without address) before sending. |
| `metadata` | `PdfMetadataOptions` | Yes | Certificate-level metadata that controls overlay texts, page behaviour, and overflow page handling. |
| `fields` | `Map<String, List<PdfFieldFillOptions>>` | Yes | Form fields to fill. Key is the AcroForm field ID. Value is an ordered list of fill operations for that field (see section below). |

---

## `PdfMetadataOptions`

Controls all behaviour that is not tied to a specific form field: overlay texts drawn directly on
the page canvas, page number rendering, and overflow page management.

```json
{
  "status": "DRAFT",
  "isSent": false,
  "sentRecipientName": null,
  "availableForCitizen": false,
  "certificateId": "abc-123",
  "additionalInfoText": "Webcert 2.0",
  "addPageNumbers": true,
  "overflowPageIndex": 2,
  "signaturePageIndex": 0,
  "signatureTagIndex": 5,
  "signedDateFieldId": "form1[0].#subform[0].signedDate[0]",
  "patientId": "19121212-1212",
  "patientIdFieldId": "form1[0].#subform[0].patientPersonnummer[0]",
  "startMcid": 100,
  "untaggedWatermarks": ["UTKAST"]
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `status` | `enum` | Yes | Certificate lifecycle status. Controls which overlay texts are rendered. Allowed values: `DRAFT`, `DELETED_DRAFT`, `LOCKED_DRAFT`, `SIGNED`, `REVOKED`. Only `DRAFT` (draft watermark) and `SIGNED` (signature text + margin text) produce status-specific overlays; all other statuses produce none. |
| `isSent` | `boolean` | Yes | Whether the certificate has been sent to a recipient. When `true`, a sent-confirmation text is drawn on the first page. |
| `sentRecipientName` | `string` | When `isSent=true` | Display name of the recipient, used in the sent-confirmation overlay text: `"Intyget har skickats digitalt till {name}"`. |
| `availableForCitizen` | `boolean` | Yes | When `true` and `isSent=true`, a second overlay line is added: `"Du kan se intyget genom att logga in på 1177.se"`. |
| `certificateId` | `string` | Yes | The certificate's unique identifier. Used in the margin text on every page when `status=SIGNED`: `"Intygsid: {certificateId}. {additionalInfoText}"`. |
| `additionalInfoText` | `string` | No | Free-form text appended after the certificate ID in the margin. Typically references the issuing system (e.g., `"Webcert 2.0"`). May be `null` or empty. |
| `addPageNumbers` | `boolean` | Yes | When `true`, the service draws page numbers in the upper-right corner of every page in the format `"X (Y)"`. Set to `false` when the template already contains a built-in page-number element. |
| `overflowPageIndex` | `integer` | No | Zero-based index of the overflow/appendix page in the template. When set, the service removes this page if no `append=true` fields are present in the request. When `null`, the template has no overflow page. |
| `signaturePageIndex` | `integer` | Yes | Zero-based index of the page where the digital signature overlay text is drawn. Only relevant when `status=SIGNED`. |
| `signatureTagIndex` | `integer` | Yes | Accessibility structure tag index used when placing the digital signature text. The caller selects between the "with-address" and "without-address" variant and passes the resolved integer value. |
| `signedDateFieldId` | `string` | When `status=SIGNED` | AcroForm field ID of the signed-date field. The digital signature overlay text is positioned relative to this field's rectangle (to the right of and just below it). The service reads the field geometry before flattening. |
| `patientId` | `string` | Yes | Patient personal-identity number (without dashes). Used to repeat the patient ID header on any dynamically added overflow pages. |
| `patientIdFieldId` | `string` | When `overflowPageIndex` is set | AcroForm field ID of the patient-ID field on the overflow page. Used to determine where to print `patientId` on dynamically added overflow pages (it is drawn at the same position as this field). |
| `startMcid` | `integer` | Yes | Starting marked-content identifier (MCID) for accessibility tagging. The service increments this counter for every canvas-drawn text element (page numbers, signature text, sent text, margin text, overflow content) so the tags remain unique relative to the template's existing tagged content. |
| `untaggedWatermarks` | `array<string>` | No (default: `[]`) | Watermark texts that exist visually on the template but are not part of its accessibility structure tree. When the service adds a new overflow page, it adds these strings as accessibility tags on that page. |

### Overlay Text Behaviour Summary

| Overlay | Triggered when | Content |
|---------|---------------|---------|
| Draft watermark | `status = DRAFT` | Large diagonal `"UTKAST"` on every page |
| Digital signature text | `status = SIGNED` | `"Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren."` — positioned to the right of and just below the signed-date field, on `signaturePageIndex` |
| Sent confirmation | `isSent = true` | Line 1: `"Intyget har skickats digitalt till {sentRecipientName}"` |
| Citizen visibility | `isSent = true` AND `availableForCitizen = true` | Line 2: `"Du kan se intyget genom att logga in på 1177.se"` |
| Margin certificate ID | `status = SIGNED` | `"Intygsid: {certificateId}. {additionalInfoText}"` on every page |
| Page numbers | `addPageNumbers = true` | `"X (Y)"` upper-right corner of every page |

---

## `PdfFieldFillOptions`

Each entry in the `fields` map value list represents one fill operation for the given field ID.
Most fields have a single entry. Overflow fields have multiple entries (see Overflow section).

```json
{
  "value": "Some text content",
  "append": false,
  "appearance": null,
  "offset": 0,
  "normalizeText": false
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `value` | `string` | Yes | The string value to write into the PDF field. For checkboxes this is `"1"` (checked) or `"Off"` (unchecked). For radio groups this is the export value of the option to select. For text fields this is the text content. |
| `append` | `boolean` | No (default: `false`) | When `true`, this entry is treated as overflow/appendix content. All entries with `append=true` for a given field ID are accumulated in order and paginated into the overflow page by the service. See the Overflow section. |
| `appearance` | `string` | No | PDF default-appearance string (e.g., `/Helv 9 Tf 0 g`) to override the font and size for this specific field. Used for fields that require a smaller font than the template default, such as diagnosis description fields. Pass `null` to use the field's built-in appearance. |
| `offset` | `integer` | No (default: `0`) | Vertical offset (in points) applied to adjust the field's rendered height before writing the value. Used when a value is shorter than the default field height and the field should be visually compressed. Pass `0` for no adjustment. |
| `normalizeText` | `boolean` | No (default: `false`) | Controls how the value is processed against the embedded PDF font. When `false`, the value is **sanitized** (characters not representable in the font are stripped). When `true`, the value is **normalized** (unsupported characters are replaced with visually similar alternatives instead of stripped). Set to `true` for the unit contact-information field. Note: `append=true` entries are always normalized regardless of this flag. |

### Field ID Usage Examples

| Field content | `value` | `append` | `appearance` | `offset` | `normalizeText` |
|---------------|---------|----------|--------------|---------|-----------------|
| Text area content | `"Patient text…"` | `false` | `null` | `10` | `false` |
| Checkbox (checked) | `"1"` | `false` | `null` | `0` | `false` |
| Checkbox (unchecked) | `"Off"` | `false` | `null` | `0` | `false` |
| Radio group selection | `"OPTION_VALUE"` | `false` | `null` | `0` | `false` |
| Dropdown text | `"Displayable label"` | `false` | `null` | `0` | `false` |
| Diagnosis description | `"Kronisk…"` | `false` | `"/Helv 9 Tf 0 g"` | `0` | `false` |
| Unit contact information | `"Vårdcentralen\nGatan 1, 12345 Stad\nTelefon: 08-1234"` | `false` | `null` | `0` | `true` |
| Overflow heading | `"Diagnos"` | `true` | `null` | `0` | `false` |
| Overflow body text | `"Full description…\n"` | `true` | `null` | `0` | `false` |

---

## Overflow / Appendix Page Handling

When the `fields` map contains one or more entries with `append=true` for a given field ID, the
service enters overflow mode for that field. The map key of the `append=true` entries **must
equal the AcroForm field ID of the overflow field** in the template — the service uses this key
to locate the overflow field and its geometry.

1. **Overflow page presence check**: If `metadata.overflowPageIndex` is set and no `append=true`
   fields exist in the request, the service **removes** the overflow page from the document before
   returning.

2. **Content accumulation**: All `append=true` entries for the overflow field ID are collected in
   the order they appear in the list and concatenated with newlines.

3. **First overflow page**: The accumulated content is written into the overflow AcroForm field on
   the existing template page at `metadata.overflowPageIndex`. The service uses actual font metrics
   from the template to determine how much content fits.

4. **Dynamic extra pages**: If the accumulated content exceeds the capacity of the overflow field,
   the service appends one or more new pages to the document. Each extra page:
   - Is a blank copy of the overflow page layout.
   - Has the overflow text rendered as free-form page content (not as an AcroForm field) using the
     same font and font size as the overflow field.
   - Has the patient ID (`metadata.patientId`) printed at the same position as the patient-ID
     field on the original overflow page.
   - Gets an appropriate PDF accessibility structure tag.

5. **Non-append fields**: All `append=false` entries are written into their respective AcroForm
   fields normally, independent of overflow processing.

---

## Accessibility (Tagged PDF)

The output must remain a tagged (accessible / PDF-UA) document. The template already contains an
accessibility structure tree; the service must extend it for everything it draws.

- **Marked-content IDs**: Every canvas-drawn text element (page numbers, signature text, sent
  text, margin text, overflow text, repeated patient ID) must be assigned a unique
  marked-content ID. The counter starts at `metadata.startMcid` and increments per element, so
  the new IDs do not collide with the template's existing tagged content.
- **Overflow page tagging**: When the service appends a new overflow page, it builds the page's
  structure tree (page container, watermark tags from `metadata.untaggedWatermarks`, a page-number
  div, a patient-ID div tagged `"Personnummer"`, and a continuation heading `"Fortsättningsblad"`).
- **Signature tag index**: The digital signature text is attached to the structure tree using
  `metadata.signatureTagIndex` (the caller resolves the with/without-address variant).

---

## Post-Processing

After all fields and overlays have been applied, the service performs the following steps before
returning the document:

1. **AcroForm flattening**: All form fields are merged into the page content stream, making them
   non-interactive.
2. **Security removal**: All encryption and security restrictions are removed from the document.

The service does **not** set the document title or generate a file name — those are the caller's
responsibility.

---

## Response: `FillPdfResponse`

```json
{
  "pdfData": "<base64-encoded PDF bytes>"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `pdfData` | `string` (base64) | The filled, flattened, and security-stripped PDF document. |

---

## Error Responses

| HTTP Status | Condition |
|-------------|-----------|
| `400 Bad Request` | Missing required fields, unknown field ID referenced in `fields` map, invalid `status` value. |
| `422 Unprocessable Entity` | The provided `template` bytes are not a valid PDF document. |
| `500 Internal Server Error` | Unexpected failure during rendering (e.g., font not found, field type mismatch). |

Error response body:

```json
{
  "error": "Field 'form-field-xyz' not found in template",
  "status": 400
}
```

---

## Full Request Example

```json
{
  "template": "JVBERi0xLj...",
  "metadata": {
    "status": "SIGNED",
    "isSent": true,
    "sentRecipientName": "Försäkringskassan",
    "availableForCitizen": true,
    "certificateId": "550e8400-e29b-41d4-a716-446655440000",
    "additionalInfoText": "Webcert 2.0",
    "addPageNumbers": true,
    "overflowPageIndex": 2,
    "signaturePageIndex": 0,
    "signatureTagIndex": 5,
    "signedDateFieldId": "form1[0].#subform[0].signedDate[0]",
    "patientId": "191212121212",
    "patientIdFieldId": "form1[0].#subform[0].patientPersonnummer[0]",
    "startMcid": 100,
    "untaggedWatermarks": ["UTKAST"]
  },
  "fields": {
    "form1[0].#subform[0].patientPersonnummer[0]": [
      { "value": "191212121212", "append": false }
    ],
    "form1[0].#subform[0].fkRuta2[0]": [
      { "value": "Anna Andersson", "append": false }
    ],
    "form1[0].#subform[0].enhetensKontaktuppgifter[0]": [
      { "value": "Vårdcentralen\nGatan 1, 12345 Stad\nTelefon: 08-1234", "append": false, "normalizeText": true }
    ],
    "form1[0].#subform[0].fkKryssruta2[0]": [
      { "value": "1", "append": false }
    ],
    "form1[0].#subform[0].fkKryssruta5[0]": [
      { "value": "Off", "append": false }
    ],
    "form1[0].#subform[0].fkDatum1[0]": [
      { "value": "2026-06-09", "append": false }
    ],
    "form1[0].#subform[0].diagnosBeskrivning1[0]": [
      {
        "value": "Kort beskrivning",
        "append": false,
        "appearance": "/Helv 9 Tf 0 g",
        "offset": 0
      }
    ],
    "form1[0].#subform[0].overflowField[0]": [
      { "value": "Diagnos", "append": true },
      { "value": "Mycket lång diagnosbeskrivning som inte fick plats på huvudsidan…\n", "append": true }
    ],
    "form1[0].#subform[0].signedDate[0]": [
      { "value": "2026-06-09", "append": false }
    ],
    "form1[0].#subform[0].issuerName[0]": [
      { "value": "Dr. Erik Svensson", "append": false }
    ]
  }
}
```
