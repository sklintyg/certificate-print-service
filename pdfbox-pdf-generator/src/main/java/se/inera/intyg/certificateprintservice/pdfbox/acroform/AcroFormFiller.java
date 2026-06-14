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
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcroFormFiller {

  public void fill(PDDocument document, Map<String, CustomPdfField> fields) {
    if (fields == null || fields.isEmpty()) {
      return;
    }
    final var acroForm = document.getDocumentCatalog().getAcroForm();
    fields.forEach(
        (fieldId, fieldOptions) -> {
          final var field = acroForm.getField(fieldId);
          if (field == null) {
            throw new IllegalArgumentException(
                "Field '%s' not found in PDF template — cannot accurately fill in template"
                    .formatted(fieldId));
          }
          try {
            // TODO Discuss if this should be handled in the api or not
            if (field instanceof PDVariableText textField) {
              final var textAppearance = new TextFieldAppearance(textField);
              textAppearance.adjustFieldHeight(1);
            }

            field.setValue(fieldOptions.getValue());
          } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to set value for field '%s': %s".formatted(fieldId, e.getMessage()), e);
          }
        });
  }
}
