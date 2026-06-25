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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.TAGGED_PDF_RESOURCE;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfbox.accessibility.PdfAccessibilityUtil;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.Appearance;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.FontStyle;

/**
 * Tests for {@link PdfTextGenerator}.
 *
 * <p>Each test loads a tagged PDF from {@code tagged-test-template.pdf} in test resources.
 *
 * <pre>
 *   StructureTreeRoot
 *     └─ Document
 *          └─ Page 0
 *               ├─ Div 0  (3 question-section kids — used by getDivInQuestionSection)
 *               └─ Div 1  (no kids — used as "last div" by getLastDivOfPage)
 * </pre>
 */
class PdfTextGeneratorTest {

  private final PdfTextGenerator generator = new PdfTextGenerator();

  private PDDocument document;

  @BeforeEach
  void setUp() throws IOException {
    try (InputStream stream = getClass().getResourceAsStream(TAGGED_PDF_RESOURCE)) {
      Assertions.assertNotNull(stream);
      document = Loader.loadPDF(stream.readAllBytes());
    }
  }

  @AfterEach
  void tearDown() throws IOException {
    if (document != null) {
      document.close();
    }
  }

  @Nested
  class AddWatermark {

    @Test
    void shallAddStructureElementToPageSection() throws IOException {
      final var pageKidsBefore = getPageElement().getKids().size();

      generator.addWatermark(document, "UTKAST", 1);

      assertEquals(pageKidsBefore + 1, getPageElement().getKids().size());
    }

    @Test
    void shallSetActualTextWithLowercaseTextPrefix() throws IOException {
      generator.addWatermark(document, "UTKAST", 1);

      final var newDiv = (PDStructureElement) getPageElement().getKids().getFirst();
      final var pElement = (PDStructureElement) newDiv.getKids().getFirst();

      assertEquals("Detta är ett utkast", pElement.getActualText());
    }
  }

  @Nested
  class AddMarginText {

    @Test
    void shallCreateNewDivOnPage() throws IOException {
      final var pageKidsBefore = getPageElement().getKids().size();

      generator.addMarginText(document, "Webcert/cert-123", 1);

      assertEquals(pageKidsBefore + 1, getPageElement().getKids().size());
    }

    @Test
    void shallSetActualTextInMarginStructure() throws IOException {
      generator.addMarginText(document, "Webcert/cert-123", 1);

      final var newDiv = (PDStructureElement) getPageElement().getKids().getLast();
      final var pElement = (PDStructureElement) newDiv.getKids().getFirst();

      assertEquals("Webcert/cert-123", pElement.getActualText());
    }
  }

  @Nested
  class DrawText {

    @Test
    void shallAddToQuestionSectionWhenTagIndexIsSet() throws IOException {
      final var targetSection = getQuestionSection(0);
      final var kidsBefore = targetSection.getKids().size();

      generator.drawText(document, buildTextInfo("Test text", 0, 0));

      assertEquals(kidsBefore + 1, getQuestionSection(0).getKids().size());
    }

    @Test
    void shallSetActualTextWhenTagIndexIsSet() throws IOException {
      generator.drawText(document, buildTextInfo("Hello", 0, 0));

      final var pElement = (PDStructureElement) getQuestionSection(0).getKids().getLast();

      assertEquals("Hello", pElement.getActualText());
    }

    @Test
    void shallCreateNewDivOnPageWhenTagIndexIsNull() throws IOException {
      final var pageKidsBefore = getPageElement().getKids().size();

      generator.drawText(document, buildTextInfo("Test text", 0, null));

      assertEquals(pageKidsBefore + 1, getPageElement().getKids().size());
    }

    private TextInfo buildTextInfo(String value, int pageIndex, Integer tagIndex) {
      return TextInfo.builder()
          .customText(
              CustomText.builder()
                  .value(value)
                  .x(100f)
                  .y(200f)
                  .appearance(Appearance.builder().style(FontStyle.NORMAL).fontSize(12f).build())
                  .pageIndex(pageIndex)
                  .tagIndex(tagIndex)
                  .build())
          .color(Color.black)
          .mcid(1)
          .build();
    }
  }

  // --- Structure traversal helpers ---

  private PDStructureElement getPageElement() {
    final var root = document.getDocumentCatalog().getStructureTreeRoot();
    final var documentElement = (PDStructureElement) root.getKids().getFirst();
    return (PDStructureElement) documentElement.getKids().getFirst();
  }

  /**
   * Returns the question-section kid at {@code index} inside the div with the most kids. Mirrors
   * the logic of {@link PdfAccessibilityUtil#getDivInQuestionSection}.
   */
  private PDStructureElement getQuestionSection(int index) {
    return (PDStructureElement) getFirstDivElement().getKids().get(index);
  }

  private PDStructureElement getFirstDivElement() {
    return (PDStructureElement) getPageElement().getKids().getFirst();
  }
}
