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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.FontResolver;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.PersonIdConfig;

public record PatientIdInfo(String value, PDRectangle rectangle, PDFont font, float fontSize) {

  public static PatientIdInfo of(PDDocument document, PersonIdConfig personIdConfig) {
    if (personIdConfig == null) {
      return null;
    }
    final var acroForm = document.getDocumentCatalog().getAcroForm();
    final var field = acroForm.getField(personIdConfig.fieldId());
    if (!(field instanceof PDTextField textField)) {
      throw new IllegalArgumentException("Invalid field for person id");
    }

    final var widgets = textField.getWidgets();
    final var font = FontResolver.getFont(textField);
    final var fontSize = Float.parseFloat(textField.getDefaultAppearance().split("\\s+")[1]);
    return new PatientIdInfo(
        personIdConfig.value(), widgets.getFirst().getRectangle(), font, fontSize);
  }
}
