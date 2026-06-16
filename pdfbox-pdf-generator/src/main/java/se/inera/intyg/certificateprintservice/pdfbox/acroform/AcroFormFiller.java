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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcroFormFiller {

  private static final String LONG_OVERFLOW_SUFFIX = "... Se fortsättningsblad!";
  private static final String SHORT_OVERFLOW_SUFFIX = "...";
  private static final String TRUNCATION_SUFFIX = "...";
  private static final String OVERFLOW_REMAINDER_PREFIX = "... ";
  private static final int MAX_LENGTH_THRESHOLD_FOR_LONG_SUFFIX = 22;

  public void fill(PDDocument document, Map<String, CustomPdfField> fields) {
    if (fields == null || fields.isEmpty()) {
      return;
    }

    final var acroForm = document.getDocumentCatalog().getAcroForm();
    final var overflowAccumulator = new LinkedHashMap<String, StringBuilder>();

    fields.forEach(
        (fieldId, fieldOptions) -> {
          final var field = acroForm.getField(fieldId);
          if (field == null) {
            throw new IllegalArgumentException(
                "Field '%s' not found in PDF template — cannot accurately fill in template"
                    .formatted(fieldId));
          }
          try {
            if (field instanceof PDTextField textField && fieldOptions.appearance() != null) {
              textField.setDefaultAppearance(fieldOptions.appearance());
            }

            if (field instanceof PDVariableText textField) {
              final var textAppearance = new TextFieldAppearance(textField);
              textAppearance.adjustFieldHeight(
                  Optional.ofNullable(fieldOptions.offset()).orElse(0));
            }

            var valueToWrite =
                fieldOptions.shouldRemoveLineBreaks()
                    ? fieldOptions.value().replace("\n", "")
                    : fieldOptions.value();

            if (requiresOverflowSplit(fieldOptions, valueToWrite)) {
              valueToWrite = splitAndAccumulate(fieldOptions, valueToWrite, overflowAccumulator);
            } else if (requiresTruncation(fieldOptions, valueToWrite)) {
              valueToWrite = truncateWithEllipsis(valueToWrite, fieldOptions.maxLength());
            }

            field.setValue(valueToWrite);
          } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to set value for field '%s': %s".formatted(fieldId, e.getMessage()), e);
          }
        });

    writeOverflowFields(acroForm, overflowAccumulator);
  }

  private boolean requiresOverflowSplit(CustomPdfField fieldOptions, String value) {
    if (fieldOptions.maxLength() == null) {
      return false;
    }
    if (value.length() <= fieldOptions.maxLength()) {
      return false;
    }
    return fieldOptions.overflow() != null && fieldOptions.overflow().overflowFieldId() != null;
  }

  private boolean requiresTruncation(CustomPdfField fieldOptions, String value) {
    if (fieldOptions.maxLength() == null) {
      return false;
    }
    if (value.length() <= fieldOptions.maxLength()) {
      return false;
    }
    return fieldOptions.overflow() == null || fieldOptions.overflow().overflowFieldId() == null;
  }

  private String splitAndAccumulate(
      CustomPdfField fieldOptions,
      String processedValue,
      Map<String, StringBuilder> overflowAccumulator) {
    final var maxLength = fieldOptions.maxLength();
    final var suffix =
        maxLength > MAX_LENGTH_THRESHOLD_FOR_LONG_SUFFIX
            ? LONG_OVERFLOW_SUFFIX
            : SHORT_OVERFLOW_SUFFIX;

    final var effectiveLimit = maxLength - suffix.length() - 1;
    final var lastSpace = processedValue.lastIndexOf(' ', effectiveLimit);
    final var splitIndex = lastSpace > 0 ? lastSpace : effectiveLimit;

    final var firstPart = processedValue.substring(0, splitIndex) + " " + suffix;

    final var originalValue = fieldOptions.value();
    final var remainder = OVERFLOW_REMAINDER_PREFIX + originalValue.substring(splitIndex).trim();

    final var overflowFieldId = fieldOptions.overflow().overflowFieldId();
    final var label = fieldOptions.overflow().overflowLabel();

    overflowAccumulator
        .computeIfAbsent(overflowFieldId, k -> new StringBuilder())
        .append(label)
        .append("\n")
        .append(remainder)
        .append("\n");

    return firstPart;
  }

  private String truncateWithEllipsis(String value, int maxLength) {
    final var effectiveLimit = maxLength - TRUNCATION_SUFFIX.length();
    final var lastSpace = value.lastIndexOf(' ', effectiveLimit);
    final var splitIndex = lastSpace > 0 ? lastSpace : effectiveLimit;
    return value.substring(0, splitIndex) + TRUNCATION_SUFFIX;
  }

  private void writeOverflowFields(
      org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm acroForm,
      Map<String, StringBuilder> overflowAccumulator) {
    overflowAccumulator.forEach(
        (overflowFieldId, content) -> {
          final var field = acroForm.getField(overflowFieldId);
          if (field == null) {
            throw new IllegalArgumentException(
                "Overflow field '%s' not found in PDF template".formatted(overflowFieldId));
          }
          try {
            field.setValue(content.toString());
          } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to write overflow field '%s': %s"
                    .formatted(overflowFieldId, e.getMessage()),
                e);
          }
        });
  }
}
