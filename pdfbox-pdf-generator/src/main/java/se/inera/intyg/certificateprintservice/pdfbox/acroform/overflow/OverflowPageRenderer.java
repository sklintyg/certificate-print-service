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
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfbox.accessibility.MaxMCIDExtractor;
import se.inera.intyg.certificateprintservice.pdfbox.accessibility.PdfAccessibilityUtil;

@Component
@RequiredArgsConstructor
public class OverflowPageRenderer {

  private static final COSName SPAN = COSName.getPDFName("Span");
  private static final float Y_MARGIN_APPENDIX_PAGE = 16f;
  private static final float X_MARGIN_APPENDIX_PAGE = 2f;
  private static final float LINE_SPACING = 1.2f;
  private static final float ACROFORM_FIELD_HORIZONTAL_INSET = 4f;
  public static final int PATIENT_RECT_Y_MARGIN = 6;

  private final OverflowPageStructureCloner structureCloner;

  public void renderAllOverflowPages(
      PDDocument document,
      int overflowPageIndex,
      List<List<OverflowLine>> pages,
      PDFont font,
      PDFont boldFont,
      float fontSize,
      PDRectangle fieldRectangle,
      PatientIdInfo patientIdInfo)
      throws IOException {
    final var clonedPageStructures = new HashMap<PDPage, ClonedPageStructure>();
    final var additionalPages = new ArrayList<PDPage>();
    for (var i = 1; i < pages.size(); i++) {
      final var clonedPage = cloneOverflowPage(document, overflowPageIndex);
      additionalPages.add(clonedPage);
      final var clonedStructure =
          structureCloner.cloneStructureForPage(document, overflowPageIndex, clonedPage);
      clonedPageStructures.put(clonedPage, clonedStructure);
    }

    final var firstPage = document.getPage(overflowPageIndex);
    renderTextOnPage(
        document,
        firstPage,
        pages.getFirst(),
        font,
        boldFont,
        fontSize,
        fieldRectangle,
        getOrCreateStructureSection(document, overflowPageIndex));

    for (var i = 0; i < additionalPages.size(); i++) {
      final var newPage = additionalPages.get(i);
      document.addPage(newPage);
      final var clonedStructure = clonedPageStructures.get(newPage);
      renderTextOnPage(
          document,
          newPage,
          pages.get(i + 1),
          font,
          boldFont,
          fontSize,
          fieldRectangle,
          clonedStructure.overflowDiv());

      renderPatientIdOnPage(
          document, newPage, clonedStructure.patientIdValuePlaceholder(), patientIdInfo);

      structureCloner.updateParentTreeForPage(document, newPage);
    }
  }

  private void renderTextOnPage(
      PDDocument document,
      PDPage page,
      List<OverflowLine> lines,
      PDFont font,
      PDFont boldFont,
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

      PDFont currentFont = font;
      for (var i = 0; i < lines.size(); i++) {
        final var line = lines.get(i);
        final var lineFont = line.bold() ? boldFont : font;
        if (!lineFont.equals(currentFont)) {
          contentStream.setFont(lineFont, fontSize);
          currentFont = lineFont;
        }
        contentStream.showText(line.text());
        if (!paragraphText.isEmpty()) {
          paragraphText.append("\n");
        }
        paragraphText.append(line.text());

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

  private PDPage cloneOverflowPage(PDDocument document, int overflowPageIndex) throws IOException {
    final var templatePage = document.getPage(overflowPageIndex);
    final var clonedDictionary = new COSDictionary(templatePage.getCOSObject());
    clonedDictionary.removeItem(COSName.ANNOTS);
    clonedDictionary.removeItem(COSName.STRUCT_PARENTS);

    final var newPage = new PDPage(clonedDictionary);
    newPage.setResources(templatePage.getResources());
    newPage.setContents(cloneContentStreams(document, templatePage));
    return newPage;
  }

  private List<PDStream> cloneContentStreams(PDDocument document, PDPage templatePage)
      throws IOException {
    final var clonedStreams = new ArrayList<PDStream>();
    final var contentStreams = templatePage.getContentStreams();
    while (contentStreams.hasNext()) {
      final var originalStream = contentStreams.next();
      final var clonedStream = new PDStream(document);
      try (final var inputStream = originalStream.createInputStream();
          final var outputStream = clonedStream.createOutputStream(COSName.FLATE_DECODE)) {
        inputStream.transferTo(outputStream);
      }
      clonedStreams.add(clonedStream);
    }
    return clonedStreams;
  }

  private void renderPatientIdOnPage(
      PDDocument document,
      PDPage page,
      PDStructureElement valueElement,
      PatientIdInfo patientIdInfo)
      throws IOException {
    var mcid = MaxMCIDExtractor.findNextMcid(document);
    final var rect = patientIdInfo.rectangle();

    try (final var contentStream = PdfAccessibilityUtil.createContentStream(document, page)) {
      contentStream.beginText();
      contentStream.setFont(patientIdInfo.font(), patientIdInfo.fontSize());
      contentStream.newLineAtOffset(
          rect.getLowerLeftX(), rect.getLowerLeftY() + PATIENT_RECT_Y_MARGIN);

      final var dictionary = PdfAccessibilityUtil.beginMarkedContent(contentStream, SPAN, ++mcid);
      contentStream.showText(patientIdInfo.value());
      contentStream.endMarkedContent();
      contentStream.endText();

      PdfAccessibilityUtil.fillPatientIdValueElement(
          page, dictionary, valueElement, SPAN, patientIdInfo.value());
    }
  }

  private PDStructureElement getOrCreateStructureSection(PDDocument document, int pageIndex) {
    try {
      return PdfAccessibilityUtil.getLastDivOfPage(document, pageIndex);
    } catch (Exception e) {
      return PdfAccessibilityUtil.createStructureForNewPage(document);
    }
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
