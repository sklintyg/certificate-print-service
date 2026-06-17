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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TextLineWrapperTest {

  private static final float FONT_SIZE = 9f;
  private static final float FIELD_WIDTH = 200f;
  private PDType1Font font;
  private TextLineWrapper wrapper;

  @BeforeEach
  void setUp() {
    font = new PDType1Font(FontName.HELVETICA);
    wrapper = new TextLineWrapper();
  }

  @Test
  void shouldReturnEmptyListForEmptyText() throws IOException {
    final var result = wrapper.wrapLines("", font, FONT_SIZE, FIELD_WIDTH);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnSingleLineWhenTextFitsWithinWidth() throws IOException {
    final var result = wrapper.wrapLines("Short text", font, FONT_SIZE, FIELD_WIDTH);
    assertEquals(List.of("Short text"), result);
  }

  @Test
  void shouldWrapAtWordBoundaryWhenTextExceedsWidth() throws IOException {
    final var longText = "This is a long text that should be wrapped at a word boundary";
    final var result = wrapper.wrapLines(longText, font, FONT_SIZE, 100f);
    assertTrue(result.size() > 1);
    for (String line : result) {
      assertTrue(line.length() > 0);
    }
  }

  @Test
  void shouldPreserveExplicitLineBreaks() throws IOException {
    final var text = "First line\nSecond line";
    final var result = wrapper.wrapLines(text, font, FONT_SIZE, FIELD_WIDTH);
    assertEquals(2, result.size());
    assertEquals("First line", result.get(0));
    assertEquals("Second line", result.get(1));
  }

  @Test
  void shouldHandleLongWordThatExceedsWidth() throws IOException {
    final var longWord = "Superlongwordthatcannotbewrappedataspaceboundary";
    final var result = wrapper.wrapLines(longWord, font, FONT_SIZE, 50f);
    assertTrue(result.size() >= 1);
    final var rejoined = String.join("", result);
    assertEquals(longWord, rejoined);
  }

  @Test
  void shouldHandleMultipleLineBreaksAndWrapping() throws IOException {
    final var text = "Line one\nThis is a longer line that needs wrapping at boundaries";
    final var result = wrapper.wrapLines(text, font, FONT_SIZE, 100f);
    assertTrue(result.size() >= 3);
    assertEquals("Line one", result.get(0));
  }

  @Test
  void shouldReturnSingleEmptyLineForNewlineOnly() throws IOException {
    final var result = wrapper.wrapLines("\n", font, FONT_SIZE, FIELD_WIDTH);
    assertEquals(2, result.size());
    assertEquals("", result.get(0));
    assertEquals("", result.get(1));
  }
}
