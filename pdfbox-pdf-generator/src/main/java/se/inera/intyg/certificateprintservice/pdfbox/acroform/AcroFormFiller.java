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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;

@Service
@RequiredArgsConstructor
public class AcroFormFiller {

  private final FieldValueProcessor fieldValueProcessor;
  private final OverflowFieldWriter overflowFieldWriter;
  private final TextFieldAppearance textFieldAppearance;

  public void fill(
      PDDocument document, Map<String, CustomPdfField> fields, Integer overflowPageIndex) {
    if (fields == null || fields.isEmpty()) {
      return;
    }

    final var acroForm = document.getDocumentCatalog().getAcroForm();
    final var overflowAccumulator = new LinkedHashMap<String, List<OverflowEntry>>();

    fields.forEach(
        (fieldId, fieldOptions) -> {
          final var field = lookupField(acroForm, fieldId);
          applyAppearance(field, fieldOptions);
          adjustHeight(field, fieldOptions);

          final var font = FontResolver.getFont(field);
          final var result = fieldValueProcessor.process(fieldOptions, font);
          setValue(field, fieldId, result.primaryValue());
          accumulateOverflow(fieldOptions, result, overflowAccumulator);
        });

    if (overflowAccumulator.isEmpty() && overflowPageIndex != null) {
      document.removePage(overflowPageIndex);
    } else {
      overflowFieldWriter.writeAll(document, acroForm, overflowAccumulator, overflowPageIndex);
    }
  }

  private PDField lookupField(PDAcroForm acroForm, String fieldId) {
    final var field = acroForm.getField(fieldId);
    if (field == null) {
      throw new IllegalArgumentException(
          "Field '%s' not found in PDF template — cannot accurately fill in template"
              .formatted(fieldId));
    }
    return field;
  }

  private void applyAppearance(PDField field, CustomPdfField fieldOptions) {
    if (field instanceof PDTextField textField && fieldOptions.appearance() != null) {
      textField.setDefaultAppearance(fieldOptions.appearance());
    }
  }

  private void adjustHeight(PDField field, CustomPdfField fieldOptions) {
    if (field instanceof PDVariableText textField) {
      textFieldAppearance.adjustFieldHeight(
          textField, Optional.ofNullable(fieldOptions.offset()).orElse(0));
    }
  }

  private void setValue(PDField field, String fieldId, String value) {
    try {
      field.setValue(value);
    } catch (IOException e) {
      throw new IllegalStateException(
          "Failed to set value for field '%s': %s".formatted(fieldId, e.getMessage()), e);
    }
  }

  private void accumulateOverflow(
      CustomPdfField fieldOptions,
      FieldValueResult result,
      Map<String, List<OverflowEntry>> accumulator) {
    if (result.overflowRemainder() == null || fieldOptions.overflow() == null) {
      return;
    }

    final var overflowFieldId = fieldOptions.overflow().overflowFieldId();
    final var label = fieldOptions.overflow().overflowLabel();

    accumulator
        .computeIfAbsent(overflowFieldId, k -> new ArrayList<>())
        .add(new OverflowEntry(label, result.overflowRemainder()));
  }
}
