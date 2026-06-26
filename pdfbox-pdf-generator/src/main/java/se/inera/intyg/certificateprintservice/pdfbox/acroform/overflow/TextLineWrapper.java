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
    final var spaceWidth = getTextWidth(" ", font, fontSize);
    var currentLineWidth = 0f;

    for (String word : words) {
      final var wordWidth = getTextWidth(word, font, fontSize);

      if (wordWidth > availableWidth) {
        flushLine(currentLine, result);
        currentLineWidth = 0f;
        breakLongWord(word, font, fontSize, availableWidth, result);
        continue;
      }

      final var wordFitsOnLine =
          currentLine.isEmpty() || currentLineWidth + spaceWidth + wordWidth <= availableWidth;

      if (!wordFitsOnLine) {
        flushLine(currentLine, result);
        currentLine.append(word);
        currentLineWidth = wordWidth;
      } else {
        if (!currentLine.isEmpty()) {
          currentLine.append(" ");
          currentLineWidth += spaceWidth;
        }
        currentLine.append(word);
        currentLineWidth += wordWidth;
      }
    }

    flushLine(currentLine, result);
  }

  private void breakLongWord(
      String word, PDFont font, float fontSize, float availableWidth, List<String> result)
      throws IOException {
    var remaining = word;
    while (!remaining.isEmpty()) {
      final var fitLength = findMaxFittingLength(remaining, font, fontSize, availableWidth);
      result.add(remaining.substring(0, fitLength));
      remaining = remaining.substring(fitLength);
    }
  }

  private int findMaxFittingLength(String text, PDFont font, float fontSize, float availableWidth)
      throws IOException {
    int low = 1;
    int high = text.length();
    while (low < high) {
      final var mid = (low + high + 1) / 2;
      if (getTextWidth(text.substring(0, mid), font, fontSize) <= availableWidth) {
        low = mid;
      } else {
        high = mid - 1;
      }
    }
    return low;
  }

  private void flushLine(StringBuilder line, List<String> result) {
    if (!line.isEmpty()) {
      result.add(line.toString());
      line.setLength(0);
    }
  }

  private float getTextWidth(String text, PDFont font, float fontSize) throws IOException {
    return font.getStringWidth(text) / 1000f * fontSize;
  }
}
