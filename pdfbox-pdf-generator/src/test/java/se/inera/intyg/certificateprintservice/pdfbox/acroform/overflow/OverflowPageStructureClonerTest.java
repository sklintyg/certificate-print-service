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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OverflowPageStructureClonerTest {

  private OverflowPageStructureCloner cloner;

  @BeforeEach
  void setUp() {
    cloner = new OverflowPageStructureCloner();
  }

  @Test
  void shouldReturnDivElementForOverflowText() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var clonedPage = createClonedPage(document, 0);

      final var structure = cloner.cloneStructureForPage(document, 0, clonedPage);

      assertNotNull(structure.overflowDiv());
      assertEquals("Div", structure.overflowDiv().getStructureType());
    }
  }

  @Test
  void shouldReturnSectElementForPage() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var clonedPage = createClonedPage(document, 0);

      final var structure = cloner.cloneStructureForPage(document, 0, clonedPage);

      assertNotNull(structure.sect());
    }
  }

  @Test
  void shouldCreateNewPageSectInStructureTree() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var structuredTree = document.getDocumentCatalog().getStructureTreeRoot();
      final var documentTag = (PDStructureElement) structuredTree.getKids().getFirst();
      final var initialSectCount = documentTag.getKids().size();

      final var clonedPage = createClonedPage(document, 0);
      cloner.cloneStructureForPage(document, 0, clonedPage);

      assertEquals(
          initialSectCount + 1,
          documentTag.getKids().size(),
          "A new page SECT should be added for the cloned page");
    }
  }

  @Test
  void shouldAssignStructParentsToClonedPage() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var clonedPage = createClonedPage(document, 0);

      cloner.cloneStructureForPage(document, 0, clonedPage);

      final var structParents = clonedPage.getCOSObject().getInt(COSName.STRUCT_PARENTS, -1);
      assertTrue(structParents >= 0, "Cloned page should have StructParents assigned");
    }
  }

  @Test
  void shouldCloneStaticStructureElementsWithMcids() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var clonedPage = createClonedPage(document, 0);
      cloner.cloneStructureForPage(document, 0, clonedPage);

      final var structuredTree = document.getDocumentCatalog().getStructureTreeRoot();
      final var documentTag = (PDStructureElement) structuredTree.getKids().getFirst();
      final var newSect = (PDStructureElement) documentTag.getKids().getLast();

      final var hasMcid = containsMcidReference(newSect.getCOSObject());
      assertTrue(hasMcid, "Cloned structure should contain MCID references");
    }
  }

  @Test
  void shouldSetPageReferenceOnClonedElements() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var clonedPage = createClonedPage(document, 0);
      cloner.cloneStructureForPage(document, 0, clonedPage);

      final var structuredTree = document.getDocumentCatalog().getStructureTreeRoot();
      final var documentTag = (PDStructureElement) structuredTree.getKids().getFirst();
      final var newSect = (PDStructureElement) documentTag.getKids().getLast();

      final var hasPageRef = containsPageReference(newSect.getCOSObject(), clonedPage);
      assertTrue(hasPageRef, "Cloned structure should reference the cloned page");
    }
  }

  @Test
  void shouldPreserveTemplateStructureAfterCloning() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var structuredTree = document.getDocumentCatalog().getStructureTreeRoot();
      final var documentTag = (PDStructureElement) structuredTree.getKids().getFirst();
      final var templateSect = (PDStructureElement) documentTag.getKids().getFirst();
      final var originalKidCount = templateSect.getKids().size();

      final var clonedPage = createClonedPage(document, 0);
      cloner.cloneStructureForPage(document, 0, clonedPage);

      assertEquals(
          originalKidCount,
          templateSect.getKids().size(),
          "Template page structure should not be modified");
    }
  }

  @Test
  void shouldSkipDynamicObjectReferenceElementsWhenCloningOverflowPage() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var clonedPage = createClonedPage(document, 4);

      cloner.cloneStructureForPage(document, 4, clonedPage);

      final var structuredTree = document.getDocumentCatalog().getStructureTreeRoot();
      final var documentTag = (PDStructureElement) structuredTree.getKids().getFirst();
      final var newSect = (PDStructureElement) documentTag.getKids().getLast();

      assertEquals(
          0,
          countObjectReferences(newSect.getCOSObject()),
          "Cloned overflow page structure must not duplicate dynamic AcroForm values");
      assertTrue(
          containsMcidReference(newSect.getCOSObject()),
          "Static labels and headings should still be cloned");
    }
  }

  @Test
  void shouldSetAllClonedPageReferencesToClonedPage() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var clonedPage = createClonedPage(document, 4);

      cloner.cloneStructureForPage(document, 4, clonedPage);

      final var structuredTree = document.getDocumentCatalog().getStructureTreeRoot();
      final var documentTag = (PDStructureElement) structuredTree.getKids().getFirst();
      final var newSect = (PDStructureElement) documentTag.getKids().getLast();

      assertAllPageReferencesMatch(newSect.getCOSObject(), clonedPage);
    }
  }

  @Test
  void shouldPlacePatientIdPlaceholderNotAtSectionEnd() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var clonedPage = createClonedPage(document, 4);

      final var structure = cloner.cloneStructureForPage(document, 4, clonedPage);

      final var placeholder = structure.patientIdValuePlaceholder();
      assertNotNull(placeholder, "A placeholder must be created for the dynamic value");

      final var sectKids = structure.sect().getKids();
      final var lastKid = sectKids.getLast();
      final var isPlaceholderLast =
          lastKid instanceof PDStructureElement last
              && last.getCOSObject() == placeholder.getCOSObject();
      assertTrue(
          !isPlaceholderLast,
          "Placeholder must not be appended at section end, preserving original reading order");
    }
  }

  /**
   * Reproduces the structure found in affected templates (fk3221, fk7426, fk7427, fk7809, fk7810)
   * where the overflow SECT has exactly 2 children: a Sect header and a margin-text Div (the latter
   * being the last child). The fix must clone the margin-text Div rather than replace it.
   */
  @Test
  void shouldCloneAllChildrenWhenMarginTextDivIsLastChild() throws IOException {
    try (final var document = createTwoChildOverflowTemplate()) {
      final var clonedPage = createClonedPage(document, 0);

      cloner.cloneStructureForPage(document, 0, clonedPage);

      final var structuredTree = document.getDocumentCatalog().getStructureTreeRoot();
      final var documentTag = (PDStructureElement) structuredTree.getKids().getFirst();
      final var newSect = (PDStructureElement) documentTag.getKids().getLast();

      assertTrue(
          containsMcidReference(newSect.getCOSObject()),
          "Margin text MCID (last child in 2-child template) must be cloned, not discarded");
    }
  }

  @Test
  void shouldAppendOverflowDivAfterAllClonedChildren() throws IOException {
    try (final var document = createTwoChildOverflowTemplate()) {
      final var clonedPage = createClonedPage(document, 0);

      final var structure = cloner.cloneStructureForPage(document, 0, clonedPage);

      final var sectKids = structure.sect().getKids();
      // Template has 2 children (Sect header + margin-text Div); both should be cloned, plus the
      // new overflow Div appended = 3 total children on the cloned SECT.
      assertEquals(
          3,
          sectKids.size(),
          "Cloned SECT must contain all 2 original children plus the appended overflow Div");
      final var lastKid = sectKids.getLast();
      assertEquals(
          structure.overflowDiv().getCOSObject(),
          ((PDStructureElement) lastKid).getCOSObject(),
          "Overflow Div must be the last child of the cloned SECT");
    }
  }

  private PDDocument createTwoChildOverflowTemplate() throws IOException {
    final var document = new PDDocument();
    document.setAllSecurityToBeRemoved(true);

    final var page = new PDPage();
    document.addPage(page);

    final var structureTree = new PDStructureTreeRoot();
    document.getDocumentCatalog().setStructureTreeRoot(structureTree);

    final var docElement = new PDStructureElement(StandardStructureTypes.DOCUMENT, null);
    structureTree.appendKid(docElement);

    final var pageElement = new PDStructureElement(StandardStructureTypes.SECT, docElement);
    pageElement.getCOSObject().setItem(COSName.getPDFName("T"), COSName.getPDFName("Page 1"));
    docElement.appendKid(pageElement);

    // Child 0: a Sect with one MCID-bearing Div (simulates static header with labels)
    final var headerSect = new PDStructureElement(StandardStructureTypes.SECT, pageElement);
    final var headerDiv = new PDStructureElement(StandardStructureTypes.DIV, headerSect);
    final var headerP = new PDStructureElement(StandardStructureTypes.P, headerDiv);
    headerP.getCOSObject().setItem(COSName.getPDFName("Pg"), page.getCOSObject());
    headerP.getCOSObject().setItem(COSName.K, COSInteger.get(1));
    headerDiv.appendKid(headerP);
    headerSect.appendKid(headerDiv);
    pageElement.appendKid(headerSect);

    // Child 1: the margin-text Div (MCID 0) — this is the LAST child, matching affected templates
    final var marginDiv = new PDStructureElement(StandardStructureTypes.DIV, pageElement);
    final var marginP = new PDStructureElement(StandardStructureTypes.P, marginDiv);
    marginP.getCOSObject().setItem(COSName.getPDFName("Pg"), page.getCOSObject());
    marginP.getCOSObject().setItem(COSName.K, COSInteger.get(0));
    marginDiv.appendKid(marginP);
    pageElement.appendKid(marginDiv);

    page.getCOSObject().setInt(COSName.STRUCT_PARENTS, 0);
    structureTree.getCOSObject().setInt(COSName.getPDFName("ParentTreeNextKey"), 1);

    return document;
  }

  private boolean containsMcidReference(COSDictionary dict) {
    final var k = resolve(dict.getDictionaryObject(COSName.K));
    if (k instanceof COSInteger) {
      return true;
    } else if (k instanceof COSDictionary kDict) {
      if (kDict.containsKey(COSName.MCID)) {
        return true;
      }
      if (kDict.containsKey(COSName.K)) {
        return containsMcidReference(kDict);
      }
    } else if (k instanceof COSArray arr) {
      for (var i = 0; i < arr.size(); i++) {
        final var item = resolve(arr.get(i));
        if (item instanceof COSInteger) {
          return true;
        }
        if (item instanceof COSDictionary itemDict) {
          if (itemDict.containsKey(COSName.MCID) || containsMcidReference(itemDict)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean containsPageReference(COSDictionary dict, PDPage page) {
    if (dict.getItem(COSName.getPDFName("Pg")) == page.getCOSObject()) {
      return true;
    }
    final var k = resolve(dict.getDictionaryObject(COSName.K));
    if (k instanceof COSDictionary kDict) {
      if (containsPageReference(kDict, page)) {
        return true;
      }
    } else if (k instanceof COSArray arr) {
      for (var i = 0; i < arr.size(); i++) {
        final var item = resolve(arr.get(i));
        if (item instanceof COSDictionary itemDict && containsPageReference(itemDict, page)) {
          return true;
        }
      }
    }
    return false;
  }

  private void assertAllPageReferencesMatch(COSDictionary dict, PDPage page) {
    final var pageReference = dict.getItem(COSName.getPDFName("Pg"));
    if (pageReference != null) {
      assertEquals(page.getCOSObject(), pageReference, "Pg must point to the cloned page");
    }

    final var k = resolve(dict.getDictionaryObject(COSName.K));
    if (k instanceof COSDictionary kDict) {
      assertAllPageReferencesMatch(kDict, page);
    } else if (k instanceof COSArray arr) {
      for (var i = 0; i < arr.size(); i++) {
        final var item = resolve(arr.get(i));
        if (item instanceof COSDictionary itemDict) {
          assertAllPageReferencesMatch(itemDict, page);
        }
      }
    }
  }

  private int countObjectReferences(COSDictionary dict) {
    var count = dict.containsKey(COSName.OBJ) ? 1 : 0;
    final var k = resolve(dict.getDictionaryObject(COSName.K));
    if (k instanceof COSDictionary kDict) {
      count += countObjectReferences(kDict);
    } else if (k instanceof COSArray arr) {
      for (var i = 0; i < arr.size(); i++) {
        final var item = resolve(arr.get(i));
        if (item instanceof COSDictionary itemDict) {
          count += countObjectReferences(itemDict);
        }
      }
    }
    return count;
  }

  private Object resolve(Object obj) {
    if (obj instanceof COSObject cosObj) {
      return cosObj.getObject();
    }
    return obj;
  }

  private PDPage createClonedPage(PDDocument document, int pageIndex) {
    final var templatePage = document.getPage(pageIndex);
    final var clonedDictionary = new COSDictionary(templatePage.getCOSObject());
    clonedDictionary.removeItem(COSName.ANNOTS);
    clonedDictionary.removeItem(COSName.STRUCT_PARENTS);
    final var newPage = new PDPage(clonedDictionary);
    newPage.setResources(templatePage.getResources());
    return newPage;
  }

  private PDDocument loadTestTemplate() throws IOException {
    final var templateBytes =
        getClass().getResourceAsStream("/tagged-test-template-overflow.pdf").readAllBytes();
    return Loader.loadPDF(templateBytes);
  }
}
