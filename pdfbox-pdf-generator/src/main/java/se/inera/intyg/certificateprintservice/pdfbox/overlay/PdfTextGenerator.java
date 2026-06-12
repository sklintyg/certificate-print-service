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
package se.inera.intyg.certificateprintservice.pdfbox.overlay;

import static se.inera.intyg.certificateprintservice.pdfbox.accessibility.PdfAccessibilityUtil.addContentToCurrentSection;
import static se.inera.intyg.certificateprintservice.pdfbox.accessibility.PdfAccessibilityUtil.beginMarkedContent;
import static se.inera.intyg.certificateprintservice.pdfbox.accessibility.PdfAccessibilityUtil.createContentStream;
import static se.inera.intyg.certificateprintservice.pdfbox.accessibility.PdfAccessibilityUtil.createNewDivOnPage;
import static se.inera.intyg.certificateprintservice.pdfbox.accessibility.PdfAccessibilityUtil.getDivInQuestionSection;

import java.awt.Color;
import java.io.IOException;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.Appearance;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.FontStyle;

@Service
public class PdfTextGenerator {

  private static final float MARGIN_TEXT_FONT_SIZE = 8f;
  private static final int WATERMARK_FONT_SIZE = 105;
  private static final float MARGIN_TEXT_OFFSET_X = 30f;
  private static final float MARGIN_TEXT_OFFSET_Y = 30f;
  private static final Matrix ROTATE_INSTANCE = Matrix.getRotateInstance(Math.PI / 2, 600, 25);

  public void addWatermark(PDDocument document, String text, int mcid) throws IOException {
    int pageIndex = 0;
    for (PDPage page : document.getPages()) {
      final var section = createNewDivOnPage(document, 0, pageIndex);
      try (final var contentStream = createContentStream(document, page)) {
        final var font = new PDType1Font(FontName.HELVETICA);
        final var width = page.getMediaBox().getWidth();
        final var height = page.getMediaBox().getHeight();
        final var stringWidth = font.getStringWidth(text) / 1000f * WATERMARK_FONT_SIZE;
        final var diagonalLength = (float) Math.sqrt(width * width + height * height);
        final var angle = (float) Math.atan2(height, width);
        final var x = (diagonalLength - stringWidth) / 2;
        final var y = (float) -WATERMARK_FONT_SIZE / 4;

        contentStream.transform(Matrix.getRotateInstance(angle, 0, 0));
        contentStream.setFont(font, WATERMARK_FONT_SIZE);

        final var gs = new PDExtendedGraphicsState();
        gs.setNonStrokingAlphaConstant(0.5f);
        gs.setStrokingAlphaConstant(0.5f);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(Color.gray);

        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        final var dictionary = beginMarkedContent(contentStream, COSName.P, mcid);
        contentStream.showText(text);
        contentStream.endMarkedContent();
        contentStream.endText();

        addContentToCurrentSection(
            page,
            dictionary,
            section,
            COSName.P,
            StandardStructureTypes.P,
            "Detta är ett " + text.toLowerCase());
      }
      pageIndex++;
    }
  }

  public void addMarginText(PDDocument document, String text, int mcid, int pageIndex)
      throws IOException {
    drawText(
        document,
        TextInfo.builder()
            .color(Color.black)
            .mcid(mcid)
            .customText(
                CustomText.builder()
                    .x(MARGIN_TEXT_OFFSET_X)
                    .y(MARGIN_TEXT_OFFSET_Y)
                    .appearance(
                        Appearance.builder()
                            .style(FontStyle.NORMAL)
                            .fontSize(MARGIN_TEXT_FONT_SIZE)
                            .build())
                    .value(text)
                    .pageIndex(pageIndex)
                    .build())
            .build(),
        ROTATE_INSTANCE
    );
  }

  public void drawText(PDDocument pdf, TextInfo textInfo, Matrix matrix) throws IOException {
    final var page = pdf.getPage(textInfo.customText().pageIndex());
    try (final var contentStream = createContentStream(pdf, page)) {
      if (matrix != null) {
        contentStream.transform(matrix);
      }
      contentStream.beginText();
      contentStream.newLineAtOffset(textInfo.customText().x(), textInfo.customText().y());
      contentStream.setNonStrokingColor(textInfo.color());
      contentStream.setFont(
          new PDType1Font(
              textInfo.customText().appearance().getStyle() == FontStyle.BOLD
                  ? FontName.HELVETICA_BOLD : FontName.HELVETICA),
          textInfo.customText().appearance().getFontSize());
      final var dictionary = beginMarkedContent(contentStream, COSName.P, textInfo.mcid());
      contentStream.showText(textInfo.customText().value());
      contentStream.endMarkedContent();
      PDStructureElement section;
      if (textInfo.customText().tagIndex() != null) {
        section = getDivInQuestionSection(pdf, textInfo.customText().tagIndex(),
            textInfo.customText().pageIndex());
      } else {
        section = createNewDivOnPage(pdf, 0, 0);
      }
      if (section != null) {
        addContentToCurrentSection(
            page, dictionary, section, COSName.P, StandardStructureTypes.P,
            textInfo.customText().value(), false);
      }
      contentStream.endText();
    }
  }

  public void drawText(PDDocument pdf, TextInfo textInfo) throws IOException {
    drawText(pdf, textInfo, null);
  }
}
