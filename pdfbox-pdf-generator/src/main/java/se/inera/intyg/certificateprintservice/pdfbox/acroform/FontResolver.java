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

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;

public class FontResolver {

  private FontResolver() {
    throw new IllegalStateException("Utility class");
  }

  public static PDFont getFont(PDField field) {
    final var acroForm = field.getAcroForm();
    final var appearance =
        field instanceof PDVariableText vt
            ? vt.getDefaultAppearance()
            : acroForm.getDefaultAppearance();
    final var fontName = appearance.split("\\s+")[0].substring(1);
    try {
      final var font = acroForm.getDefaultResources().getFont(COSName.getPDFName(fontName));
      if (font != null) {
        return font;
      }
    } catch (Exception e) {
      throw new IllegalStateException("Missing font resource in template", e);
    }
    throw new IllegalStateException("Missing font resource in template");
  }
}
