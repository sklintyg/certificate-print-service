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
package se.inera.intyg.certificateprintservice.pdfbox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.util.Matrix;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PdfTextGenerator}.
 *
 * <p>Each test loads a tagged PDF from {@code tagged-test-template.pdf} in test resources.
 * If the resource is absent (e.g. during initial development), a programmatic fallback creates the
 * required structure:
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

  private static final String TAGGED_PDF_RESOURCE = "/tagged-test-template.pdf";

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
  class AddTopWatermark {

    @Test
    void shallCreateNewDivWhenAddInExistingTopTagIsFalse() throws IOException {
      final var pageKidsBefore = getPageElement().getKids().size();

      generator.addTopWatermark(
          document, "MAKULERAT", Matrix.getTranslateInstance(40, 685), 22f, 1, false);

      assertEquals(pageKidsBefore + 1, getPageElement().getKids().size());
    }

    @Test
    void shallReuseFirstDivWhenAddInExistingTopTagIsTrue() throws IOException {
      final var pageKidsBefore = getPageElement().getKids().size();
      final var firstDivKidsBefore = getFirstDivElement().getKids().size();

      generator.addTopWatermark(
          document, "MAKULERAT", Matrix.getTranslateInstance(40, 685), 22f, 1, true);

      assertEquals(pageKidsBefore, getPageElement().getKids().size());
      assertEquals(firstDivKidsBefore + 1, getFirstDivElement().getKids().size());
    }

    @Test
    void shallSetActualTextInStructure() throws IOException {
      generator.addTopWatermark(
          document, "MAKULERAT", Matrix.getTranslateInstance(40, 685), 22f, 1, false);

      // New div was inserted at index 0; its first kid is the P element
      final var newDiv = (PDStructureElement) getPageElement().getKids().getFirst();
      final var pElement = (PDStructureElement) newDiv.getKids().getFirst();

      assertEquals("MAKULERAT", pElement.getActualText());
    }
  }

  @Nested
  class AddMarginText {

    @Test
    void shallAddStructureElementToLastDiv() throws IOException {
      final var lastDivKidsBefore = getLastDivElement().getKids().size();

      generator.addMarginText(document, "Webcert/cert-123", 1, 0);

      assertEquals(lastDivKidsBefore + 1, getLastDivElement().getKids().size());
    }

    @Test
    void shallSetActualTextInMarginStructure() throws IOException {
      generator.addMarginText(document, "Webcert/cert-123", 1, 0);

      final var lastDiv = getLastDivElement();
      final var pElement = (PDStructureElement) lastDiv.getKids().getLast();

      assertEquals("Webcert/cert-123", pElement.getActualText());
    }
  }

  @Nested
  class AddDigitalSignatureText {

    @Test
    void shallAddStructureElementToCorrectQuestionSectionIndex() throws IOException {
      final var targetSection = getQuestionSection(0);
      final var kidsBefore = targetSection.getKids().size();

      generator.addDigitalSignatureText(document, "Signatur", 100f, 200f, 1, 0, 0);

      assertEquals(kidsBefore + 1, getQuestionSection(0).getKids().size());
    }

    @Test
    void shallSetActualTextInSignatureStructure() throws IOException {
      generator.addDigitalSignatureText(document, "Signatur", 100f, 200f, 1, 0, 0);

      final var pElement = (PDStructureElement) getQuestionSection(0).getKids().getLast();

      assertEquals("Signatur", pElement.getActualText());
    }
  }

  @Nested
  class AddSentText {

    @Test
    void shallAddStructureElementForSentText() throws IOException {
      final var pageKidsBefore = getPageElement().getKids().size();

      generator.addSentText(document, "Skickad till Försäkringskassan", 1);

      assertEquals(pageKidsBefore + 1, getPageElement().getKids().size());
    }
  }

  @Nested
  class AddSentVisibilityText {

    @Test
    void shallReuseFirstDivForVisibilityText() throws IOException {
      final var pageKidsBefore = getPageElement().getKids().size();
      final var firstDivKidsBefore = getFirstDivElement().getKids().size();

      generator.addSentVisibilityText(document, "Intyget är tillgängligt för patienten", 1);

      assertEquals(pageKidsBefore, getPageElement().getKids().size());
      assertEquals(firstDivKidsBefore + 1, getFirstDivElement().getKids().size());
    }
  }

  // --- Structure traversal helpers ---

  private PDStructureElement getPageElement() {
    final var root = document.getDocumentCatalog().getStructureTreeRoot();
    final var documentElement = (PDStructureElement) root.getKids().getFirst();
    return (PDStructureElement) documentElement.getKids().getFirst();
  }

  private PDStructureElement getFirstDivElement() {
    return (PDStructureElement) getPageElement().getKids().getFirst();
  }

  private PDStructureElement getLastDivElement() {
    return (PDStructureElement) getPageElement().getKids().getLast();
  }

  /**
   * Returns the question-section kid at {@code index} inside the div with the most kids. Mirrors
   * the logic of {@link PdfAccessibilityUtil#getDivInQuestionSection}.
   */
  private PDStructureElement getQuestionSection(int index) {
    return (PDStructureElement) getFirstDivElement().getKids().get(index);
  }
}
