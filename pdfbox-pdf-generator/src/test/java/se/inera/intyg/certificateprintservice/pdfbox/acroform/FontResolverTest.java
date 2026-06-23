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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FontResolverTest {

  private PDTextField field;
  private PDAcroForm acroForm;
  private PDResources resources;

  @BeforeEach
  void setUp() {
    field = mock(PDTextField.class);
    acroForm = mock(PDAcroForm.class);
    resources = mock(PDResources.class);
    when(field.getAcroForm()).thenReturn(acroForm);
    when(acroForm.getDefaultResources()).thenReturn(resources);
  }

  @Test
  void shouldResolveFontFromFieldAppearance() throws Exception {
    final var expectedFont = mock(PDFont.class);

    when(field.getDefaultAppearance()).thenReturn("/Helv 9 Tf 0 g");
    when(resources.getFont(COSName.getPDFName("Helv"))).thenReturn(expectedFont);

    final var result = FontResolver.getFont(field);

    assertNotNull(result);
    assertEquals(expectedFont, result);
  }

  @Test
  void shouldThrowWhenFontNotFoundInResources() {
    when(field.getDefaultAppearance()).thenReturn("/UnknownFont 10 Tf 0 g");

    assertThrows(IllegalStateException.class, () -> FontResolver.getFont(field));
  }
}
