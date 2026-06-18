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

import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;

@Component
public class FieldValueProcessor {

  private static final String LONG_OVERFLOW_SUFFIX = "... Se fortsättningsblad!";
  private static final String SHORT_OVERFLOW_SUFFIX = "...";
  private static final String TRUNCATION_SUFFIX = "...";
  private static final String OVERFLOW_REMAINDER_PREFIX = "... ";
  private static final int MAX_LENGTH_THRESHOLD_FOR_LONG_SUFFIX = 22;

  public FieldValueResult process(CustomPdfField field) {
    final var originalValue = field.value();
    final var primaryValue =
        field.shouldRemoveLineBreaks() ? originalValue.replace("\n", "") : originalValue;

    if (!exceedsMaxLength(field, primaryValue)) {
      return new FieldValueResult(primaryValue, null);
    }

    if (hasOverflowConfig(field)) {
      return splitForOverflow(field, primaryValue, originalValue);
    }

    return new FieldValueResult(truncate(primaryValue, field.maxLength()), null);
  }

  private boolean exceedsMaxLength(CustomPdfField field, String value) {
    return field.maxLength() != null && value.length() > field.maxLength();
  }

  private boolean hasOverflowConfig(CustomPdfField field) {
    return field.overflow() != null && field.overflow().overflowFieldId() != null;
  }

  private FieldValueResult splitForOverflow(
      CustomPdfField field, String primaryValue, String originalValue) {
    final var maxLength = field.maxLength();
    final var suffix =
        maxLength > MAX_LENGTH_THRESHOLD_FOR_LONG_SUFFIX
            ? LONG_OVERFLOW_SUFFIX
            : SHORT_OVERFLOW_SUFFIX;

    final var effectiveLimit = maxLength - suffix.length() - 1;
    if (effectiveLimit <= 0) {
      return new FieldValueResult(truncate(primaryValue, maxLength), null);
    }

    final var lastSpace = primaryValue.lastIndexOf(' ', effectiveLimit);
    final var splitIndex = lastSpace > 0 ? lastSpace : effectiveLimit;

    final var primaryValueWithSuffix = primaryValue.substring(0, splitIndex) + " " + suffix;

    final var originalSplitIndex =
        field.shouldRemoveLineBreaks() ? mapIndexToOriginal(originalValue, splitIndex) : splitIndex;

    final var remainder =
        OVERFLOW_REMAINDER_PREFIX
            + originalValue.substring(Math.min(originalSplitIndex, originalValue.length())).trim();

    return new FieldValueResult(primaryValueWithSuffix, remainder);
  }

  private String truncate(String value, int maxLength) {
    final var effectiveLimit = maxLength - TRUNCATION_SUFFIX.length();
    final var lastSpace = value.lastIndexOf(' ', effectiveLimit);
    final var splitIndex = lastSpace > 0 ? lastSpace : effectiveLimit;
    return value.substring(0, splitIndex) + TRUNCATION_SUFFIX;
  }

  private int mapIndexToOriginal(String original, int strippedIndex) {
    var count = 0;
    for (var i = 0; i < original.length(); i++) {
      if (original.charAt(i) != '\n') {
        if (count == strippedIndex) {
          return i;
        }
        count++;
      }
    }
    return original.length();
  }
}
