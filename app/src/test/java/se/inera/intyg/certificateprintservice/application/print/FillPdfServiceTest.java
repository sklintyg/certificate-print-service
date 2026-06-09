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
package se.inera.intyg.certificateprintservice.application.print;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static se.inera.intyg.certificateprintservice.application.testdata.TestDataFillPdfRequest.validMetadataBuilder;
import static se.inera.intyg.certificateprintservice.application.testdata.TestDataFillPdfRequest.validRequest;
import static se.inera.intyg.certificateprintservice.application.testdata.TestDataFillPdfRequest.validRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.certificateprintservice.application.print.converter.FillPdfRequestConverter;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.CertificateStatusDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.FillPdfResponseDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.FillPdfGenerator;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.fill.FillPdf;

@ExtendWith(MockitoExtension.class)
class FillPdfServiceTest {

  @Mock FillPdfGenerator fillPdfGenerator;
  @Mock FillPdfRequestConverter fillPdfRequestConverter;
  @InjectMocks FillPdfService fillPdfService;

  @Test
  void shallReturnFilledPdf() {
    final var request = validRequest();
    final var domain = FillPdf.builder().build();
    final var expectedPdfData = "filled-pdf".getBytes(StandardCharsets.UTF_8);

    doReturn(domain).when(fillPdfRequestConverter).convert(request);
    doReturn(expectedPdfData).when(fillPdfGenerator).fill(domain);

    final var actual = fillPdfService.fill(request);
    assertArrayEquals(expectedPdfData, actual.getPdfData());
  }

  @Test
  void shallThrowWhenSentButSentRecipientNameIsMissing() {
    final var metadata = validMetadataBuilder().isSent(true).build();
    final var request = validRequestBuilder().metadata(metadata).build();

    final var ex = assertThrows(IllegalArgumentException.class, () -> fillPdfService.fill(request));
    assertEquals(
        "Invalid request - sentRecipientName is required when isSent is true", ex.getMessage());
  }

  @Test
  void shallThrowWhenStatusIsSignedButSignedDateFieldIdIsMissing() {
    final var metadata =
        validMetadataBuilder().status(CertificateStatusDTO.SIGNED).build();
    final var request = validRequestBuilder().metadata(metadata).build();

    final var ex = assertThrows(IllegalArgumentException.class, () -> fillPdfService.fill(request));
    assertEquals(
        "Invalid request - signedDateFieldId is required when status is SIGNED", ex.getMessage());
  }
}
