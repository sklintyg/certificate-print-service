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

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TextFieldAppearance {

  public void adjustFieldHeight(PDVariableText field, Integer offset) {
    final var fontSize = getFontSize(field);
    for (PDAnnotationWidget widget : field.getWidgets()) {
      final var rec = widget.getRectangle();

      widget.setRectangle(
          new PDRectangle(
              rec.getLowerLeftX(),
              rec.getLowerLeftY(),
              rec.getWidth(),
              rec.getHeight() + Math.round(fontSize) - 1 + (offset != null ? offset : 0)));
    }
  }

  public float getFontSize(PDVariableText field) {
    return Float.parseFloat(getAppearanceParts(field)[1]);
  }

  private String[] getAppearanceParts(PDVariableText field) {
    final var appearance = field.getDefaultAppearance();
    return appearance.split("\\s+");
  }
}
