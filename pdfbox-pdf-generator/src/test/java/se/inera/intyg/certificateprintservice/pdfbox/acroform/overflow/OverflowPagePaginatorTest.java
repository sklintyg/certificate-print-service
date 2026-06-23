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

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.OverflowEntry;

class OverflowPagePaginatorTest {

  private OverflowPagePaginator paginator;
  private PDType1Font font;
  private PDType1Font boldFont;

  private static final float FONT_SIZE = 9f;
  private static final float LINE_SPACING = 1.2f;
  private static final float FIELD_WIDTH = 400f;
  private static final float FIELD_HEIGHT = 100f;
  private static final float TOP_MARGIN = 10f;

  @BeforeEach
  void setUp() {
    paginator =
        new OverflowPagePaginator(new TextLineWrapper(), new OverflowPageCapacityCalculator());
    font = new PDType1Font(FontName.HELVETICA);
    boldFont = new PDType1Font(FontName.HELVETICA_BOLD);
  }

  @Test
  void shouldReturnSinglePageWhenTextFits() throws IOException {
    final var entries = List.of(new OverflowEntry("Label", "Short text that fits on one page."));
    final var result =
        paginator.paginate(
            entries,
            font,
            boldFont,
            FONT_SIZE,
            FIELD_WIDTH,
            FIELD_HEIGHT,
            LINE_SPACING,
            TOP_MARGIN);

    assertEquals(1, result.size());
    assertTrue(result.get(0).stream().anyMatch(l -> l.text().equals("Label") && l.bold()));
    assertTrue(
        result.get(0).stream()
            .anyMatch(l -> l.text().equals("Short text that fits on one page.") && !l.bold()));
  }

  @Test
  void shouldReturnMultiplePagesWhenTextExceedsCapacity() throws IOException {
    final var content =
        IntStream.range(0, 50)
            .mapToObj(i -> "Line number " + i)
            .collect(java.util.stream.Collectors.joining("\n"));
    final var entries = List.of(new OverflowEntry("Label", content));
    final var result =
        paginator.paginate(
            entries,
            font,
            boldFont,
            FONT_SIZE,
            FIELD_WIDTH,
            FIELD_HEIGHT,
            LINE_SPACING,
            TOP_MARGIN);

    assertTrue(result.size() > 1);
  }

  @Test
  void shouldDistributeAllLinesAcrossPages() throws IOException {
    final var content =
        IntStream.range(0, 20)
            .mapToObj(i -> "Line " + i)
            .collect(java.util.stream.Collectors.joining("\n"));
    final var entries = List.of(new OverflowEntry("Label", content));
    final var result =
        paginator.paginate(
            entries,
            font,
            boldFont,
            FONT_SIZE,
            FIELD_WIDTH,
            FIELD_HEIGHT,
            LINE_SPACING,
            TOP_MARGIN);

    final var totalLines = result.stream().mapToInt(List::size).sum();
    // 1 label line + 20 content lines = 21
    assertEquals(21, totalLines);
  }

  @Test
  void shouldReturnEmptyListForEmptyEntries() throws IOException {
    final var result =
        paginator.paginate(
            List.of(),
            font,
            boldFont,
            FONT_SIZE,
            FIELD_WIDTH,
            FIELD_HEIGHT,
            LINE_SPACING,
            TOP_MARGIN);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldMarkLabelLinesBold() throws IOException {
    final var entries = List.of(new OverflowEntry("My Label", "My content"));
    final var result =
        paginator.paginate(
            entries,
            font,
            boldFont,
            FONT_SIZE,
            FIELD_WIDTH,
            FIELD_HEIGHT,
            LINE_SPACING,
            TOP_MARGIN);

    final var allLines = result.stream().flatMap(List::stream).toList();
    final var labelLine = allLines.stream().filter(l -> l.text().equals("My Label")).findFirst();
    assertTrue(labelLine.isPresent());
    assertTrue(labelLine.get().bold());
  }

  @Test
  void shouldMarkContentLinesNotBold() throws IOException {
    final var entries = List.of(new OverflowEntry("My Label", "My content"));
    final var result =
        paginator.paginate(
            entries,
            font,
            boldFont,
            FONT_SIZE,
            FIELD_WIDTH,
            FIELD_HEIGHT,
            LINE_SPACING,
            TOP_MARGIN);

    final var allLines = result.stream().flatMap(List::stream).toList();
    final var contentLine =
        allLines.stream().filter(l -> l.text().equals("My content")).findFirst();
    assertTrue(contentLine.isPresent());
    assertTrue(!contentLine.get().bold());
  }
}
