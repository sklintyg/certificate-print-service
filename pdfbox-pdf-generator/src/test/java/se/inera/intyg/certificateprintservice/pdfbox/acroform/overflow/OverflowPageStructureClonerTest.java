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

      final var overflowDiv = cloner.cloneStructureForPage(document, 0, clonedPage);

      assertNotNull(overflowDiv);
      assertEquals("Div", overflowDiv.getStructureType());
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
