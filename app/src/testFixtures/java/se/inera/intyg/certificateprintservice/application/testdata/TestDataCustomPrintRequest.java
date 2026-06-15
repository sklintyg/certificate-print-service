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
package se.inera.intyg.certificateprintservice.application.testdata;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.AccessibilityMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.AppearanceDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfFieldDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomTextDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.FontStyleEnumDTO;

public class TestDataCustomPrintRequest {

  public static final byte[] TEMPLATE_BYTES = "pdf-template".getBytes(StandardCharsets.UTF_8);
  public static final String VALID_TEMPLATE = Base64.getEncoder().encodeToString(TEMPLATE_BYTES);

  private TestDataCustomPrintRequest() {
    throw new IllegalStateException("Utility class");
  }

  public static CustomPdfMetadataDTO.CustomPdfMetadataDTOBuilder validMetadataBuilder() {
    return CustomPdfMetadataDTO.builder()
        .customTexts(
            List.of(
                CustomTextDTO.builder()
                    .value("UTKAST")
                    .x(0f)
                    .y(0f)
                    .appearance(new AppearanceDTO(22f, null))
                    .pageIndex(0)
                    .build()))
        .accessibilityMetadata(new AccessibilityMetadataDTO("Intyg-om-graviditet-2026-06-11"))
        .addDraftWatermark(true);
  }

  public static CustomPdfMetadataDTO.CustomPdfMetadataDTOBuilder fullMetadataBuilder() {
    return CustomPdfMetadataDTO.builder()
        .customTexts(
            List.of(
                CustomTextDTO.builder()
                    .value(
                        "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.")
                    .appearance(new AppearanceDTO(12f, null))
                    .x(100f)
                    .y(200f)
                    .pageIndex(0)
                    .tagIndex(5)
                    .build(),
                CustomTextDTO.builder()
                    .value("Intyget har skickats digitalt till Försäkringskassan")
                    .appearance(new AppearanceDTO(22f, FontStyleEnumDTO.BOLD))
                    .x(0f)
                    .y(0f)
                    .pageIndex(0)
                    .build(),
                CustomTextDTO.builder()
                    .value("Du kan se intyget genom att logga in på 1177.se")
                    .appearance(new AppearanceDTO(16f, null))
                    .x(0f)
                    .y(0f)
                    .pageIndex(0)
                    .build()))
        .rightMarginText(
            "Intygsid: 8996d3d8-cb67-4602-b6a9-81dee33616ce. Intyget är utskrivet från Webcert.")
        .accessibilityMetadata(new AccessibilityMetadataDTO("fk7210"))
        .addDraftWatermark(false);
  }

  public static CustomPrintRequestDTO buildRequest(
      CustomPdfMetadataDTO metadata, Map<String, CustomPdfFieldDTO> fields) {
    return CustomPrintRequestDTO.builder()
        .template(VALID_TEMPLATE)
        .metadata(metadata)
        .fields(fields)
        .build();
  }

  public static CustomPrintRequestDTO.CustomPrintRequestDTOBuilder validRequestBuilder() {
    return CustomPrintRequestDTO.builder()
        .template(VALID_TEMPLATE)
        .metadata(validMetadataBuilder().build())
        .fields(Collections.emptyMap());
  }

  public static CustomPrintRequestDTO validRequest() {
    return validRequestBuilder().build();
  }
}
