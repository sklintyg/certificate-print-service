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
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7804CustomPdfMetadata.metadataWithAllCustomTextsAndMargin;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OverflowPageRendererTest {

  private OverflowPageRenderer renderer;
  private PDType1Font font;
  private PDType1Font boldFont;

  private static final float FONT_SIZE = 9f;
  private static final PDRectangle FIELD_RECT = new PDRectangle(50f, 50f, 400f, 700f);

  @BeforeEach
  void setUp() {
    renderer = new OverflowPageRenderer(new OverflowPageStructureCloner());
    font = new PDType1Font(FontName.HELVETICA);
    boldFont = new PDType1Font(FontName.HELVETICA_BOLD);
  }

  @Test
  void shouldNotAddPagesWhenOnlySinglePage() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var initialPageCount = document.getNumberOfPages();
      final var pages =
          List.of(List.of(new OverflowLine("Line 1", false), new OverflowLine("Line 2", false)));

      renderer.renderAllOverflowPages(
          document, 0, pages, font, boldFont, FONT_SIZE, FIELD_RECT, null);

      assertEquals(initialPageCount, document.getNumberOfPages());
    }
  }

  @Test
  void shouldAddOnePageWhenTwoPageChunksProvided() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var initialPageCount = document.getNumberOfPages();
      final var pages =
          List.of(
              List.of(new OverflowLine("Line 1", true), new OverflowLine("Line 2", false)),
              List.of(new OverflowLine("Line 3", true), new OverflowLine("Line 4", false)));

      renderer.renderAllOverflowPages(
          document,
          0,
          pages,
          font,
          boldFont,
          FONT_SIZE,
          FIELD_RECT,
          PatientIdInfo.of(document, metadataWithAllCustomTextsAndMargin().personIdConfig()));

      assertEquals(initialPageCount + 1, document.getNumberOfPages());
    }
  }

  @Test
  void shouldAddMultiplePagesWhenManyChunksProvided() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var initialPageCount = document.getNumberOfPages();
      final var pages =
          List.of(
              List.of(new OverflowLine("Line 1", false)),
              List.of(new OverflowLine("Line 2", false)),
              List.of(new OverflowLine("Line 3", false)));

      renderer.renderAllOverflowPages(
          document,
          0,
          pages,
          font,
          boldFont,
          FONT_SIZE,
          FIELD_RECT,
          PatientIdInfo.of(document, metadataWithAllCustomTextsAndMargin().personIdConfig()));

      assertEquals(initialPageCount + 2, document.getNumberOfPages());
    }
  }

  @Test
  void shouldUseMediaBoxFromOverflowPage() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var overflowPage = document.getPage(0);
      final var expectedWidth = overflowPage.getMediaBox().getWidth();
      final var pages =
          List.of(
              List.of(new OverflowLine("Line 1", false)),
              List.of(new OverflowLine("Line 2", false)));

      renderer.renderAllOverflowPages(
          document,
          0,
          pages,
          font,
          boldFont,
          FONT_SIZE,
          FIELD_RECT,
          PatientIdInfo.of(document, metadataWithAllCustomTextsAndMargin().personIdConfig()));

      final var newPage = document.getPage(document.getNumberOfPages() - 1);
      assertEquals(expectedWidth, newPage.getMediaBox().getWidth());
    }
  }

  @Test
  void shouldRenderSinglePatientIdValuePerClonedOverflowPage() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var pages =
          List.of(
              List.of(new OverflowLine("Line 1", false)),
              List.of(new OverflowLine("Line 2", false)),
              List.of(new OverflowLine("Line 3", false)));
      final var patientIdInfo =
          PatientIdInfo.of(document, metadataWithAllCustomTextsAndMargin().personIdConfig());

      renderer.renderAllOverflowPages(
          document, 4, pages, font, boldFont, FONT_SIZE, FIELD_RECT, patientIdInfo);

      final var documentTag = getDocumentTag(document);
      assertPatientIdValue(
          (PDStructureElement) documentTag.getKids().get(documentTag.getKids().size() - 2),
          document.getPage(5),
          patientIdInfo.value());
      assertPatientIdValue(
          (PDStructureElement) documentTag.getKids().getLast(),
          document.getPage(6),
          patientIdInfo.value());
    }
  }

  private void assertPatientIdValue(
      PDStructureElement pageSect, org.apache.pdfbox.pdmodel.PDPage page, String patientId) {
    assertEquals(
        1,
        countPatientIdValues(pageSect.getCOSObject(), patientId, null),
        "There should be exactly one patient id value structure element");
    assertEquals(
        1,
        countPatientIdValues(pageSect.getCOSObject(), patientId, page.getCOSObject()),
        "The patient id value structure element must point to the cloned page");
    assertEquals(
        0,
        countObjectReferences(pageSect.getCOSObject()),
        "Cloned dynamic AcroForm value structures should not be present");
  }

  private PDStructureElement getDocumentTag(org.apache.pdfbox.pdmodel.PDDocument document) {
    final var structuredTree = document.getDocumentCatalog().getStructureTreeRoot();
    return (PDStructureElement) structuredTree.getKids().getFirst();
  }

  private int countPatientIdValues(COSDictionary dict, String patientId, COSDictionary page) {
    var count = 0;
    final var actualText = resolve(dict.getDictionaryObject(COSName.ACTUAL_TEXT));
    final var pg = dict.getItem(COSName.getPDFName("Pg"));
    if (actualText instanceof COSString text
        && patientId.equals(text.getString())
        && (page == null || pg == page)) {
      count++;
    }

    final var k = resolve(dict.getDictionaryObject(COSName.K));
    if (k instanceof COSDictionary kDict) {
      count += countPatientIdValues(kDict, patientId, page);
    } else if (k instanceof COSArray arr) {
      for (var i = 0; i < arr.size(); i++) {
        final var item = resolve(arr.get(i));
        if (item instanceof COSDictionary itemDict) {
          count += countPatientIdValues(itemDict, patientId, page);
        }
      }
    }
    return count;
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

  private org.apache.pdfbox.pdmodel.PDDocument loadTestTemplate() throws IOException {
    final var templateBytes =
        Objects.requireNonNull(getClass().getResourceAsStream("/tagged-test-template-overflow.pdf"))
            .readAllBytes();
    return Loader.loadPDF(templateBytes);
  }
}
