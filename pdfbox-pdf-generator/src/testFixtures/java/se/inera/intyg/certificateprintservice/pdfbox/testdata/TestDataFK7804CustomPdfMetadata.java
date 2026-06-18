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
package se.inera.intyg.certificateprintservice.pdfbox.testdata;

import java.util.List;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.AccessibilityMetadata;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.Appearance;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfMetadata;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.FontStyle;

public class TestDataFK7804CustomPdfMetadata {

  public static final String RIGHT_MARGIN_TEXT = "Webcert";
  public static final String TITLE = "fk7804";

  private TestDataFK7804CustomPdfMetadata() {
    throw new IllegalStateException("Utility class");
  }

  public static CustomPdfMetadata metadataWithDraftWatermark() {
    return CustomPdfMetadata.builder()
        .accessibilityMetadata(AccessibilityMetadata.builder().title(TITLE).build())
        .addDraftWatermark(true)
        .build();
  }

  public static CustomPdfMetadata metadataWithCustomTextAndMargin() {
    return CustomPdfMetadata.builder()
        .customTextList(
            List.of(
                CustomText.builder()
                    .value(
                        "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.")
                    .x(100f)
                    .y(50f)
                    .appearance(Appearance.builder().fontSize(12f).build())
                    .pageIndex(3)
                    .tagIndex(10)
                    .build()))
        .rightMarginText(RIGHT_MARGIN_TEXT)
        .accessibilityMetadata(AccessibilityMetadata.builder().title(TITLE).build())
        .build();
  }

  public static CustomPdfMetadata metadataWithAllCustomTextsAndMargin() {
    return CustomPdfMetadata.builder()
        .customTextList(
            List.of(
                CustomText.builder()
                    .value(
                        "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.")
                    .x(100f)
                    .y(50f)
                    .appearance(Appearance.builder().fontSize(12f).build())
                    .pageIndex(0)
                    .tagIndex(10)
                    .build(),
                CustomText.builder()
                    .value("Intyget har skickats digitalt till Försäkringskassan")
                    .x(0f)
                    .y(0f)
                    .appearance(Appearance.builder().fontSize(16f).style(FontStyle.BOLD).build())
                    .pageIndex(0)
                    .build(),
                CustomText.builder()
                    .value("Du kan se intyget genom att logga in på 1177.se")
                    .x(0f)
                    .y(0f)
                    .appearance(Appearance.builder().fontSize(14f).build())
                    .pageIndex(0)
                    .build()))
        .rightMarginText(RIGHT_MARGIN_TEXT)
        .accessibilityMetadata(AccessibilityMetadata.builder().title(TITLE).build())
        .addDraftWatermark(false)
        .build();
  }
}
