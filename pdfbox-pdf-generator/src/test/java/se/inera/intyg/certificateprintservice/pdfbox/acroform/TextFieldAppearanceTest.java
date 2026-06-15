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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class TextFieldAppearanceTest {

  private PDTextField field;
  private TextFieldAppearance textfieldAppearance;

  @BeforeEach
  void setUp() {
    field = mock(PDTextField.class);
    textfieldAppearance = new TextFieldAppearance(field);
  }

  @Test
  void shouldAdjustFieldHeight() {
    final var widget1 = mock(PDAnnotationWidget.class);
    final var widget2 = mock(PDAnnotationWidget.class);
    final var captor1 = ArgumentCaptor.forClass(PDRectangle.class);
    final var captor2 = ArgumentCaptor.forClass(PDRectangle.class);

    when(widget1.getRectangle()).thenReturn(new PDRectangle(0, 0, 100, 50));
    when(widget2.getRectangle()).thenReturn(new PDRectangle(0, 0, 100, 75));

    when(field.getWidgets()).thenReturn(Arrays.asList(widget1, widget2));
    when(field.getDefaultAppearance()).thenReturn("/ArialMT 12.0");

    textfieldAppearance.adjustFieldHeight(0);

    verify(widget1).setRectangle(captor1.capture());
    verify(widget2).setRectangle(captor2.capture());

    assertPDRectangleEquals(new PDRectangle(0, 0, 100, 61), captor1.getValue());
    assertPDRectangleEquals(new PDRectangle(0, 0, 100, 86), captor2.getValue());
  }

  private void assertPDRectangleEquals(PDRectangle expected, PDRectangle actual) {
    assertEquals(expected.getLowerLeftX(), actual.getLowerLeftX());
    assertEquals(expected.getLowerLeftY(), actual.getLowerLeftY());
    assertEquals(expected.getWidth(), actual.getWidth());
    assertEquals(expected.getHeight(), actual.getHeight());
  }
}
