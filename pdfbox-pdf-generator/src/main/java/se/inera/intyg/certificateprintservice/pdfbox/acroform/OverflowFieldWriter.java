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
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPaginationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OverflowFieldWriter {

  private final OverflowPaginationService overflowPaginationService;

  public void writeAll(
      PDDocument document,
      PDAcroForm acroForm,
      Map<String, StringBuilder> accumulator,
      Integer overflowPageIndex) {
    accumulator.forEach(
        (overflowFieldId, content) -> {
          final var field = lookupField(acroForm, overflowFieldId);
          final var contentText = content.toString();

          if (overflowPageIndex != null && field instanceof PDTextField textField) {
            paginateOverflow(document, textField, overflowFieldId, contentText, overflowPageIndex);
          } else {
            setValue(field, overflowFieldId, contentText);
          }
        });
  }

  private PDField lookupField(PDAcroForm acroForm, String fieldId) {
    final var field = acroForm.getField(fieldId);
    if (field == null) {
      throw new IllegalArgumentException(
          "Overflow field '%s' not found in PDF template".formatted(fieldId));
    }
    return field;
  }

  private void setValue(PDField field, String fieldId, String value) {
    try {
      field.setValue(value);
    } catch (IOException e) {
      throw new IllegalStateException(
          "Failed to set value for overflow field '%s': %s".formatted(fieldId, e.getMessage()), e);
    }
  }

  private void paginateOverflow(
      PDDocument document,
      PDTextField textField,
      String fieldId,
      String content,
      int overflowPageIndex) {
    try {
      overflowPaginationService.writeWithPagination(
          document, textField, content, overflowPageIndex);
    } catch (IOException e) {
      throw new IllegalStateException(
          "Failed to paginate overflow field '%s': %s".formatted(fieldId, e.getMessage()), e);
    }
  }
}
