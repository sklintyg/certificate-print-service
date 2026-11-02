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

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * The usable text area of a form field, expressed in the terms that decide whether a value fits:
 * how wide a line may be, how many lines there is room for, and the font used to measure.
 *
 * @param font font the field renders with
 * @param fontSize font size in points, always greater than zero
 * @param availableWidth width in points available to a single line of text, always greater than
 *     zero
 * @param maxLines number of lines that fit vertically, always at least one
 * @param multiLine whether the field wraps text across lines
 */
public record FieldGeometry(
    PDFont font, float fontSize, float availableWidth, int maxLines, boolean multiLine) {

  public FieldGeometry {
    if (font == null) {
      throw new IllegalArgumentException("Cannot be null!");
    }
    if (fontSize <= 0) {
      throw new IllegalArgumentException(
          "Font size must be greater than zero, was %s".formatted(fontSize));
    }
    if (availableWidth <= 0) {
      throw new IllegalArgumentException(
          "Available width must be greater than zero, was %s".formatted(availableWidth));
    }
    if (maxLines < 1) {
      throw new IllegalArgumentException(
          "Max lines must be at least one, was %s".formatted(maxLines));
    }
  }
}
