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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.inera.intyg.certificateprintservice.integrationtest.util.PdfTestResultHelper.savePdf;

import java.io.IOException;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintResponseDTO;
import se.inera.intyg.certificateprintservice.integrationtest.BaseIT;

class FK7804OverflowIT extends BaseIT {

  @Test
  void shallReturnValidPdfWithAllFieldsAndOverflow() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request = setup.buildRequest(TestSetupFK7804Fields.allFields());

    final var response = postCustom(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    final var pdfBytes = assertValidPdf(response);
    savePdf(pdfBytes, "FK7804_ALL_FIELDS");
  }

  @Test
  void shallReturnValidPdfWithSingleFieldOverflow() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request = setup.buildRequest(TestSetupFK7804Fields.fieldsWithSingleOverflow());

    final var response = postCustom(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    final var pdfBytes = assertValidPdf(response);
    savePdf(pdfBytes, "FK7804_SINGLE_OVERFLOW");
  }

  @Test
  void shallReturnValidPdfWithMultipleFieldsOverflowingToSameSheet() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request = setup.buildRequest(TestSetupFK7804Fields.fieldsWithMultipleOverflows());

    final var response = postCustom(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    final var pdfBytes = assertValidPdf(response);
    savePdf(pdfBytes, "FK7804_MULTIPLE_OVERFLOWS");
  }

  @Test
  void shallReturnValidPdfWithOverflowAndLineBreaksRemovedFromPrimary() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request = setup.buildRequest(TestSetupFK7804Fields.fieldsWithOverflowAndLineBreaks());

    final var response = postCustom(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    final var pdfBytes = assertValidPdf(response);
    savePdf(pdfBytes, "FK7804_OVERFLOW_LINEBREAKS_REMOVED_PRIMARY");
  }

  @Test
  void shallReturnValidPdfWithOverflowPreservingLineBreaks() throws IOException {
    final var setup = new TestSetupFK7804();
    final var request =
        setup.buildRequest(TestSetupFK7804Fields.fieldsWithOverflowNoLineBreakRemoval());

    final var response = postCustom(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    final var pdfBytes = assertValidPdf(response);
    savePdf(pdfBytes, "FK7804_OVERFLOW_LINEBREAKS_PRESERVED");
  }

  private byte[] assertValidPdf(ResponseEntity<CustomPrintResponseDTO> response) {
    assertNotNull(response.getBody());
    final var rawData = response.getBody().getPdfData();
    assertNotNull(rawData);
    assertTrue(rawData.length > 0, "PDF response must not be empty");
    return Base64.getDecoder().decode(rawData);
  }
}
