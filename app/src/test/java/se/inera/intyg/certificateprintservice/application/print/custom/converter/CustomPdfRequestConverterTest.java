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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.FontStyle;

class CustomPdfRequestConverterTest {

  private final CustomPdfRequestConverter converter = new CustomPdfRequestConverter();

  @Test
  void shallDecodeTemplateFromBase64() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertArrayEquals(TEMPLATE_BYTES, result.getTemplate());
  }

  @Test
  void shallConvertCustomTextListSize() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(3, result.getMetadata().getCustomTextList().size());
  }

  @Test
  void shallConvertCustomTextValue() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(
        "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.",
        result.getMetadata().getCustomTextList().getFirst().value());
  }

  @Test
  void shallConvertCustomTextX() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(100f, result.getMetadata().getCustomTextList().getFirst().x());
  }

  @Test
  void shallConvertCustomTextY() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(200f, result.getMetadata().getCustomTextList().getFirst().y());
  }

  @Test
  void shallConvertCustomTextPageIndex() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(0, result.getMetadata().getCustomTextList().getFirst().pageIndex());
  }

  @Test
  void shallConvertCustomTextTagIndex() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(5, result.getMetadata().getCustomTextList().getFirst().tagIndex());
  }

  @Test
  void shallConvertCustomTextAppearanceFontSize() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(
        12f, result.getMetadata().getCustomTextList().getFirst().appearance().getFontSize());
  }

  @Test
  void shallConvertCustomTextAppearanceStyleNormal() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(
        FontStyle.NORMAL,
        result.getMetadata().getCustomTextList().getFirst().appearance().getStyle());
  }

  @Test
  void shallConvertCustomTextAppearanceStyleBold() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(
        FontStyle.BOLD, result.getMetadata().getCustomTextList().get(1).appearance().getStyle());
  }

  @Test
  void shallConvertRightMarginText() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(
        "Intygsid: 8996d3d8-cb67-4602-b6a9-81dee33616ce. Intyget är utskrivet från Webcert.",
        result.getMetadata().getRightMarginText());
  }

  @Test
  void shallConvertAccessibilityMetadataTitle() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals("fk7210", result.getMetadata().getAccessibilityMetadata().getTitle());
  }

  @Test
  void shallConvertAddDraftWatermark() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertFalse(result.getMetadata().isAddDraftWatermark());
  }

  @Test
  void shallConvertFieldValue() {
    final var request =
        buildRequest(
            fullMetadataBuilder().build(),
            Map.of("field-id", CustomPdfFieldDTO.builder().value("some-value").build()));
    final var result = converter.convert(request);
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
