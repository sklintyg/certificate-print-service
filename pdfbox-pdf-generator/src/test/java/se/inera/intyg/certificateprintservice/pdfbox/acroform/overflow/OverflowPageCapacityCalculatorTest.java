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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OverflowPageCapacityCalculatorTest {

  private OverflowPageCapacityCalculator calculator;

  @BeforeEach
  void setUp() {
    calculator = new OverflowPageCapacityCalculator();
  }

  @Test
  void shouldCalculateMaxLinesForGivenFieldHeightAndFontSize() {
    final var fieldHeight = 700f;
    final var fontSize = 10f;
    final var lineSpacing = 1.2f;

    final var result = calculator.calculateMaxLines(fieldHeight, fontSize, lineSpacing);

    final var expectedLineHeight = fontSize * lineSpacing;
    final var expected = (int) Math.floor(fieldHeight / expectedLineHeight);
    assertEquals(expected, result);
  }

  @Test
  void shouldReturnAtLeastOneLineForSmallHeight() {
    final var result = calculator.calculateMaxLines(12f, 10f, 1.2f);
    assertTrue(result >= 1);
  }

  @Test
  void shouldAccountForTopMargin() {
    final var fieldHeight = 700f;
    final var fontSize = 10f;
    final var lineSpacing = 1.2f;
    final var topMargin = 10f;

    final var resultWithMargin =
        calculator.calculateMaxLines(fieldHeight, fontSize, lineSpacing, topMargin);
    final var resultWithoutMargin =
        calculator.calculateMaxLines(fieldHeight, fontSize, lineSpacing);

    assertTrue(resultWithMargin < resultWithoutMargin);
  }

  @Test
  void shouldReturnZeroWhenHeightIsTooSmallForAnyLine() {
    final var result = calculator.calculateMaxLines(5f, 10f, 1.2f);
    assertEquals(0, result);
  }
}
