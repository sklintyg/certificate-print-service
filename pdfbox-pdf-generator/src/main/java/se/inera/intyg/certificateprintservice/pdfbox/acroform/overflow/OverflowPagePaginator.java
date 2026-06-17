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

@Component
@RequiredArgsConstructor
public class OverflowPagePaginator {

  private final TextLineWrapper textLineWrapper;
  private final OverflowPageCapacityCalculator capacityCalculator;

  public List<List<String>> paginate(
      String text,
      PDFont font,
      float fontSize,
      float fieldWidth,
      float fieldHeight,
      float lineSpacing,
      float topMargin)
      throws IOException {
    final var wrappedLines = textLineWrapper.wrapLines(text, font, fontSize, fieldWidth);
    if (wrappedLines.isEmpty()) {
      return List.of();
    }

    final var maxLinesPerPage =
        capacityCalculator.calculateMaxLines(fieldHeight, fontSize, lineSpacing, topMargin);
    if (maxLinesPerPage <= 0) {
      return List.of(wrappedLines);
    }

    final var pages = new ArrayList<List<String>>();
    var fromIndex = 0;

    while (fromIndex < wrappedLines.size()) {
      final var toIndex = Math.min(fromIndex + maxLinesPerPage, wrappedLines.size());
      pages.add(new ArrayList<>(wrappedLines.subList(fromIndex, toIndex)));
      fromIndex = toIndex;
    }

    return pages;
  }
}
