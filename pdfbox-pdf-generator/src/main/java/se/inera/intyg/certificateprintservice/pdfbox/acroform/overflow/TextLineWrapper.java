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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.stereotype.Component;

@Component
public class TextLineWrapper {

  public List<String> wrapLines(String text, PDFont font, float fontSize, float availableWidth)
      throws IOException {
    if (text == null || text.isEmpty()) {
      return List.of();
    }

    final var result = new ArrayList<String>();
    final var paragraphs = text.split("\n", -1);

    for (String paragraph : paragraphs) {
      if (paragraph.isEmpty()) {
        result.add("");
      } else {
        wrapParagraph(paragraph, font, fontSize, availableWidth, result);
      }
    }

    return result;
  }

  private void wrapParagraph(
      String paragraph, PDFont font, float fontSize, float availableWidth, List<String> result)
      throws IOException {
    final var words = paragraph.split(" ");
    final var currentLine = new StringBuilder();

    for (String word : words) {
      if (currentLine.isEmpty()) {
        if (getTextWidth(word, font, fontSize) > availableWidth) {
          breakLongWord(word, font, fontSize, availableWidth, result);
        } else {
          currentLine.append(word);
        }
      } else {
        final var candidate = currentLine + " " + word;
        if (getTextWidth(candidate, font, fontSize) <= availableWidth) {
          currentLine.append(" ").append(word);
        } else {
          result.add(currentLine.toString());
          currentLine.setLength(0);
          if (getTextWidth(word, font, fontSize) > availableWidth) {
            breakLongWord(word, font, fontSize, availableWidth, result);
          } else {
            currentLine.append(word);
          }
        }
      }
    }

    if (!currentLine.isEmpty()) {
      result.add(currentLine.toString());
    }
  }

  private void breakLongWord(
      String word, PDFont font, float fontSize, float availableWidth, List<String> result)
      throws IOException {
    var remaining = word;
    while (!remaining.isEmpty()) {
      var end = remaining.length();
      while (end > 1
          && getTextWidth(remaining.substring(0, end), font, fontSize) > availableWidth) {
        end--;
      }
      result.add(remaining.substring(0, end));
      remaining = remaining.substring(end);
    }
  }

  private float getTextWidth(String text, PDFont font, float fontSize) throws IOException {
    return font.getStringWidth(text) / 1000f * fontSize;
  }
}
