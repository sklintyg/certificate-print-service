# Certificate Print Service — Progress

## Overview

A Spring Boot service for generating PDFs for certificates, providing both general print and custom
PDF print endpoints. The app delegates PDF generation to pluggable generator modules via a shared
API.

## Architecture

| Module                     | Purpose                                                    |
|----------------------------|------------------------------------------------------------|
| `app`                      | REST API, request conversion, orchestration                |
| `pdf-generator-api`        | Shared API contracts and model types for PDF generation    |
| `pdfbox-pdf-generator`     | Custom PDF implementation using Apache PDFBox              |
| `playwright-pdf-generator` | Browser-based general certificate rendering via Playwright |
| `logging`                  | MDC and performance logging support                        |
| `integration-test`         | Dedicated integration test suite                           |

## Implemented Features

### Custom PDF Printing (`POST /api/print/custom`)

- Accepts `CustomPrintRequestDTO` with base64 template, metadata, and field map
- Returns generated PDF data as base64

### Metadata Support

- **Custom overlay texts** — positioned text rendered on top of the PDF
- **Right margin text** — rotated text along the right margin (e.g., intygsid)
- **Draft watermark** — "UTKAST" watermark overlay
- **Accessibility metadata** — sets PDF document title for screen readers

### AcroForm Field Filling

- Fills AcroForm fields by field ID with validation (throws on missing fields)
- Per-field appearance overrides (font, size)
- Text field height adjustment via offset
- Form flattening after fill

### Value Processing (`FieldValueProcessor`)

- **Line break removal** — optionally strips `\n` from the primary field value only
- **Truncation** — truncates with `...` suffix when value exceeds `maxLength` and no overflow is configured
- **Overflow splitting** — splits text at word boundary, writes primary with continuation suffix, accumulates remainder to overflow/continuation sheet field
- **Line breaks preserved in overflow** — `shouldRemoveLineBreaks` only applies to the primary field, never the overflow remainder

### Overflow / Continuation Sheet

- Multiple fields can overflow to the same continuation sheet field
- Overflow entries include a label prefix for each contributing field
- Insertion order is preserved via `LinkedHashMap`
- Supports both long suffix ("... Se fortsättningsblad!") and short suffix ("...") based on `maxLength` threshold

### Overlay Text Rendering

- Custom positioned text with configurable font size and style
- Continues accessibility structure ordering via `MaxMCIDExtractor`

## Test Coverage

### Unit Tests

- `AcroFormFillerTest` — field filling, appearance, line breaks, truncation
- `AcroFormFillerOverflowTest` — overflow splitting, suffix selection, accumulation
- `FieldValueProcessorTest` — all text processing scenarios independently

### Integration Tests

- **FK7210** — full field fill, draft watermark, metadata variations, minimal fields, error cases
- **FK7804 Overflow** — single overflow, multiple overflows to same sheet, line break handling with overflow

## Commit History

| Ticket     | Description                                                           |
|------------|-----------------------------------------------------------------------|
| `K1J-2210` | Overflow support, `FieldValueProcessor` refactor, integration tests   |
| `K1J-2211` | Appearance handling for fields                                        |
| `K1J-2190` | Custom print endpoint, offset, metadata, field styling, model updates |

## Not Yet Implemented

- `overflowPageIndex` — specifying which page contains the overflow field
- `addPageNumbers` — automatic page numbering
