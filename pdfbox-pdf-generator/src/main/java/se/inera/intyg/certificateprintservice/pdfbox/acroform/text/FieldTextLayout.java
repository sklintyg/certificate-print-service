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

import java.io.IOException;
import java.text.BreakIterator;
import org.springframework.stereotype.Component;

/**
 * Answers whether a value fits inside a form field, by reproducing the line breaking PDFBox itself
 * performs when it generates the field appearance.
 *
 * <p>This deliberately mirrors {@code PlainText.Paragraph#getLines} from PDFBox rather than reusing
 * {@link TextLineWrapper}. The two differ in ways that matter here: PDFBox breaks on every
 * opportunity a {@link BreakIterator} reports (hyphens and dashes, not only spaces) and it breaks
 * when a line <em>reaches</em> the available width rather than when it exceeds it. A more
 * permissive estimate would report that a value fits on N lines where PDFBox renders N+1, pushing
 * text outside the field — precisely the defect this fitting logic exists to prevent.
 *
 * <p>{@link TextLineWrapper} remains correct for overflow pages, where we draw the text ourselves.
 */
@Component
public class FieldTextLayout {

  private static final float GLYPH_SPACE_SCALE = 1000f;
  private static final String PARAGRAPH_SEPARATOR = "\\R";
  private static final String EMPTY_PARAGRAPH = " ";

  public boolean fits(String text, FieldGeometry geometry) throws IOException {
    if (text == null || text.isEmpty()) {
      return true;
    }

    if (!geometry.multiLine()) {
      return !containsLineBreak(text) && width(text, geometry) <= geometry.availableWidth();
    }

    return countLines(text, geometry) <= geometry.maxLines();
  }

  public int countLines(String text, FieldGeometry geometry) throws IOException {
    if (text == null || text.isEmpty()) {
      return 1;
    }

    if (!geometry.multiLine()) {
      return 1;
    }

    var lines = 0;
    for (final var paragraph : paragraphs(text)) {
      lines += countParagraphLines(paragraph, geometry);
    }
    return lines;
  }

  /**
   * PDFBox turns tabs into spaces, splits on any line break, and renders an empty paragraph as a
   * space.
   */
  private String[] paragraphs(String text) {
    final var paragraphs = text.replace('\t', ' ').split(PARAGRAPH_SEPARATOR);
    for (var i = 0; i < paragraphs.length; i++) {
      if (paragraphs[i].isEmpty()) {
        paragraphs[i] = EMPTY_PARAGRAPH;
      }
    }
    return paragraphs;
  }

  /**
   * Port of {@code PlainText.Paragraph#getLines}, counting lines instead of building them. The
   * BreakIterator is created exactly as PDFBox creates it, default locale included, so that the two
   * agree on where a line may be broken.
   */
  private int countParagraphLines(String paragraph, FieldGeometry geometry) throws IOException {
    final var availableWidth = geometry.availableWidth();
    final var iterator = BreakIterator.getLineInstance();
    iterator.setText(paragraph);

    var start = iterator.first();
    var end = iterator.next();
    var lineWidth = 0f;
    var lines = 0;
    var lineIsEmpty = true;

    while (end != BreakIterator.DONE) {
      var word = paragraph.substring(start, end);
      var wordWidth = width(word, geometry);
      var wordNeedsSplit = false;
      var splitOffset = end - start;

      lineWidth = lineWidth + wordWidth;

      if (lineWidth >= availableWidth && Character.isWhitespace(word.charAt(word.length() - 1))) {
        lineWidth -= width(word.substring(word.length() - 1), geometry);
      }

      if (lineWidth >= availableWidth && !lineIsEmpty) {
        lines++;
        lineIsEmpty = true;
        lineWidth = width(word, geometry);
      }

      if (word.length() > 1 && wordWidth > availableWidth && lineIsEmpty) {
        wordNeedsSplit = true;
        splitOffset = maxFittingChars(word, geometry);
        word = word.substring(0, splitOffset);
        lineWidth = width(word, geometry);
      }

      lineIsEmpty = false;

      if (wordNeedsSplit) {
        start = start + splitOffset;
      } else {
        start = end;
        end = iterator.next();
      }
    }

    return lines + 1;
  }

  /** Largest number of leading characters that fit the width, but never fewer than one. */
  private int maxFittingChars(String word, FieldGeometry geometry) throws IOException {
    var low = 1;
    var high = word.length();
    while (low < high) {
      final var mid = (low + high + 1) / 2;
      if (width(word.substring(0, mid), geometry) <= geometry.availableWidth()) {
        low = mid;
      } else {
        high = mid - 1;
      }
    }
    return low;
  }

  private boolean containsLineBreak(String text) {
    return text.indexOf('\n') >= 0 || text.indexOf('\r') >= 0;
  }

  private float width(String text, FieldGeometry geometry) throws IOException {
    return geometry.font().getStringWidth(text) / GLYPH_SPACE_SCALE * geometry.fontSize();
  }
}
