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
package se.inera.intyg.certificateprintservice.integrationtest.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.inera.intyg.certificateprintservice.integrationtest.util.PdfTestResultHelper.savePdf;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.AccessibilityMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfFieldDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintResponseDTO;
import se.inera.intyg.certificateprintservice.integrationtest.BaseIT;
import se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210;
import se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants;

class CustomPrintIT extends BaseIT {

  @Test
  void shallReturnValidPdfWithAllFieldsAndFullMetadata() throws IOException {
    final var setup = new TestSetupFK7210();
    final var request = setup.loadTemplateAsBase64(setup.fk7210WithAllFields().build());

    final var response = postCustom(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    final var pdfBytes = assertValidPdf(response);
    savePdf(pdfBytes, "CustomPrint_ALL_FIELDS_FULL_METADATA");
  }

  @Test
  void shallReturnValidPdfWithDraftWatermark() throws IOException {
    final var setup = new TestSetupFK7210();
    final var request = setup.loadTemplateAsBase64(setup.fk7210WithDraftWatermark().build());

    final var response = postCustom(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    final var pdfBytes = assertValidPdf(response);
    savePdf(pdfBytes, "CustomPrint_DRAFT_WATERMARK");
  }

  @Test
  void shallReturnValidPdfWithoutRightMarginText() throws IOException {
    final var setup = new TestSetupFK7210();
    final var request = setup.loadTemplateAsBase64(setup.fk7210WithoutRightMarginText().build());

    final var response = postCustom(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    final var pdfBytes = assertValidPdf(response);
    savePdf(pdfBytes, "CustomPrint_WITHOUT_RIGHT_MARGIN_TEXT");
  }

  @Test
  void shallReturnValidPdfWithoutCustomTexts() throws IOException {
    final var setup = new TestSetupFK7210();
    final var request = setup.loadTemplateAsBase64(setup.fk7210WithoutCustomTexts().build());

    final var response = postCustom(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    final var pdfBytes = assertValidPdf(response);
    savePdf(pdfBytes, "CustomPrint_WITHOUT_CUSTOM_TEXTS");
  }

  @Test
  void shallReturnValidPdfWithSignatureTextOnly() throws IOException {
    final var setup = new TestSetupFK7210();
    final var request = setup.loadTemplateAsBase64(setup.fk7210WithSignatureTextOnly().build());

    final var response = postCustom(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    final var pdfBytes = assertValidPdf(response);
    savePdf(pdfBytes, "CustomPrint_SIGNATURE_TEXT_ONLY");
  }

  @Test
  void shallReturnValidPdfWithMinimalFields() throws IOException {
    final var setup = new TestSetupFK7210();
    final var request = setup.loadTemplateAsBase64(setup.fk7210WithMinimalFields().build());

    final var response = postCustom(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    final var pdfBytes = assertValidPdf(response);
    savePdf(pdfBytes, "CustomPrint_MINIMAL_FIELDS");
  }

  @Test
  void shallReturnBadRequestWhenTemplateIsMissing() {
    final var request =
        CustomPrintRequestDTO.builder()
            .template(null)
            .metadata(
                CustomPdfMetadataDTO.builder()
                    .customTexts(java.util.List.of())
                    .accessibilityMetadata(new AccessibilityMetadataDTO("fk7210"))
                    .build())
            .fields(
                Map.of(
                    TestSetupFK7210Constants.PATIENT_ID_FIELD_ID,
                    CustomPdfFieldDTO.builder().value(TestSetupFK7210Constants.PATIENT_ID).build()))
            .build();

    final var response = postCustomError(request);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void shallReturnBadRequestWhenFieldsAreEmpty() throws IOException {
    final var setup = new TestSetupFK7210();
    final var base = setup.loadTemplateAsBase64(setup.fk7210WithAllFields().build());
    final var request =
        CustomPrintRequestDTO.builder()
            .template(base.getTemplate())
            .metadata(base.getMetadata())
            .fields(Map.of())
            .build();

    final var response = postCustomError(request);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  /**
   * Decodes the base64-encoded pdfData from the response (PdfBoxPdfGenerator base64-encodes its
   * output), returns the decoded bytes for saving.
   */
  private byte[] assertValidPdf(ResponseEntity<CustomPrintResponseDTO> response) {
    assertNotNull(response.getBody());
    final var rawData = response.getBody().getPdfData();
    assertNotNull(rawData);
    assertTrue(rawData.length > 0, "PDF response must not be empty");

    return Base64.getDecoder().decode(rawData);
  }
}
