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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;

class AcroFormFillerTest {

  private final AcroFormFiller filler = new AcroFormFiller();

  private PDDocument document;
  private PDAcroForm acroForm;

  @BeforeEach
  void setUp() {
    document = new PDDocument();
    document.addPage(new PDPage());
    acroForm = new PDAcroForm(document);
    document.getDocumentCatalog().setAcroForm(acroForm);
  }

  @Test
  void shallFillFieldWithGivenValue() {
    addTextField("myField");

    filler.fill(document, Map.of("myField", CustomPdfField.builder().value("Expected").build()));

    assertEquals("Expected", acroForm.getField("myField").getValueAsString());
  }

  @Test
  void shallThrowWhenFieldIdDoesNotExist() {
    assertThrows(IllegalArgumentException.class, () -> filler.fill(document,
        Map.of("nonExistent", CustomPdfField.builder().value("value").build())));
  }

  @Test
  void shallDoNothingWhenFieldsMapIsEmpty() {
    addTextField("myField");

    filler.fill(document, Collections.emptyMap());

    assertEquals("", acroForm.getField("myField").getValueAsString());
  }

  private void addTextField(String fieldName) {
    final var field = new PDTextField(acroForm);
    field.setPartialName(fieldName);
    acroForm.getFields().add(field);
  }
}
