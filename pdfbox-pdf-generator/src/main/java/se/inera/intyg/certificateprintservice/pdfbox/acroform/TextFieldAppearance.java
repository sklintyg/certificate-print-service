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
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;

@Slf4j
@EqualsAndHashCode
public class TextFieldAppearance {

  PDVariableText field;

  public TextFieldAppearance(PDVariableText field) {
    this.field = field;
  }

  public void adjustFieldHeight(Integer offset) {
    final var fontSize = getFontSize();
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

  public float getFontSize() {
    return Float.parseFloat(this.getAppearanceParts()[1]);
  }

  public PDFont getFont(PDAcroForm acroForm) throws IOException {
    final var fontName = getAppearanceParts()[0].replaceFirst("^/", "");
    final var resources = acroForm.getDefaultResources();
    if (resources != null) {
      final var font = resources.getFont(COSName.getPDFName(fontName));
      if (font != null) {
        return font;
      }
    }
    throw new IOException("Font '%s' not found in AcroForm default resources".formatted(fontName));
  }

  private String[] getAppearanceParts() {
    final var appearance = field.getDefaultAppearance();
    return appearance.split("\\s+");
  }
}
