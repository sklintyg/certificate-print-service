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
package se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfbox.accessibility.MaxMCIDExtractor;
import se.inera.intyg.certificateprintservice.pdfbox.accessibility.PdfAccessibilityUtil;

@Component
@RequiredArgsConstructor
public class OverflowPageRenderer {

  private static final float Y_MARGIN_APPENDIX_PAGE = 16f;
  private static final float X_MARGIN_APPENDIX_PAGE = 2f;
  private static final float LINE_SPACING = 1.2f;
  private static final float ACROFORM_FIELD_HORIZONTAL_INSET = 4f;

  public void renderAllOverflowPages(
      PDDocument document,
      int overflowPageIndex,
      List<List<String>> pages,
      PDFont font,
      float fontSize,
      PDRectangle fieldRectangle)
      throws IOException {
    if (pages.isEmpty()) {
      return;
    }

    final var additionalPages = new ArrayList<PDPage>();
    for (var i = 1; i < pages.size(); i++) {
      additionalPages.add(cloneOverflowPage(document, overflowPageIndex));
    }

    final var firstPage = document.getPage(overflowPageIndex);
    renderTextOnPage(
        document, firstPage, overflowPageIndex, pages.getFirst(), font, fontSize, fieldRectangle);

    for (var i = 0; i < additionalPages.size(); i++) {
      final var newPage = additionalPages.get(i);
      document.addPage(newPage);
      final var pageIndex = document.getPages().getCount() - 1;
      renderTextOnPage(
          document, newPage, pageIndex, pages.get(i + 1), font, fontSize, fieldRectangle);
    }
  }

  public void renderAdditionalPages(
      PDDocument document,
      int overflowPageIndex,
      List<List<String>> pages,
      PDFont font,
      float fontSize,
      PDRectangle fieldRectangle)
      throws IOException {
    if (pages.size() <= 1) {
      return;
    }

    for (var i = 1; i < pages.size(); i++) {
      final var pageLines = pages.get(i);
      final var newPage = cloneOverflowPage(document, overflowPageIndex);
      document.addPage(newPage);

      final var pageIndex = document.getPages().getCount() - 1;
      renderTextOnPage(document, newPage, pageIndex, pageLines, font, fontSize, fieldRectangle);
    }
  }

  private PDPage cloneOverflowPage(PDDocument document, int overflowPageIndex) {
    final var templatePage = document.getPage(overflowPageIndex);
    final var clonedDictionary = new COSDictionary(templatePage.getCOSObject());
    clonedDictionary.removeItem(org.apache.pdfbox.cos.COSName.ANNOTS);
    clonedDictionary.removeItem(org.apache.pdfbox.cos.COSName.STRUCT_PARENTS);

    final var newPage = new PDPage(clonedDictionary);
    newPage.setResources(templatePage.getResources());
    return newPage;
  }

  private void renderTextOnPage(
      PDDocument document,
      PDPage page,
      int pageIndex,
      List<String> lines,
      PDFont font,
      float fontSize,
      PDRectangle fieldRectangle)
      throws IOException {
    final var lineHeight = fontSize * LINE_SPACING;
    final var startX = fieldRectangle.getLowerLeftX() + X_MARGIN_APPENDIX_PAGE;
    final var startY = fieldRectangle.getUpperRightY() - Y_MARGIN_APPENDIX_PAGE;

    final var section = getOrCreateStructureSection(document, pageIndex);
    var mcid = MaxMCIDExtractor.findNextMcid(document);

    try (final var contentStream = PdfAccessibilityUtil.createContentStream(document, page)) {
      contentStream.beginText();
      contentStream.setFont(font, fontSize);
      contentStream.newLineAtOffset(startX, startY);

      for (var i = 0; i < lines.size(); i++) {
        final var dictionary =
            PdfAccessibilityUtil.beginMarkedContent(contentStream, COSName.P, ++mcid);
        contentStream.showText(lines.get(i));
        contentStream.endMarkedContent();

        PdfAccessibilityUtil.addContentToCurrentSection(
            page, dictionary, section, COSName.P, StandardStructureTypes.P, lines.get(i));

        if (i < lines.size() - 1) {
          contentStream.newLineAtOffset(0, -lineHeight);
        }
      }
      contentStream.endText();
    }
  }

  private PDStructureElement getOrCreateStructureSection(PDDocument document, int pageIndex) {
    try {
      return PdfAccessibilityUtil.getLastDivOfPage(document, pageIndex);
    } catch (Exception e) {
      return PdfAccessibilityUtil.createStructureForNewPage(document);
    }
  }

  public PDFont getDefaultOverflowFont() {
    return new PDType1Font(FontName.HELVETICA);
  }

  public float getLineSpacing() {
    return LINE_SPACING;
  }

  public float getTopMargin() {
    return Y_MARGIN_APPENDIX_PAGE;
  }

  public float getHorizontalInsets() {
    return ACROFORM_FIELD_HORIZONTAL_INSET;
  }
}
