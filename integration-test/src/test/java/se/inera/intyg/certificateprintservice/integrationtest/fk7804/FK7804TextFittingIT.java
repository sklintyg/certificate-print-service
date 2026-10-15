/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.certificateprintservice.integrationtest.fk7804;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintResponseDTO;
import se.inera.intyg.certificateprintservice.integrationtest.BaseIT;
import se.inera.intyg.certificateprintservice.integrationtest.util.PdfAssertionHelper;

/**
 * Verifies the dynamic, geometry-based text-fitting/overflow behavior that replaced the removed
 * {@code maxLength} API field: overflow decisions are now computed per field from its own AcroForm
 * box size and font size (see {@code FieldGeometryResolver}, {@code FieldTextLayout}, and {@code
 * FieldValueProcessor}).
 *
 * <p>Unlike {@link FK7804OverflowIT}, which only asserts that a valid, non-empty PDF is returned,
 * these tests inspect the actual response (page count and rendered text via {@link
 * PdfAssertionHelper}) so that a regression in the capacity calculation — e.g. a fixed length
 * creeping back in — would be caught.
 *
 * <p>The template ({@code fk7804-overflow-signed.pdf}) has no auto-sized (font size 0) or comb text
 * fields — every field uses a fixed 10pt ArialMT default appearance — so there is no scenario here
 * for the auto-sized/comb exclusions documented on {@code FieldGeometryResolver}; those exclusions
 * remain covered at the unit level ({@code FieldGeometryResolverTest}).
 */
class FK7804TextFittingIT extends BaseIT {

  private static final int BASE_PAGE_COUNT = 4;

  @Test
  void shallNotOverflowWhenTextExactlyFillsFieldCapacity() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request = setup.buildRequest(TestSetupFK7804Fields.fieldsWithMaxFillNoOverflow());

    final var pdfBytes = assertValidPdf(postCustom(request));

    assertEquals(
        BASE_PAGE_COUNT,
        PdfAssertionHelper.pageCount(pdfBytes),
        "Text sized to exactly fill the field must not add an overflow page");
    final var text = PdfAssertionHelper.extractText(pdfBytes);
    assertFalse(
        text.contains("Se fortsättningsblad"),
        "Text that fits must not be truncated with the overflow suffix");
  }

  @Test
  void shallOverflowWhenTextIsOneStepPastFieldCapacity() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request = setup.buildRequest(TestSetupFK7804Fields.fieldsWithMinimalOverflow());

    final var pdfBytes = assertValidPdf(postCustom(request));

    assertEquals(
        BASE_PAGE_COUNT + 1,
        PdfAssertionHelper.pageCount(pdfBytes),
        "Text one step past capacity must add exactly one overflow page");
    final var text = PdfAssertionHelper.extractText(pdfBytes);
    assertTrue(
        text.contains("Se fortsättningsblad"),
        "The primary field must be cut off with the overflow suffix");
    assertTrue(
        text.contains("Medicinsk behandling"),
        "The overflow page must contain the field's overflow label");
  }

  @Test
  void shallTruncateInPlaceWhenFieldHasNoOverflowConfig() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request =
        setup.buildRequest(TestSetupFK7804Fields.fieldsExceedingCapacityWithoutOverflowConfig());

    final var pdfBytes = assertValidPdf(postCustom(request));

    assertEquals(
        BASE_PAGE_COUNT,
        PdfAssertionHelper.pageCount(pdfBytes),
        "A field with no overflow config must not cause a new page to be added");
    final var text = PdfAssertionHelper.extractText(pdfBytes);
    assertTrue(text.contains("..."), "The oversized value must be truncated with an ellipsis");
  }

  @Test
  void shallJoinLinesInsteadOfTruncatingWhenFieldHasNoOverflowConfig() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request =
        setup.buildRequest(
            TestSetupFK7804Fields.fieldsWithLineBreaksInSingleLineFieldWithoutOverflow());

    final var pdfBytes = assertValidPdf(postCustom(request));

    final var text = PdfAssertionHelper.extractText(pdfBytes);
    assertTrue(
        text.contains("Hosta Feber Yrsel"),
        "Every line must be kept, joined by spaces, when the field has nowhere to overflow");
  }

  @Test
  void shallOverflowSingleLineFieldWithoutWrapping() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request =
        setup.buildRequest(TestSetupFK7804Fields.fieldsWithSingleLineFieldOverflow());

    final var pdfBytes = assertValidPdf(postCustom(request));

    assertEquals(
        BASE_PAGE_COUNT + 1,
        PdfAssertionHelper.pageCount(pdfBytes),
        "A single-line field wider than its box must still overflow to a new page");
    final var text = PdfAssertionHelper.extractText(pdfBytes);
    assertTrue(text.contains("..."), "The single-line field's value must be cut off");
    assertTrue(
        text.contains("Annan grund"), "The overflow page must contain the field's overflow label");
  }

  @Test
  void shallNotOverflowSingleLineFieldWhenTextFits() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request =
        setup.buildRequest(TestSetupFK7804Fields.fieldsWithSingleLineFieldFitting());

    final var pdfBytes = assertValidPdf(postCustom(request));

    assertEquals(
        BASE_PAGE_COUNT,
        PdfAssertionHelper.pageCount(pdfBytes),
        "A single-line field's text that fits within its width must not overflow");
  }

  @Test
  void shallKeepTwoDigitMonthsInPrognosFieldTooSmallForAnEllipsis() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request =
        setup.buildRequest(TestSetupFK7804Fields.fieldsWithTwoDigitPrognosManader());

    final var pdfBytes = assertValidPdf(postCustom(request));

    assertEquals(
        BASE_PAGE_COUNT,
        PdfAssertionHelper.pageCount(pdfBytes),
        "A field without overflow config must not add an overflow page");
    final var text = PdfAssertionHelper.extractText(pdfBytes);
    assertTrue(
        text.contains(TestSetupFK7804Constants.PROGNOS_MANADER_TWO_DIGITS),
        "The months value must be printed, not replaced by an ellipsis");
    assertFalse(text.contains("..."), "The months value must not be truncated");
  }

  @Test
  void shallComputeCapacityPerFieldRatherThanUsingAFixedLength() throws IOException {
    final var setup = new TestSetupFK7804();

    final var overflowingRequest =
        setup.buildRequest(TestSetupFK7804Fields.fieldsWithDynamicCapacityComparisonOverflowing());
    final var overflowingPages =
        PdfAssertionHelper.pageCount(assertValidPdf(postCustom(overflowingRequest)));

    final var fittingRequest =
        setup.buildRequest(TestSetupFK7804Fields.fieldsWithDynamicCapacityComparisonFitting());
    final var fittingPages =
        PdfAssertionHelper.pageCount(assertValidPdf(postCustom(fittingRequest)));

    assertEquals(
        BASE_PAGE_COUNT + 1,
        overflowingPages,
        "The same text length must overflow the smaller field (500.315x113.397pt)");
    assertEquals(
        BASE_PAGE_COUNT,
        fittingPages,
        "The identical text length must fit the taller field (500.315x177.183pt) at the same font"
            + " size, proving capacity is computed per field rather than from a fixed length");
  }

  @Test
  void shallSpanMultipleOverflowPagesWhenCombinedContentIsLargeEnough() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request =
        setup.buildRequest(TestSetupFK7804Fields.fieldsWithOverflowAcrossMultiplePages());

    final var pdfBytes = assertValidPdf(postCustom(request));

    assertEquals(
        BASE_PAGE_COUNT + 2,
        PdfAssertionHelper.pageCount(pdfBytes),
        "Combined overflow content larger than one overflow page's capacity must span two"
            + " overflow pages");
    final var text = PdfAssertionHelper.extractText(pdfBytes);
    assertTrue(
        text.contains("Funktionsnedsättning"), "First overflow entry's label must be present");
    assertTrue(
        text.contains("Aktivitetsbegränsning"), "Second overflow entry's label must be present");
  }

  @Test
  void shallWriteContactInformationVerbatimWhenTextFittingIsSkipped() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request =
        setup.buildRequest(TestSetupFK7804Fields.fieldsWithVerbatimContactInformation());

    final var pdfBytes = assertValidPdf(postCustom(request));

    final var text = PdfAssertionHelper.extractText(pdfBytes);
    assertTrue(
        text.contains("Forshaga"),
        "The end of the first line must survive — the value must not be truncated");
    assertTrue(text.contains("054121317"), "The last line must be printed");
    assertFalse(text.contains("..."), "A verbatim field must never be truncated with an ellipsis");
  }

  @Test
  void shallFitContactInformationWhenTextFittingIsNotSkipped() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request =
        setup.buildRequest(TestSetupFK7804Fields.fieldsWithFittedContactInformation());

    final var pdfBytes = assertValidPdf(postCustom(request));

    final var text = PdfAssertionHelper.extractText(pdfBytes);
    assertTrue(
        text.contains("..."),
        "Without the opt-out the generic rules must still truncate the oversized value");
  }

  private byte[] assertValidPdf(ResponseEntity<CustomPrintResponseDTO> response) {
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    final var rawData = response.getBody().getPdfData();
    assertNotNull(rawData);
    assertTrue(rawData.length > 0, "PDF response must not be empty");
    return Base64.getDecoder().decode(rawData);
  }
}
