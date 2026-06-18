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
import java.util.HashMap;
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

  private final OverflowPageStructureCloner structureCloner;

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

    final var clonedPageSections = new HashMap<PDPage, PDStructureElement>();
    final var additionalPages = new ArrayList<PDPage>();
    for (var i = 1; i < pages.size(); i++) {
      final var clonedPage = cloneOverflowPage(document, overflowPageIndex);
      additionalPages.add(clonedPage);
      final var overflowDiv =
          structureCloner.cloneStructureForPage(document, overflowPageIndex, clonedPage);
      clonedPageSections.put(clonedPage, overflowDiv);
    }

    final var firstPage = document.getPage(overflowPageIndex);
    renderTextOnPage(
        document,
        firstPage,
        pages.getFirst(),
        font,
        fontSize,
        fieldRectangle,
        getOrCreateStructureSection(document, overflowPageIndex));

    for (var i = 0; i < additionalPages.size(); i++) {
      final var newPage = additionalPages.get(i);
      document.addPage(newPage);
      final var section = clonedPageSections.get(newPage);
      renderTextOnPage(
          document, newPage, pages.get(i + 1), font, fontSize, fieldRectangle, section);
      structureCloner.updateParentTreeForPage(document, newPage);
    }
  }

  private void renderTextOnPage(
      PDDocument document,
      PDPage page,
      List<String> lines,
      PDFont font,
      float fontSize,
      PDRectangle fieldRectangle,
      PDStructureElement section)
      throws IOException {
    final var lineHeight = fontSize * LINE_SPACING;
    final var startX = fieldRectangle.getLowerLeftX() + X_MARGIN_APPENDIX_PAGE;
    final var startY = fieldRectangle.getUpperRightY() - Y_MARGIN_APPENDIX_PAGE;

    var mcid = MaxMCIDExtractor.findNextMcid(document);

    try (final var contentStream = PdfAccessibilityUtil.createContentStream(document, page)) {
      contentStream.beginText();
      contentStream.setFont(font, fontSize);
      contentStream.newLineAtOffset(startX, startY);

      final var paragraphText = new StringBuilder();
      final var dictionary =
          PdfAccessibilityUtil.beginMarkedContent(contentStream, COSName.P, ++mcid);

      for (var i = 0; i < lines.size(); i++) {
        final var line = lines.get(i);
        contentStream.showText(line);
        if (!paragraphText.isEmpty()) {
          paragraphText.append("\n");
        }
        paragraphText.append(line);

        if (i < lines.size() - 1) {
          contentStream.newLineAtOffset(0, -lineHeight);
        }
      }

      contentStream.endMarkedContent();
      PdfAccessibilityUtil.addContentToCurrentSection(
          page, dictionary, section, COSName.P, StandardStructureTypes.P, paragraphText.toString());

      contentStream.endText();
    }
  }

  private PDPage cloneOverflowPage(PDDocument document, int overflowPageIndex) {
    final var templatePage = document.getPage(overflowPageIndex);
    final var clonedDictionary = new COSDictionary(templatePage.getCOSObject());
    clonedDictionary.removeItem(COSName.ANNOTS);
    clonedDictionary.removeItem(COSName.STRUCT_PARENTS);

    final var newPage = new PDPage(clonedDictionary);
    newPage.setResources(templatePage.getResources());
    return newPage;
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
