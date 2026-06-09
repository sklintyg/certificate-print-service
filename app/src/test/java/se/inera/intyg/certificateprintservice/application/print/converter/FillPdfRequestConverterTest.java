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
package se.inera.intyg.certificateprintservice.application.print.converter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.CertificateStatusDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.FillPdfRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.PdfFieldFillOptionsDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.PdfMetadataOptionsDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.fill.CertificateStatus;

class FillPdfRequestConverterTest {

  private static final byte[] TEMPLATE_BYTES = "pdf-template".getBytes(StandardCharsets.UTF_8);
  private static final String TEMPLATE_BASE64 =
      Base64.getEncoder().encodeToString(TEMPLATE_BYTES);

  private final FillPdfRequestConverter converter = new FillPdfRequestConverter();

  @Test
  void shallDecodeTemplateFromBase64() {
    final var request = buildRequest(buildMetadata(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertArrayEquals(TEMPLATE_BYTES, result.getTemplate());
  }

  @Test
  void shallConvertMetadataStatus() {
    final var request = buildRequest(buildMetadata(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(CertificateStatus.SIGNED, result.getMetadata().getStatus());
  }

  @Test
  void shallConvertMetadataCertificateId() {
    final var request = buildRequest(buildMetadata(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals("cert-123", result.getMetadata().getCertificateId());
  }

  @Test
  void shallConvertMetadataPatientId() {
    final var request = buildRequest(buildMetadata(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals("191212121212", result.getMetadata().getPatientId());
  }

  @Test
  void shallConvertMetadataIsSent() {
    final var request = buildRequest(buildMetadata(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertTrue(result.getMetadata().isSent());
  }

  @Test
  void shallConvertMetadataSentRecipientName() {
    final var request = buildRequest(buildMetadata(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals("Försäkringskassan", result.getMetadata().getSentRecipientName());
  }

  @Test
  void shallConvertFields() {
    final var fieldOption =
        PdfFieldFillOptionsDTO.builder()
            .value("some-value")
            .append(false)
            .appearance("/Helv 9 Tf 0 g")
            .offset(5)
            .normalizeText(true)
            .build();
    final var fields = Map.of("field-id", List.of(fieldOption));
    final var request = buildRequest(buildMetadata(), fields);

    final var result = converter.convert(request);

    assertEquals(1, result.getFields().size());
    final var convertedOption = result.getFields().get("field-id").get(0);
    assertEquals("some-value", convertedOption.getValue());
    assertEquals("/Helv 9 Tf 0 g", convertedOption.getAppearance());
    assertEquals(5, convertedOption.getOffset());
    assertTrue(convertedOption.isNormalizeText());
  }

  @Test
  void shallReturnEmptyMapWhenFieldsIsNull() {
    final var request =
        FillPdfRequestDTO.builder()
            .template(TEMPLATE_BASE64)
            .metadata(buildMetadata())
            .fields(null)
            .build();
    final var result = converter.convert(request);
    assertTrue(result.getFields().isEmpty());
  }

  @Test
  void shallDefaultUntaggedWatermarksToEmptyListWhenNull() {
    final var metadata =
        PdfMetadataOptionsDTO.builder()
            .status(CertificateStatusDTO.DRAFT)
            .certificateId("cert-123")
            .patientId("191212121212")
            .untaggedWatermarks(null)
            .build();
    final var request = buildRequest(metadata, Collections.emptyMap());
    final var result = converter.convert(request);
    assertTrue(result.getMetadata().getUntaggedWatermarks().isEmpty());
  }

  private PdfMetadataOptionsDTO buildMetadata() {
    return PdfMetadataOptionsDTO.builder()
        .status(CertificateStatusDTO.SIGNED)
        .isSent(true)
        .sentRecipientName("Försäkringskassan")
        .availableForCitizen(true)
        .certificateId("cert-123")
        .additionalInfoText("Webcert 2.0")
        .addPageNumbers(true)
        .overflowPageIndex(2)
        .signaturePageIndex(0)
        .signatureTagIndex(5)
        .signedDateFieldId("signed-date-field")
        .patientId("191212121212")
        .patientIdFieldId("patient-id-field")
        .startMcid(100)
        .untaggedWatermarks(List.of("UTKAST"))
        .build();
  }

  private FillPdfRequestDTO buildRequest(
      PdfMetadataOptionsDTO metadata,
      Map<String, List<PdfFieldFillOptionsDTO>> fields) {
    return FillPdfRequestDTO.builder()
        .template(TEMPLATE_BASE64)
        .metadata(metadata)
        .fields(fields)
        .build();
  }
}
