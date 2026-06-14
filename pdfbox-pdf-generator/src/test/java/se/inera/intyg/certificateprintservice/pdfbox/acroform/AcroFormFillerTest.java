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
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.BIRTH_DATE_FIELD_ID;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.PATIENT_ID;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.PATIENT_ID_FIELD_ID;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.TAGGED_PDF_RESOURCE;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;

class AcroFormFillerTest {

  private final AcroFormFiller filler = new AcroFormFiller();

  private PDDocument document;

  @BeforeEach
  void setUp() throws IOException {
    try (InputStream stream = getClass().getResourceAsStream(TAGGED_PDF_RESOURCE)) {
      Assertions.assertNotNull(stream);
      document = Loader.loadPDF(stream.readAllBytes());
    }
  }

  @AfterEach
  void tearDown() throws IOException {
    if (document != null) {
      document.close();
    }
  }

  @Test
  void shallFillFieldWithGivenValue() {
    filler.fill(
        document, Map.of(PATIENT_ID_FIELD_ID, CustomPdfField.builder().value(PATIENT_ID).build()));

    assertEquals(
        PATIENT_ID,
        document
            .getDocumentCatalog()
            .getAcroForm()
            .getField(PATIENT_ID_FIELD_ID)
            .getValueAsString());
  }

  @Test
  void shallThrowWhenFieldIdDoesNotExist() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            filler.fill(
                document, Map.of("nonExistent", CustomPdfField.builder().value("value").build())));
  }

  @Test
  void shallDoNothingWhenFieldsMapIsEmpty() {
    filler.fill(document, Collections.emptyMap());

    assertEquals(
        "",
        document
            .getDocumentCatalog()
            .getAcroForm()
            .getField(PATIENT_ID_FIELD_ID)
            .getValueAsString());
  }

  @Test
  void shallNotModifyUnspecifiedFields() {
    filler.fill(
        document, Map.of(PATIENT_ID_FIELD_ID, CustomPdfField.builder().value(PATIENT_ID).build()));

    assertEquals(
        "",
        document
            .getDocumentCatalog()
            .getAcroForm()
            .getField(BIRTH_DATE_FIELD_ID)
            .getValueAsString());
  }
}
