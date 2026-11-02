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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DefaultAppearanceParserTest {

  @Test
  void shouldReturnFontSizeFromSetFontOperator() {
    assertEquals(9f, DefaultAppearanceParser.fontSize("/Helv 9 Tf 0 g"));
  }

  @Test
  void shouldReturnFontNameWithoutLeadingSlash() {
    assertEquals("Helv", DefaultAppearanceParser.fontName("/Helv 9 Tf 0 g"));
  }

  @Test
  void shouldReturnFontSizeWhenSetFontOperatorIsNotFirst() {
    assertEquals(11.5f, DefaultAppearanceParser.fontSize("0 0 0 rg /ArialMT 11.5 Tf"));
  }

  @Test
  void shouldReturnFontNameWhenSetFontOperatorIsNotFirst() {
    assertEquals("ArialMT", DefaultAppearanceParser.fontName("0 0 0 rg /ArialMT 11.5 Tf"));
  }

  @Test
  void shouldReturnZeroForAutoSizedFont() {
    assertEquals(0f, DefaultAppearanceParser.fontSize("/Helv 0 Tf 0 g"));
  }

  @Test
  void shouldIgnoreSurroundingWhitespace() {
    assertEquals(8f, DefaultAppearanceParser.fontSize("   /Helv   8   Tf   "));
  }

  @Test
  void shouldThrowWhenAppearanceIsNull() {
    assertThrows(IllegalArgumentException.class, () -> DefaultAppearanceParser.fontSize(null));
  }

  @Test
  void shouldThrowWhenAppearanceIsBlank() {
    assertThrows(IllegalArgumentException.class, () -> DefaultAppearanceParser.fontSize("  "));
  }

  @Test
  void shouldThrowWhenSetFontOperatorIsMissing() {
    assertThrows(IllegalArgumentException.class, () -> DefaultAppearanceParser.fontSize("0 g"));
  }
}
