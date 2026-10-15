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
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.FieldGeometry;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.FieldTextLayout;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;

/**
 * Fits a field value to the space the field actually has.
 *
 * <p>A value that does not fit is either split — the part that fits stays in the field, the rest is
 * carried to an extension page — or, when the field has no extension page configured, fitted in
 * place: line breaks are given up first, and the value is truncated only if it still does not fit.
 * Text that has nowhere else to go must not be lost just to keep its layout.
 *
 * <p>A field marked {@code skipTextFitting} opts out: its value is written verbatim, keeping its
 * line breaks and its full length, and any clipping is left to the renderer. Pre-formatted values
 * such as the care unit's name, address and phone number are laid out by the consumer, not by us.
 * An overflow sheet still takes precedence over the opt-out, because text must never be lost.
 *
 * <p>The split point is found by measurement rather than by counting characters: the longest prefix
 * that still leaves room for the suffix is located by binary search over {@link FieldTextLayout},
 * then moved back to a word boundary. Because a shorter prefix can never need more lines, moving
 * back to a word boundary cannot break the fit.
 *
 * <p>A field too small to show any of the value beside the ellipsis keeps the value unchanged and
 * leaves the clipping to the renderer, since an ellipsis alone conveys nothing.
 */
@Component
@RequiredArgsConstructor
public class FieldValueProcessor {

  private static final String LONG_OVERFLOW_SUFFIX = "... Se fortsättningsblad!";
  private static final String SHORT_OVERFLOW_SUFFIX = "...";
  private static final String TRUNCATION_SUFFIX = "...";
  private static final String OVERFLOW_REMAINDER_PREFIX = "... ";
  private static final String SUFFIX_SEPARATOR = " ";
  private static final int NOTHING_FITS = 0;
  private static final Pattern LINE_BREAKS = Pattern.compile("\\s*\\R\\s*");
  private static final String LINE_BREAK_REPLACEMENT = " ";

  private final FieldTextLayout textLayout;

  public FieldValueResult process(
      CustomPdfField field, PDFont font, Optional<FieldGeometry> geometry) throws IOException {
    final var value = FieldTextSanitizer.sanitize(field.value(), font);

    if (geometry.isEmpty() || textLayout.fits(value, geometry.get())) {
      return new FieldValueResult(value, null);
    }

    if (hasOverflowConfig(field)) {
      return splitForOverflow(value, geometry.get(), field.skipTextFitting());
    }

    return fitInPlace(value, geometry.get(), field.skipTextFitting());
  }

  /**
   * Fits a value that has nowhere else to go. A field that opted out of fitting keeps its value
   * untouched; every other field gives up its line breaks first and is truncated only if it still
   * does not fit.
   */
  private FieldValueResult fitInPlace(String value, FieldGeometry geometry, boolean skipTextFitting)
      throws IOException {
    return skipTextFitting
        ? new FieldValueResult(value, null)
        : new FieldValueResult(fitWithoutContinuation(value, geometry), null);
  }

  private boolean hasOverflowConfig(CustomPdfField field) {
    return field.overflow() != null && field.overflow().overflowFieldId() != null;
  }

  private FieldValueResult splitForOverflow(
      String value, FieldGeometry geometry, boolean skipTextFitting) throws IOException {
    var suffix = LONG_OVERFLOW_SUFFIX;
    var splitIndex = splitIndexLeavingRoomFor(value, suffix, SUFFIX_SEPARATOR, geometry);

    if (splitIndex == NOTHING_FITS) {
      suffix = SHORT_OVERFLOW_SUFFIX;
      splitIndex = splitIndexLeavingRoomFor(value, suffix, SUFFIX_SEPARATOR, geometry);
    }

    if (splitIndex == NOTHING_FITS) {
      return fitInPlace(value, geometry, skipTextFitting);
    }

    final var primaryValueWithSuffix =
        value.substring(0, splitIndex).stripTrailing() + SUFFIX_SEPARATOR + suffix;

    final var remainder =
        OVERFLOW_REMAINDER_PREFIX + value.substring(Math.min(splitIndex, value.length())).trim();

    return new FieldValueResult(primaryValueWithSuffix, remainder);
  }

  private String fitWithoutContinuation(String value, FieldGeometry geometry) throws IOException {
    final var flattened = flattenLineBreaks(value);
    if (textLayout.fits(flattened, geometry)) {
      return flattened;
    }
    final var splitIndex = splitIndexLeavingRoomFor(flattened, TRUNCATION_SUFFIX, "", geometry);
    final var prefix = flattened.substring(0, splitIndex).stripTrailing();
    return prefix.isEmpty() ? value : prefix + TRUNCATION_SUFFIX;
  }

  /** Joins lines with a single space; blank lines and whitespace around each break collapse too. */
  private String flattenLineBreaks(String value) {
    return LINE_BREAKS.matcher(value).replaceAll(LINE_BREAK_REPLACEMENT);
  }

  /**
   * Longest prefix of {@code value}, ending on a word boundary where possible, that still fits once
   * {@code suffix} is appended. Returns {@link #NOTHING_FITS} when not even an empty prefix leaves
   * room for the suffix.
   */
  private int splitIndexLeavingRoomFor(
      String value, String suffix, String separator, FieldGeometry geometry) throws IOException {
    if (!textLayout.fits(withSuffix(value, 0, suffix, separator), geometry)) {
      return NOTHING_FITS;
    }

    var low = 0;
    var high = value.length();
    while (low < high) {
      final var mid = (low + high + 1) / 2;
      if (textLayout.fits(withSuffix(value, mid, suffix, separator), geometry)) {
        low = mid;
      } else {
        high = mid - 1;
      }
    }

    return atWordBoundary(value, low);
  }

  private String withSuffix(String value, int length, String suffix, String separator) {
    final var prefix = value.substring(0, length).stripTrailing();
    return prefix.isEmpty() ? suffix : prefix + separator + suffix;
  }

  private int atWordBoundary(String value, int splitIndex) {
    if (splitIndex >= value.length()) {
      return splitIndex;
    }
    final var lastBoundary = lastWhitespace(value, splitIndex);
    return lastBoundary > 0 ? lastBoundary : splitIndex;
  }

  private int lastWhitespace(String value, int searchFromIndex) {
    for (var i = Math.min(searchFromIndex, value.length() - 1); i >= 0; i--) {
      if (Character.isWhitespace(value.charAt(i))) {
        return i;
      }
    }
    return -1;
  }
}
