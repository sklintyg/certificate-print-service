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
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfMetadata;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomText;

public class TestDataFK7210CustomPdfMetadata {

  public static final String RIGHT_MARGIN_TEXT = "Webcert";
  public static final String TITLE = "fk7210";

  private TestDataFK7210CustomPdfMetadata() {
    throw new IllegalStateException("Utility class");
  }

  public static CustomPdfMetadata draftMetadata() {
    return CustomPdfMetadata.builder()
        .accessibilityMetadata(AccessibilityMetadata.builder().title(TITLE).build())
        .addDraftWatermark(true)
        .build();
  }

  public static CustomPdfMetadata signedMetadata() {
    return CustomPdfMetadata.builder()
        .customTextList(
            List.of(
                CustomText.builder()
                    .value(
                        "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.")
                    .x(100)
                    .y(50)
                    .fontSize(12)
                    .tagIndex(15)
                    .build()))
        .rightMarginText(RIGHT_MARGIN_TEXT)
        .accessibilityMetadata(AccessibilityMetadata.builder().title(TITLE).build())
        .build();
  }

  public static CustomPdfMetadata signedAndSentMetadata() {
    return CustomPdfMetadata.builder()
        .customTextList(
            List.of(
                CustomText.builder()
                    .value(
                        "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.")
                    .x(100)
                    .y(50)
                    .fontSize(12)
                    .pageIndex(0)
                    .tagIndex(15)
                    .build(),
                CustomText.builder()
                    .value("Intyget har skickats digitalt till Försäkringskassan")
                    .fontSize(16)
                    .build(),
                CustomText.builder()
                    .value("Du kan se intyget genom att logga in på 1177.se")
                    .fontSize(14)
                    .build()))
        .rightMarginText(RIGHT_MARGIN_TEXT)
        .accessibilityMetadata(AccessibilityMetadata.builder().title(TITLE).build())
        .addDraftWatermark(false)
        .build();
  }

  public static CustomPdfMetadata fullMetadata() {
    return signedAndSentMetadata();
  }
}
