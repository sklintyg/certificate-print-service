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
package se.inera.intyg.certificateprintservice.application.print.custom.converter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.inera.intyg.certificateprintservice.application.testdata.TestDataCustomPrintRequest.TEMPLATE_BYTES;
import static se.inera.intyg.certificateprintservice.application.testdata.TestDataCustomPrintRequest.VALID_TEMPLATE;
import static se.inera.intyg.certificateprintservice.application.testdata.TestDataCustomPrintRequest.buildRequest;
import static se.inera.intyg.certificateprintservice.application.testdata.TestDataCustomPrintRequest.fullMetadataBuilder;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfFieldDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintRequestDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CertificateStatus;

class CustomPdfRequestConverterTest {

  private final CustomPdfRequestConverter converter = new CustomPdfRequestConverter();

  @Test
  void shallDecodeTemplateFromBase64() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertArrayEquals(TEMPLATE_BYTES, result.getTemplate());
  }

  @Test
  void shallConvertMetadataStatus() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(CertificateStatus.SIGNED, result.getMetadata().getStatus());
  }

  @Test
  void shallConvertMetadataCertificateId() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals("cert-123", result.getMetadata().getCertificateId());
  }

  @Test
  void shallConvertMetadataIsSent() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertTrue(result.getMetadata().isSent());
  }

  @Test
  void shallConvertMetadataSentRecipientName() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals("Försäkringskassan", result.getMetadata().getSentRecipientName());
  }

  @Test
  void shallConvertFieldValue() {
    final var fieldOption = CustomPdfFieldDTO.builder().value("some-value").build();
    final var request =
        buildRequest(fullMetadataBuilder().build(), Map.of("field-id", fieldOption));

    final var result = converter.convert(request);

    assertEquals(1, result.getFields().size());
    assertEquals("some-value", result.getFields().get("field-id").getValue());
  }

  @Test
  void shallReturnEmptyMapWhenFieldsIsNull() {
    final var request =
        CustomPrintRequestDTO.builder()
            .template(VALID_TEMPLATE)
            .metadata(fullMetadataBuilder().build())
            .fields(null)
            .build();
    final var result = converter.convert(request);
    assertTrue(result.getFields().isEmpty());
  }
}
