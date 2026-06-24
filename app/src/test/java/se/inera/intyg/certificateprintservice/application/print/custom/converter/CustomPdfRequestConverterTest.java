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
import static org.junit.jupiter.api.Assertions.assertNull;
import static se.inera.intyg.certificateprintservice.application.testdata.TestDataCustomPrintRequest.TEMPLATE_BYTES;
import static se.inera.intyg.certificateprintservice.application.testdata.TestDataCustomPrintRequest.buildRequest;
import static se.inera.intyg.certificateprintservice.application.testdata.TestDataCustomPrintRequest.fullMetadataBuilder;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfFieldDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.PersonIdConfigDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.FontStyle;

class CustomPdfRequestConverterTest {

  private final CustomPdfRequestConverter converter = new CustomPdfRequestConverter();

  @Test
  void shallDecodeTemplateFromBase64() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertArrayEquals(TEMPLATE_BYTES, result.template());
  }

  @Test
  void shallConvertCustomTextListSize() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(3, result.metadata().customTextList().size());
  }

  @Test
  void shallConvertCustomTextValue() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(
        "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.",
        result.metadata().customTextList().getFirst().value());
  }

  @Test
  void shallConvertCustomTextX() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(100f, result.metadata().customTextList().getFirst().x());
  }

  @Test
  void shallConvertCustomTextY() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(200f, result.metadata().customTextList().getFirst().y());
  }

  @Test
  void shallConvertCustomTextPageIndex() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(0, result.metadata().customTextList().getFirst().pageIndex());
  }

  @Test
  void shallConvertCustomTextTagIndex() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(5, result.metadata().customTextList().getFirst().tagIndex());
  }

  @Test
  void shallConvertCustomTextAppearanceFontSize() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(12f, result.metadata().customTextList().getFirst().appearance().fontSize());
  }

  @Test
  void shallConvertCustomTextAppearanceStyleNormal() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(
        FontStyle.NORMAL, result.metadata().customTextList().getFirst().appearance().style());
  }

  @Test
  void shallConvertCustomTextAppearanceStyleBold() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(FontStyle.BOLD, result.metadata().customTextList().get(1).appearance().style());
  }

  @Test
  void shallConvertRightMarginText() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(
        "Intygsid: 8996d3d8-cb67-4602-b6a9-81dee33616ce. Intyget är utskrivet från Webcert.",
        result.metadata().rightMarginText());
  }

  @Test
  void shallConvertAccessibilityMetadataTitle() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals("fk7210", result.metadata().accessibilityMetadata().title());
  }

  @Test
  void shallConvertAddDraftWatermark() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertFalse(result.metadata().addDraftWatermark());
  }

  @Test
  void shallConvertOverflowPageIndex() {
    final var request =
        buildRequest(fullMetadataBuilder().overflowPageIndex(3).build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(3, result.metadata().overflowPageIndex());
  }

  @Test
  void shallConvertFieldValue() {
    final var request =
        buildRequest(
            fullMetadataBuilder().build(),
            Map.of("field-id", CustomPdfFieldDTO.builder().value("some-value").offset(5).build()));
    final var result = converter.convert(request);
    assertEquals("some-value", result.fields().get("field-id").value());
    assertEquals(5, result.fields().get("field-id").offset());
  }

  @Test
  void shallConvertPersonIdFieldId() {
    final var personId =
        PersonIdConfigDTO.builder()
            .fieldId("form1[0].#subform[0].flt_txtPersonNr[0]")
            .value("198012121234")
            .build();
    final var request =
        buildRequest(fullMetadataBuilder().personId(personId).build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals(
        "form1[0].#subform[0].flt_txtPersonNr[0]", result.metadata().personIdConfig().fieldId());
  }

  @Test
  void shallConvertPersonIdValue() {
    final var personId =
        PersonIdConfigDTO.builder()
            .fieldId("form1[0].#subform[0].flt_txtPersonNr[0]")
            .value("198012121234")
            .build();
    final var request =
        buildRequest(fullMetadataBuilder().personId(personId).build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertEquals("198012121234", result.metadata().personIdConfig().value());
  }

  @Test
  void shallConvertPersonIdAsNullWhenNotProvided() {
    final var request = buildRequest(fullMetadataBuilder().build(), Collections.emptyMap());
    final var result = converter.convert(request);
    assertNull(result.metadata().personIdConfig());
  }
}
