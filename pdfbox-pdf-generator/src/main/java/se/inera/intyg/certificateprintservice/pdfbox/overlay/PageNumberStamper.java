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

import java.io.IOException;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.stereotype.Component;

@Component
public class PageNumberStamper {

  private static final float FONT_SIZE = 10f;
  private static final float RIGHT_MARGIN = 63.5f;
  private static final float TOP_MARGIN = 37f;

  public void stamp(PDDocument document, int startMcid) throws IOException {
    if (document == null) {
      throw new IllegalArgumentException("Document must not be null");
    }

    final var totalPages = document.getNumberOfPages();
    final var font = resolveFont(document);
    var mcid = startMcid;

    for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
      final var page = document.getPage(pageIndex);
      final var pageNumberText = formatPageNumber(pageIndex + 1, totalPages);
      stampPageNumber(document, page, pageIndex, pageNumberText, font, ++mcid);
    }
  }

  private PDFont resolveFont(PDDocument document) throws IOException {
    final var acroForm = document.getDocumentCatalog().getAcroForm();
    if (acroForm != null) {
      final var resources = acroForm.getDefaultResources();
      if (resources != null) {
        final var fontIterator = resources.getFontNames().iterator();
        if (fontIterator.hasNext()) {
          final var fontName = fontIterator.next();
          final var font = resources.getFont(fontName);
          if (font != null) {
            return font;
          }
        }
      }
    }
    throw new IOException("No font found in document AcroForm default resources");
  }

  private void stampPageNumber(
      PDDocument document, PDPage page, int pageIndex, String text, PDFont font, int mcid)
      throws IOException {
    final var mediaBox = page.getMediaBox();
    final var x = mediaBox.getWidth() - RIGHT_MARGIN;
    final var y = mediaBox.getHeight() - TOP_MARGIN;

    final var section = createNewDivOnPage(document, 0, pageIndex);

    try (final var contentStream = createContentStream(document, page)) {
      contentStream.beginText();
      contentStream.setFont(font, FONT_SIZE);
      contentStream.newLineAtOffset(x, y);
      final var dictionary = beginMarkedContent(contentStream, COSName.P, mcid);
      contentStream.showText(text);
      contentStream.endMarkedContent();
      contentStream.endText();

      addContentToCurrentSection(
          page, dictionary, section, COSName.P, StandardStructureTypes.P, text);
    }
  }

  private String formatPageNumber(int currentPage, int totalPages) {
    return "%d (%d)".formatted(currentPage, totalPages);
  }
}
