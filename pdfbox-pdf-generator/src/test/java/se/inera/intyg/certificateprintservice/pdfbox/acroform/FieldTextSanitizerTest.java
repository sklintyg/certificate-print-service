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
package se.inera.intyg.certificateprintservice.pdfbox.acroform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FieldTextSanitizerTest {

  private static final PDType1Font HELVETICA = new PDType1Font(FontName.HELVETICA);

  @Nested
  class NullAndEmpty {

    @Test
    void shallReturnEmptyStringForNull() {
      assertEquals("", FieldTextSanitizer.sanitize(null, HELVETICA));
    }

    @Test
    void shallReturnEmptyStringForEmptyInput() {
      assertEquals("", FieldTextSanitizer.sanitize("", HELVETICA));
    }

    @Test
    void shallReturnEmptyStringForOnlySpaces() {
      assertEquals("", FieldTextSanitizer.sanitize("   ", HELVETICA));
    }
  }

  @Nested
  class ControlCharacters {

    @Test
    void shallReplaceTabWithSpace() {
      assertEquals("a b", FieldTextSanitizer.sanitize("a\tb", HELVETICA));
    }

    @Test
    void shallReplaceCarriageReturnWithSpace() {
      assertEquals("a b", FieldTextSanitizer.sanitize("a\rb", HELVETICA));
    }

    @Test
    void shallReplaceNullByteWithSpace() {
      assertEquals("a b", FieldTextSanitizer.sanitize("a\u0000b", HELVETICA));
    }

    @Test
    void shallReplaceVerticalTabWithSpace() {
      assertEquals("a b", FieldTextSanitizer.sanitize("a\u000Bb", HELVETICA));
    }

    @Test
    void shallReplaceFormFeedWithSpace() {
      assertEquals("a b", FieldTextSanitizer.sanitize("a\u000Cb", HELVETICA));
    }

    @Test
    void shallReplaceDeleteCharWithSpace() {
      assertEquals("a b", FieldTextSanitizer.sanitize("a\u007Fb", HELVETICA));
    }

    @Test
    void shallCollapseConsecutiveControlCharsIntoOneSpace() {
      assertEquals("a b", FieldTextSanitizer.sanitize("a\t\t\tb", HELVETICA));
    }

    @Test
    void shallTrimLeadingControlChars() {
      assertEquals("text", FieldTextSanitizer.sanitize("\ttext", HELVETICA));
    }

    @Test
    void shallTrimTrailingControlChars() {
      assertEquals("text", FieldTextSanitizer.sanitize("text\t", HELVETICA));
    }

    @Test
    void shallPreserveNewline() {
      assertEquals("line1\nline2", FieldTextSanitizer.sanitize("line1\nline2", HELVETICA));
    }

    @Test
    void shallPreserveNewlineWhileReplacingOtherControlChars() {
      assertEquals("line1\n line2", FieldTextSanitizer.sanitize("line1\n\tline2", HELVETICA));
    }
  }

  @Nested
  class UnicodeNormalization {

    @Test
    void shallReplaceHyphenU2010WithAsciiHyphen() {
      assertEquals("-", FieldTextSanitizer.sanitize("\u2010", HELVETICA));
    }

    @Test
    void shallReplaceNonBreakingHyphenU2011WithAsciiHyphen() {
      assertEquals("-", FieldTextSanitizer.sanitize("\u2011", HELVETICA));
    }

    @Test
    void shallReplaceFigureDashU2012WithAsciiHyphen() {
      assertEquals("-", FieldTextSanitizer.sanitize("\u2012", HELVETICA));
    }

    @Test
    void shallReplaceEnDashU2013WithAsciiHyphen() {
      assertEquals("-", FieldTextSanitizer.sanitize("\u2013", HELVETICA));
    }

    @Test
    void shallReplaceEmDashU2014WithAsciiHyphen() {
      assertEquals("-", FieldTextSanitizer.sanitize("\u2014", HELVETICA));
    }

    @Test
    void shallReplaceHorizontalBarU2015WithAsciiHyphen() {
      assertEquals("-", FieldTextSanitizer.sanitize("\u2015", HELVETICA));
    }

    @Test
    void shallReplaceMinusSignU2212WithAsciiHyphen() {
      assertEquals("-", FieldTextSanitizer.sanitize("\u2212", HELVETICA));
    }

    @Test
    void shallReplaceRightArrowU2192WithAsciiArrow() {
      assertEquals("->", FieldTextSanitizer.sanitize("\u2192", HELVETICA));
    }

    @Test
    void shallReplaceLeftArrowU2190WithAsciiArrow() {
      assertEquals("<-", FieldTextSanitizer.sanitize("\u2190", HELVETICA));
    }

    @Test
    void shallReplaceLeftRightArrowU2194WithAsciiArrow() {
      assertEquals("<->", FieldTextSanitizer.sanitize("\u2194", HELVETICA));
    }
  }

  @Nested
  class FontAwareFiltering {

    @Test
    void shallPassThroughCharactersSupportedByFont() {
      final var text = "Hello World 123 !@#";
      assertEquals(text, FieldTextSanitizer.sanitize(text, HELVETICA));
    }

    @Test
    void shallPassThroughSwedishCharactersSupportedByFont() {
      final var text = "Åsa Öberg är läkare";
      assertEquals(text, FieldTextSanitizer.sanitize(text, HELVETICA));
    }

    @Test
    void shallReplaceChineseCharacterNotSupportedByHelvetica() {
      assertEquals(" ", FieldTextSanitizer.sanitize("\u4E2D", HELVETICA));
    }

    @Test
    void shallReplaceEmojiNotSupportedByHelvetica() {
      assertEquals(" ", FieldTextSanitizer.sanitize("\uD83D\uDE00", HELVETICA));
    }

    @Test
    void shallReplaceUnsupportedCharsInMixedText() {
      assertEquals("Hello  World", FieldTextSanitizer.sanitize("Hello \u4E2DWorld", HELVETICA));
    }
  }

  @Nested
  class CombinedBehavior {

    @Test
    void shallApplyAllSanitizationStepsInOrder() {
      // \t → space (control char), \u2013 → - (unicode normalization), \u4E2D → space (font)
      assertEquals("a - b", FieldTextSanitizer.sanitize("a\t\u2013\tb", HELVETICA));
    }

    @Test
    void shallNotModifyCleanText() {
      final var clean = "Normal text med svenska tecken: åäö ÅÄÖ.";
      assertEquals(clean, FieldTextSanitizer.sanitize(clean, HELVETICA));
    }
  }
}
