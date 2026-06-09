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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.certificateprintservice.application.print.converter.FillPdfRequestConverter;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.CertificateStatusDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.FillPdfRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.FillPdfResponseDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.PdfMetadataOptionsDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.FillPdfGenerator;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.fill.FillPdf;

@ExtendWith(MockitoExtension.class)
class FillPdfServiceTest {

  private static final String VALID_TEMPLATE =
      Base64.getEncoder().encodeToString("pdf-bytes".getBytes(StandardCharsets.UTF_8));
  private static final PdfMetadataOptionsDTO VALID_METADATA =
      PdfMetadataOptionsDTO.builder()
          .status(CertificateStatusDTO.DRAFT)
          .certificateId("cert-id")
          .patientId("191212121212")
          .build();
  private static final FillPdfRequestDTO VALID_REQUEST =
      FillPdfRequestDTO.builder()
          .template(VALID_TEMPLATE)
          .metadata(VALID_METADATA)
          .fields(Collections.emptyMap())
          .build();

  @Mock FillPdfGenerator fillPdfGenerator;
  @Mock FillPdfRequestConverter fillPdfRequestConverter;
  @InjectMocks FillPdfService fillPdfService;

  @Test
  void shallThrowIfRequestIsNull() {
    assertThrows(IllegalArgumentException.class, () -> fillPdfService.fill(null));
  }

  @Test
  void shallThrowIfTemplateIsNull() {
    final var request =
        FillPdfRequestDTO.builder()
            .metadata(VALID_METADATA)
            .fields(Collections.emptyMap())
            .build();
    final var ex =
        assertThrows(IllegalArgumentException.class, () -> fillPdfService.fill(request));
    assertEquals("Invalid request - Missing required parameter template", ex.getMessage());
  }

  @Test
  void shallThrowIfTemplateIsBlank() {
    final var request =
        FillPdfRequestDTO.builder()
            .template("  ")
            .metadata(VALID_METADATA)
            .fields(Collections.emptyMap())
            .build();
    final var ex =
        assertThrows(IllegalArgumentException.class, () -> fillPdfService.fill(request));
    assertEquals("Invalid request - Missing required parameter template", ex.getMessage());
  }

  @Test
  void shallThrowIfMetadataIsNull() {
    final var request =
        FillPdfRequestDTO.builder()
            .template(VALID_TEMPLATE)
            .fields(Collections.emptyMap())
            .build();
    final var ex =
        assertThrows(IllegalArgumentException.class, () -> fillPdfService.fill(request));
    assertEquals("Invalid request - Missing required parameter metadata", ex.getMessage());
  }

  @Test
  void shallThrowIfFieldsIsNull() {
    final var request =
        FillPdfRequestDTO.builder().template(VALID_TEMPLATE).metadata(VALID_METADATA).build();
    final var ex =
        assertThrows(IllegalArgumentException.class, () -> fillPdfService.fill(request));
    assertEquals("Invalid request - Missing required parameter fields", ex.getMessage());
  }

  @Test
  void shallThrowIfCertificateIdIsMissing() {
    final var metadata =
        PdfMetadataOptionsDTO.builder()
            .status(CertificateStatusDTO.DRAFT)
            .patientId("191212121212")
            .build();
    final var request =
        FillPdfRequestDTO.builder()
            .template(VALID_TEMPLATE)
            .metadata(metadata)
            .fields(Collections.emptyMap())
            .build();
    final var ex =
        assertThrows(IllegalArgumentException.class, () -> fillPdfService.fill(request));
    assertEquals(
        "Invalid request - Missing required metadata parameter certificateId", ex.getMessage());
  }

  @Test
  void shallThrowIfPatientIdIsMissing() {
    final var metadata =
        PdfMetadataOptionsDTO.builder()
            .status(CertificateStatusDTO.DRAFT)
            .certificateId("cert-id")
            .build();
    final var request =
        FillPdfRequestDTO.builder()
            .template(VALID_TEMPLATE)
            .metadata(metadata)
            .fields(Collections.emptyMap())
            .build();
    final var ex =
        assertThrows(IllegalArgumentException.class, () -> fillPdfService.fill(request));
    assertEquals(
        "Invalid request - Missing required metadata parameter patientId", ex.getMessage());
  }

  @Test
  void shallThrowIfSentAndSentRecipientNameIsMissing() {
    final var metadata =
        PdfMetadataOptionsDTO.builder()
            .status(CertificateStatusDTO.SIGNED)
            .certificateId("cert-id")
            .patientId("191212121212")
            .isSent(true)
            .signedDateFieldId("field-id")
            .build();
    final var request =
        FillPdfRequestDTO.builder()
            .template(VALID_TEMPLATE)
            .metadata(metadata)
            .fields(Collections.emptyMap())
            .build();
    final var ex =
        assertThrows(IllegalArgumentException.class, () -> fillPdfService.fill(request));
    assertEquals(
        "Invalid request - sentRecipientName is required when isSent is true", ex.getMessage());
  }

  @Test
  void shallThrowIfStatusSignedAndSignedDateFieldIdIsMissing() {
    final var metadata =
        PdfMetadataOptionsDTO.builder()
            .status(CertificateStatusDTO.SIGNED)
            .certificateId("cert-id")
            .patientId("191212121212")
            .build();
    final var request =
        FillPdfRequestDTO.builder()
            .template(VALID_TEMPLATE)
            .metadata(metadata)
            .fields(Collections.emptyMap())
            .build();
    final var ex =
        assertThrows(IllegalArgumentException.class, () -> fillPdfService.fill(request));
    assertEquals(
        "Invalid request - signedDateFieldId is required when status is SIGNED", ex.getMessage());
  }

  @Test
  void shallThrowIfOverflowPageIndexSetAndPatientIdFieldIdIsMissing() {
    final var metadata =
        PdfMetadataOptionsDTO.builder()
            .status(CertificateStatusDTO.DRAFT)
            .certificateId("cert-id")
            .patientId("191212121212")
            .overflowPageIndex(2)
            .build();
    final var request =
        FillPdfRequestDTO.builder()
            .template(VALID_TEMPLATE)
            .metadata(metadata)
            .fields(Collections.emptyMap())
            .build();
    final var ex =
        assertThrows(IllegalArgumentException.class, () -> fillPdfService.fill(request));
    assertEquals(
        "Invalid request - patientIdFieldId is required when overflowPageIndex is set",
        ex.getMessage());
  }

  @Test
  void shallReturnFillPdfResponse() {
    final var expectedPdfData = "filled-pdf".getBytes(StandardCharsets.UTF_8);
    final var expectedResponse = FillPdfResponseDTO.builder().pdfData(expectedPdfData).build();
    final var domain = FillPdf.builder().build();

    doReturn(domain).when(fillPdfRequestConverter).convert(VALID_REQUEST);
    doReturn(expectedPdfData).when(fillPdfGenerator).fill(domain);

    final var actual = fillPdfService.fill(VALID_REQUEST);
    assertArrayEquals(expectedResponse.getPdfData(), actual.getPdfData());
  }
}
