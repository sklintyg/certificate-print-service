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
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

class CustomPdfRequestConverterTest {

  private final CustomPdfRequestConverter converter = new CustomPdfRequestConverter();

  @Test
  void shallDecodeTemplateFromBase64() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertArrayEquals(TEMPLATE_BYTES, result.getTemplate());
  }

  @Test
  void shallConvertMetadataWaterMarks() {
    // TODO: assert all values for converted metadata
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertNotNull(result.getMetadata().getCustomTextList());
    assertEquals(3, result.getMetadata().getCustomTextList().size());
    assertEquals(
        "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.",
        result.getMetadata().getCustomTextList().get(0).getValue());
  }

  @Test
  void shallConvertMetadataRightMarginText() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertNotNull(result.getMetadata().getRightMarginText());
    assertTrue(result.getMetadata().getRightMarginText().contains("Intygsid:"));
    assertTrue(result.getMetadata().getRightMarginText().contains("Webcert"));
  }

  @Test
  void shallConvertMetadataAccessibilityMetadata() {
    // TODO: assert content of getAccessibilityMetadata value

    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertNotNull(result.getMetadata().getAccessibilityMetadata());
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
