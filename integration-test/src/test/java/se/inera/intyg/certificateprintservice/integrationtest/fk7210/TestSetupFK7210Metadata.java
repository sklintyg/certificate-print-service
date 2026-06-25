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
package se.inera.intyg.certificateprintservice.integrationtest.fk7210;

import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.RIGHT_MARGIN_TEXT;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.SIGNATURE_PAGE_INDEX;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.SIGNATURE_TAG_INDEX_WITH_ADDRESS;

import java.util.List;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.AccessibilityMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.AppearanceDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomTextDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.FontStyleEnumDTO;

public class TestSetupFK7210Metadata {

  public static CustomPdfMetadataDTO fullMetadata() {
    return CustomPdfMetadataDTO.builder()
        .customTexts(
            List.of(
                CustomTextDTO.builder()
                    .value(
                        "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.")
                    .appearance(new AppearanceDTO(8f, FontStyleEnumDTO.BOLD))
                    .x(173f)
                    .y(523f)
                    .pageIndex(SIGNATURE_PAGE_INDEX)
                    .tagIndex(SIGNATURE_TAG_INDEX_WITH_ADDRESS)
                    .build(),
                CustomTextDTO.builder()
                    .value("Intyget har skickats digitalt till Försäkringskassan")
                    .appearance(new AppearanceDTO(22f, null))
                    .x(40f)
                    .y(685f)
                    .pageIndex(0)
                    .tagIndex(4)
                    .build(),
                CustomTextDTO.builder()
                    .value("Du kan se intyget genom att logga in på 1177.se")
                    .appearance(new AppearanceDTO(16f, null))
                    .x(40f)
                    .y(665f)
                    .pageIndex(0)
                    .tagIndex(4)
                    .build()))
        .rightMarginText(RIGHT_MARGIN_TEXT)
        .accessibilityMetadata(new AccessibilityMetadataDTO("fk7210"))
        .addDraftWatermark(false)
        .build();
  }

  public static CustomPdfMetadataDTO metadataWithDraftWatermark() {
    return CustomPdfMetadataDTO.builder()
        .customTexts(
            List.of(
                CustomTextDTO.builder()
                    .value(
                        "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.")
                    .appearance(new AppearanceDTO(8f, FontStyleEnumDTO.BOLD))
                    .x(173f)
                    .y(523f)
                    .pageIndex(SIGNATURE_PAGE_INDEX)
                    .tagIndex(SIGNATURE_TAG_INDEX_WITH_ADDRESS)
                    .build()))
        .rightMarginText(RIGHT_MARGIN_TEXT)
        .accessibilityMetadata(new AccessibilityMetadataDTO("fk7210"))
        .addDraftWatermark(true)
        .build();
  }

  public static CustomPdfMetadataDTO metadataWithoutRightMarginText() {
    return CustomPdfMetadataDTO.builder()
        .customTexts(
            List.of(
                CustomTextDTO.builder()
                    .value(
                        "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.")
                    .appearance(new AppearanceDTO(8f, FontStyleEnumDTO.BOLD))
                    .x(173f)
                    .y(523f)
                    .pageIndex(SIGNATURE_PAGE_INDEX)
                    .tagIndex(SIGNATURE_TAG_INDEX_WITH_ADDRESS)
                    .build(),
                CustomTextDTO.builder()
                    .value("Intyget har skickats digitalt till Försäkringskassan")
                    .appearance(new AppearanceDTO(22f, null))
                    .x(40f)
                    .y(685f)
                    .pageIndex(0)
                    .build(),
                CustomTextDTO.builder()
                    .value("Du kan se intyget genom att logga in på 1177.se")
                    .appearance(new AppearanceDTO(16f, null))
                    .x(40f)
                    .y(665f)
                    .pageIndex(0)
                    .build()))
        .rightMarginText(null)
        .accessibilityMetadata(new AccessibilityMetadataDTO("fk7210"))
        .addDraftWatermark(false)
        .build();
  }

  public static CustomPdfMetadataDTO metadataWithoutCustomTexts() {
    return CustomPdfMetadataDTO.builder()
        .customTexts(List.of())
        .rightMarginText(RIGHT_MARGIN_TEXT)
        .accessibilityMetadata(new AccessibilityMetadataDTO("fk7210"))
        .addDraftWatermark(false)
        .build();
  }

  public static CustomPdfMetadataDTO metadataWithSignatureTextOnly() {
    return CustomPdfMetadataDTO.builder()
        .customTexts(
            List.of(
                CustomTextDTO.builder()
                    .value(
                        "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.")
                    .appearance(new AppearanceDTO(8f, FontStyleEnumDTO.BOLD))
                    .x(173f)
                    .y(523f)
                    .pageIndex(SIGNATURE_PAGE_INDEX)
                    .tagIndex(SIGNATURE_TAG_INDEX_WITH_ADDRESS)
                    .build()))
        .rightMarginText(RIGHT_MARGIN_TEXT)
        .accessibilityMetadata(new AccessibilityMetadataDTO("fk7210"))
        .addDraftWatermark(false)
        .build();
  }
}
