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
package se.inera.intyg.certificateprintservice.pdfbox.acroform.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Pins the intent of each layout rule.
 *
 * <p>{@link FieldTextLayoutMatchesPdfBoxTest} proves that this class agrees with PDFBox, which is
 * the stronger guarantee, but it says so only in aggregate: if a rule were removed it would report
 * a line count disagreement without naming the rule, and a rule could stop being exercised
 * altogether if the template or PDFBox changed. These tests name each rule instead.
 */
class FieldTextLayoutTest {

  private static final PDType1Font HELVETICA = new PDType1Font(FontName.HELVETICA);
  private static final float FONT_SIZE = 10f;

  private final FieldTextLayout textLayout = new FieldTextLayout();

  private static FieldGeometry multiLine(float width, int maxLines) {
    return new FieldGeometry(HELVETICA, FONT_SIZE, width, maxLines, true);
  }

  private static FieldGeometry singleLine(float width) {
    return new FieldGeometry(HELVETICA, FONT_SIZE, width, 1, false);
  }

  private static float widthOf(String text) throws IOException {
    return HELVETICA.getStringWidth(text) / 1000f * FONT_SIZE;
  }

  @Nested
  class EmptyValues {

    @Test
    void shallCountOneLineForNull() throws IOException {
      assertEquals(1, textLayout.countLines(null, multiLine(100f, 5)));
    }

    @Test
    void shallCountOneLineForEmptyText() throws IOException {
      assertEquals(1, textLayout.countLines("", multiLine(100f, 5)));
    }

    @Test
    void shallFitNullValue() throws IOException {
      assertTrue(textLayout.fits(null, singleLine(1f)));
    }

    @Test
    void shallFitEmptyValue() throws IOException {
      assertTrue(textLayout.fits("", singleLine(1f)));
    }
  }

  @Nested
  class SingleLineFields {

    @Test
    void shallCountOneLineHoweverLongTheTextIs() throws IOException {
      assertEquals(1, textLayout.countLines("word ".repeat(200), singleLine(50f)));
    }

    @Test
    void shallFitTextNarrowerThanTheField() throws IOException {
      assertTrue(textLayout.fits("Hello", singleLine(widthOf("Hello") + 1f)));
    }

    @Test
    void shallNotFitTextWiderThanTheField() throws IOException {
      assertFalse(textLayout.fits("Hello", singleLine(widthOf("Hello") - 1f)));
    }

    /**
     * A single line field renders a line break as a glyph rather than starting a new line, so a
     * value containing one never fits however narrow the text is.
     */
    @Test
    void shallNotFitTextContainingALineBreakEvenWhenNarrowEnough() throws IOException {
      final var text = "a\nb";

      assertFalse(textLayout.fits(text, singleLine(widthOf("ab") + 100f)));
    }

    @Test
    void shallNotFitTextContainingACarriageReturn() throws IOException {
      final var text = "a\rb";

      assertFalse(textLayout.fits(text, singleLine(widthOf("ab") + 100f)));
    }
  }

  @Nested
  class WrappingBoundary {

    /**
     * PDFBox breaks when a line <em>reaches</em> the available width, not when it exceeds it. This
     * is the deliberate difference from {@link TextLineWrapper}; getting it wrong by one glyph
     * pushes a line of text outside the field.
     */
    @Test
    void shallWrapWhenTheTextExactlyReachesTheAvailableWidth() throws IOException {
      final var text = "aaa bbb";

      assertEquals(2, textLayout.countLines(text, multiLine(widthOf(text), 5)));
    }

    @Test
    void shallNotWrapWhenTheTextStaysJustUnderTheAvailableWidth() throws IOException {
      final var text = "aaa bbb";

      assertEquals(1, textLayout.countLines(text, multiLine(widthOf(text) + 0.1f, 5)));
    }

    /**
     * The space that ends a line is not drawn, so its width is discounted before deciding whether
     * the line is full.
     */
    @Test
    void shallNotLetTrailingWhitespaceOnTheLastWordForceAWrap() throws IOException {
      final var geometry = multiLine(widthOf("aaa bbb") + 0.1f, 5);

      assertEquals(1, textLayout.countLines("aaa bbb ", geometry));
    }
  }

  @Nested
  class Paragraphs {

    @Test
    void shallCountOneLinePerParagraph() throws IOException {
      assertEquals(3, textLayout.countLines("one\ntwo\nthree", multiLine(500f, 10)));
    }

    /**
     * A blank line between two paragraphs occupies a line of its own. PDFBox renders it as a space;
     * either way it must be counted, or the text is taller than we predicted.
     */
    @Test
    void shallCountAnEmptyParagraphAsALine() throws IOException {
      assertEquals(3, textLayout.countLines("one\n\ntwo", multiLine(500f, 10)));
    }

    /**
     * Trailing blank lines are dropped rather than counted. That is surprising, but it is exactly
     * what PDFBox does — it splits on {@code \R} with the same trailing-empty-discarding semantics
     * — and agreeing with the renderer matters more than being intuitive.
     */
    @Test
    void shallIgnoreTrailingBlankLines() throws IOException {
      assertEquals(1, textLayout.countLines("one\n\n", multiLine(500f, 10)));
    }

    @Test
    void shallTreatCarriageReturnLineFeedAsASingleParagraphBreak() throws IOException {
      assertEquals(2, textLayout.countLines("one\r\ntwo", multiLine(500f, 10)));
    }

    /** PDFBox converts tabs to spaces before measuring, so a tab must measure as a space. */
    @Test
    void shallMeasureATabAsASpace() throws IOException {
      final var geometry = multiLine(widthOf("aaa bbb") - 0.1f, 5);

      assertEquals(2, textLayout.countLines("aaa bbb", geometry));
      assertEquals(2, textLayout.countLines("aaa\tbbb", geometry));
    }

    @Test
    void shallNotFitWhenParagraphsNeedMoreLinesThanAreAvailable() throws IOException {
      assertFalse(textLayout.fits("one\ntwo\nthree", multiLine(500f, 2)));
    }

    @Test
    void shallFitWhenParagraphsUseExactlyTheAvailableLines() throws IOException {
      assertTrue(textLayout.fits("one\ntwo\nthree", multiLine(500f, 3)));
    }
  }

  @Nested
  class UnbreakableWords {

    @Test
    void shallSplitAWordWiderThanTheFieldAcrossLines() throws IOException {
      final var word = "a".repeat(10);
      final var geometry = multiLine(widthOf("aaaaa"), 10);

      assertEquals(2, textLayout.countLines(word, geometry));
    }

    /**
     * A field too narrow for even one character must still consume a character per line, otherwise
     * the layout would never terminate.
     */
    @Test
    void shallConsumeAtLeastOneCharacterPerLineInAnImpossiblyNarrowField() throws IOException {
      final var narrowerThanOneCharacter = widthOf("a") / 2;

      final var geometry = multiLine(narrowerThanOneCharacter, 10);

      assertEquals(4, textLayout.countLines("abcd", geometry));
    }

    /** A field with no width at all is not "very narrow", it is nonsense, and is rejected. */
    @Test
    void shallRejectAFieldWithNoWidthAtAll() {
      assertThrows(IllegalArgumentException.class, () -> multiLine(0f, 10));
    }

    @Test
    void shallRejectAFieldWithNegativeWidth() {
      assertThrows(IllegalArgumentException.class, () -> multiLine(-3f, 10));
    }
  }
}
