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
package se.inera.intyg.certificateprintservice.pdfbox;

import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.CustomPdfFieldF;

@Slf4j
@Service
public class AcroFormFiller {

  public void fill(PDDocument document, Map<String, CustomPdfFieldF> fields) {
    if (fields == null || fields.isEmpty()) {
      return;
    }

    final var acroForm = document.getDocumentCatalog().getAcroForm();

    if (acroForm == null) {
      log.warn("PDF template has no AcroForm — no fields will be filled");
      return;
    }

    fields.forEach(
        (fieldId, fieldOptions) -> {
          final var field = acroForm.getField(fieldId);
          if (field == null) {
            log.warn("Field '{}' not found in PDF template — skipping", fieldId);
            return;
          }
          try {
            field.setValue(fieldOptions.getValue());
          } catch (IOException e) {
            log.warn("Failed to set value for field '{}': {}", fieldId, e.getMessage());
          }
        });
  }
}
