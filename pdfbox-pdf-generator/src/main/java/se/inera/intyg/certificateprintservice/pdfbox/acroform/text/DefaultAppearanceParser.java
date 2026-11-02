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

/**
 * Parses the parts we need out of a PDF default appearance (/DA) string.
 *
 * <p>A /DA string is a content stream fragment such as {@code /Helv 9 Tf 0 g}. The font name and
 * size are the two operands of the {@code Tf} operator, which is not necessarily the first operator
 * in the string, so they are located by scanning for that operator rather than by fixed position.
 */
public class DefaultAppearanceParser {

  private static final String SET_FONT_OPERATOR = "Tf";
  private static final int FONT_NAME_OFFSET = 2;
  private static final int FONT_SIZE_OFFSET = 1;

  private DefaultAppearanceParser() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Returns the font size of the {@code Tf} operator. A size of {@code 0} is legal and means the
   * viewer auto-sizes the text to fit the field.
   */
  public static float fontSize(String defaultAppearance) {
    return Float.parseFloat(operand(defaultAppearance, FONT_SIZE_OFFSET));
  }

  /** Returns the font resource name of the {@code Tf} operator, without the leading slash. */
  public static String fontName(String defaultAppearance) {
    final var name = operand(defaultAppearance, FONT_NAME_OFFSET);
    return name.startsWith("/") ? name.substring(1) : name;
  }

  private static String operand(String defaultAppearance, int offsetBeforeOperator) {
    if (defaultAppearance == null || defaultAppearance.isBlank()) {
      throw new IllegalArgumentException("Default appearance is missing, cannot determine font");
    }

    final var tokens = defaultAppearance.trim().split("\\s+");
    for (var i = tokens.length - 1; i >= offsetBeforeOperator; i--) {
      if (SET_FONT_OPERATOR.equals(tokens[i])) {
        return tokens[i - offsetBeforeOperator];
      }
    }

    throw new IllegalArgumentException(
        "Default appearance '%s' has no %s operator, cannot determine font"
            .formatted(defaultAppearance, SET_FONT_OPERATOR));
  }
}
