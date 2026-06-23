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
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.OverflowEntry;

@Component
@RequiredArgsConstructor
public class OverflowPagePaginator {

  private final TextLineWrapper textLineWrapper;
  private final OverflowPageCapacityCalculator capacityCalculator;

  public List<List<OverflowLine>> paginate(
      List<OverflowEntry> entries,
      PDFont font,
      PDFont boldFont,
      float fontSize,
      float fieldWidth,
      float fieldHeight,
      float lineSpacing,
      float topMargin)
      throws IOException {
    final var allLines = buildLines(entries, font, boldFont, fontSize, fieldWidth);
    if (allLines.isEmpty()) {
      return List.of();
    }

    final var maxLinesPerPage =
        capacityCalculator.calculateMaxLines(fieldHeight, fontSize, lineSpacing, topMargin);
    if (maxLinesPerPage <= 0) {
      return List.of(allLines);
    }

    final var pages = new ArrayList<List<OverflowLine>>();
    var fromIndex = 0;

    while (fromIndex < allLines.size()) {
      final var toIndex = Math.min(fromIndex + maxLinesPerPage, allLines.size());
      pages.add(new ArrayList<>(allLines.subList(fromIndex, toIndex)));
      fromIndex = toIndex;
    }

    return pages;
  }

  private List<OverflowLine> buildLines(
      List<OverflowEntry> entries, PDFont font, PDFont boldFont, float fontSize, float fieldWidth)
      throws IOException {
    final var allLines = new ArrayList<OverflowLine>();

    for (var i = 0; i < entries.size(); i++) {
      final var entry = entries.get(i);

      for (final var labelLine :
          textLineWrapper.wrapLines(entry.label(), boldFont, fontSize, fieldWidth)) {
        allLines.add(new OverflowLine(labelLine, true));
      }

      for (final var contentLine :
          textLineWrapper.wrapLines(entry.content(), font, fontSize, fieldWidth)) {
        allLines.add(new OverflowLine(contentLine, false));
      }

      if (notLastEntry(entries, i)) {
        allLines.add(new OverflowLine("", false));
        allLines.add(new OverflowLine("", false));
      }
    }

    return allLines;
  }

  private static boolean notLastEntry(List<OverflowEntry> entries, int i) {
    return i < entries.size() - 1;
  }
}
