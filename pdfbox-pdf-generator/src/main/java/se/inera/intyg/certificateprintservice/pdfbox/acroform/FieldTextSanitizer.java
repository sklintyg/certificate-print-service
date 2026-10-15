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

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.font.PDFont;

@Slf4j
public class FieldTextSanitizer {

  private static final Map<String, String> UNICODE_NORMALIZATION_MAP =
      Map.of(
          "\u2010", "-", // hyphen
          "\u2011", "-", // non-breaking hyphen
          "\u2012", "-", // figure dash
          "\u2013", "-", // en dash
          "\u2014", "-", // em dash
          "\u2015", "-", // horizontal bar
          "\u2212", "-", // minus sign
          "\u2192", "->", // right arrow
          "\u2190", "<-", // left arrow
          "\u2194", "<->" // left-right arrow
          );

  private FieldTextSanitizer() {
    throw new IllegalStateException("Utility class");
  }

  public static String sanitize(String text, PDFont font) {
    if (text == null || text.isEmpty()) {
      return "";
    }

    final var stripped = stripControlCharacters(text);
    final var normalized = normalizeUnicodeCharacters(stripped);
    return filterUnsupportedFontCharacters(normalized, font);
  }

  private static String stripControlCharacters(String text) {
    return text.replaceAll("[\\t\\r\\x00-\\x08\\x0B-\\x0C\\x0E-\\x1F\\x7F]+", " ").trim();
  }

  private static String normalizeUnicodeCharacters(String text) {
    return UNICODE_NORMALIZATION_MAP.entrySet().stream()
        .reduce(
            text,
            (result, entry) -> result.replace(entry.getKey(), entry.getValue()),
            (s1, s2) -> s2);
  }

  private static String filterUnsupportedFontCharacters(String text, PDFont font) {
    return text.codePoints()
        .mapToObj(cp -> toSupportedString(cp, font))
        .collect(Collectors.joining());
  }

  private static String toSupportedString(int codePoint, PDFont font) {
    if (codePoint == '\n') {
      return "\n";
    }

    final var character = new String(Character.toChars(codePoint));
    try {
      font.encode(character);
      return character;
    } catch (IOException | IllegalArgumentException e) {
      log.warn(
          "Character '{}' with unicode 'U+{}' cannot be encoded in font '{}', replacing with space.",
          character,
          Integer.toHexString(codePoint).toUpperCase(),
          font.getName());
      return " ";
    }
  }
}
