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

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OverflowPageRendererTest {

  private OverflowPageRenderer renderer;
  private PDType1Font font;

  private static final float FONT_SIZE = 9f;
  private static final PDRectangle FIELD_RECT = new PDRectangle(50f, 50f, 400f, 700f);

  @BeforeEach
  void setUp() {
    renderer = new OverflowPageRenderer(new OverflowPageStructureCloner());
    font = new PDType1Font(FontName.HELVETICA);
  }

  @Test
  void shouldNotAddPagesWhenOnlySinglePage() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var initialPageCount = document.getNumberOfPages();
      final var pages = List.of(List.of("Line 1", "Line 2"));

      renderer.renderAllOverflowPages(document, 0, pages, font, FONT_SIZE, FIELD_RECT);

      assertEquals(initialPageCount, document.getNumberOfPages());
    }
  }

  @Test
  void shouldAddOnePageWhenTwoPageChunksProvided() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var initialPageCount = document.getNumberOfPages();
      final var pages = List.of(List.of("Line 1", "Line 2"), List.of("Line 3", "Line 4"));

      renderer.renderAllOverflowPages(document, 0, pages, font, FONT_SIZE, FIELD_RECT);

      assertEquals(initialPageCount + 1, document.getNumberOfPages());
    }
  }

  @Test
  void shouldAddMultiplePagesWhenManyChunksProvided() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var initialPageCount = document.getNumberOfPages();
      final var pages = List.of(List.of("Line 1"), List.of("Line 2"), List.of("Line 3"));

      renderer.renderAllOverflowPages(document, 0, pages, font, FONT_SIZE, FIELD_RECT);

      assertEquals(initialPageCount + 2, document.getNumberOfPages());
    }
  }

  @Test
  void shouldUseMediaBoxFromOverflowPage() throws IOException {
    try (final var document = loadTestTemplate()) {
      final var overflowPage = document.getPage(0);
      final var expectedWidth = overflowPage.getMediaBox().getWidth();
      final var pages = List.of(List.of("Line 1"), List.of("Line 2"));

      renderer.renderAllOverflowPages(document, 0, pages, font, FONT_SIZE, FIELD_RECT);

      final var newPage = document.getPage(document.getNumberOfPages() - 1);
      assertEquals(expectedWidth, newPage.getMediaBox().getWidth());
    }
  }

  private org.apache.pdfbox.pdmodel.PDDocument loadTestTemplate() throws IOException {
    final var templateBytes =
        Objects.requireNonNull(getClass().getResourceAsStream("/tagged-test-template-overflow.pdf"))
            .readAllBytes();
    return Loader.loadPDF(templateBytes);
  }
}
