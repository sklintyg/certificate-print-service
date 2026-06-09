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
package se.inera.intyg.certificateprintservice.pdfbox.accessibility;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureNode;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;

public class PdfAccessibilityUtil {

  private PdfAccessibilityUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static PDPageContentStream createContentStream(PDDocument document, PDPage page)
      throws IOException {
    return new PDPageContentStream(document, page, AppendMode.APPEND, true, true);
  }

  public static COSDictionary beginMarkedContent(
      PDPageContentStream contentStream, COSName name, int mcid) throws IOException {
    final var dictionary = new COSDictionary();
    dictionary.setName("Tag" + System.currentTimeMillis(), name.getName());
    dictionary.setInt(COSName.MCID, mcid);
    contentStream.beginMarkedContent(name, PDPropertyList.create(dictionary));
    return dictionary;
  }

  public static void addContentToCurrentSection(
      PDPage page,
      COSDictionary markedContentDictionary,
      PDStructureElement section,
      COSName name,
      String type,
      String text,
      boolean prepend) {
    final var newContent = new PDStructureElement(type, section);
    newContent.setActualText(text);
    newContent.setPage(page);

    if (markedContentDictionary != null) {
      final var markedContent = new PDMarkedContent(name, markedContentDictionary);
      newContent.appendKid(markedContent);
    }

    if (prepend) {
      final var kids = section.getKids();
      kids.addFirst(newContent);
      section.setKids(kids);
    } else {
      section.appendKid(newContent);
    }
  }

  public static void addContentToCurrentSection(
      PDPage page,
      COSDictionary markedContentDictionary,
      PDStructureElement section,
      COSName name,
      String type,
      String text) {
    addContentToCurrentSection(page, markedContentDictionary, section, name, type, text, false);
  }

  public static PDStructureElement createNewDivOnPage(PDDocument pdf, int index, int pageIndex) {
    final var structuredTree = pdf.getDocumentCatalog().getStructureTreeRoot();
    final var pageTag = getPageTag(structuredTree, pageIndex);
    return createNewContainer(pageTag, StandardStructureTypes.DIV, index);
  }

  public static PDStructureElement getFirstDiv(PDDocument pdf) {
    final var structuredTree = pdf.getDocumentCatalog().getStructureTreeRoot();
    final var pageTag = getPageTag(structuredTree, 0);
    if (pageTag.getKids().isEmpty()) {
      return pageTag;
    }
    return (PDStructureElement) pageTag.getKids().getFirst();
  }

  public static PDStructureElement getLastDivOfPage(PDDocument pdf, int pageIndex) {
    final var structuredTree = pdf.getDocumentCatalog().getStructureTreeRoot();
    final var pageTag = getPageTag(structuredTree, pageIndex);
    if (pageTag.getKids().isEmpty()) {
      return pageTag;
    }
    return (PDStructureElement) pageTag.getKids().getLast();
  }

  public static PDStructureElement getDivInQuestionSection(
      PDDocument pdf, int index, int pageIndex) {
    final var structuredTree = pdf.getDocumentCatalog().getStructureTreeRoot();
    final var pageTag = getPageTag(structuredTree, pageIndex);
    if (pageTag.getKids().isEmpty()) {
      return pageTag;
    }
    final var containerWithMostKids =
        pageTag.getKids().stream()
            .map(PDStructureElement.class::cast)
            .map(PDStructureNode::getKids)
            .max(Comparator.comparing(List::size));

    if (containerWithMostKids.isEmpty() || containerWithMostKids.get().size() - 1 < index) {
      throw new IllegalStateException("Does not exist div to place tag in");
    }
    return (PDStructureElement) containerWithMostKids.get().get(index);
  }

  // --- Private helpers ---

  private static PDStructureElement getPageTag(PDStructureTreeRoot structuredTree, int pageIndex) {
    final var documentTag = getFirstChildFromStructuredElement(structuredTree.getKids());
    return getChildFromStructuredElement(documentTag, pageIndex);
  }

  private static PDStructureElement getFirstChildFromStructuredElement(List<Object> kids) {
    if (kids.isEmpty()) {
      throw new IllegalStateException("PDF does not have expected root element for tags");
    }
    return (PDStructureElement) kids.getFirst();
  }

  private static PDStructureElement getChildFromStructuredElement(
      PDStructureElement element, int index) {
    final var kids = element.getKids();
    if (kids.isEmpty()) {
      throw new IllegalStateException("PDF does not have expected div/section element");
    }
    if (index >= kids.size() - 1) {
      return (PDStructureElement) kids.getLast();
    }
    return (PDStructureElement) kids.get(index);
  }

  private static PDStructureElement createNewContainer(
      PDStructureElement parent, String type, Integer index) {
    final var newDiv = new PDStructureElement(type, parent);
    final var kids = parent.getKids();
    if (index == null) {
      kids.addLast(newDiv);
    } else {
      kids.add(index, newDiv);
    }
    parent.setKids(kids);
    return newDiv;
  }
}
