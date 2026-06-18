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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7804Fields.AKTIVITETSBEGRANSNING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7804Fields.FUNKTIONSNEDSATTNING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7804Fields.OVERFLOW_FIELD_ID;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7804Fields.PATIENT_ID_FIELD_ID_1;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7804Fields.TAGGED_PDF_OVERFLOW_RESOURCE;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPageCapacityCalculator;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPagePaginator;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPageRenderer;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPageStructureCloner;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPaginationService;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.TextLineWrapper;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.OverflowConfig;

class AcroFormFillerOverflowTest {

  private final AcroFormFiller filler =
      new AcroFormFiller(
          new FieldValueProcessor(),
          new OverflowFieldWriter(
              new OverflowPaginationService(
                  new OverflowPagePaginator(
                      new TextLineWrapper(), new OverflowPageCapacityCalculator()),
                  new OverflowPageRenderer(new OverflowPageStructureCloner()))));
  private PDDocument document;

  @BeforeEach
  void setUp() throws IOException {
    try (InputStream stream = getClass().getResourceAsStream(TAGGED_PDF_OVERFLOW_RESOURCE)) {
      Assertions.assertNotNull(stream, "Overflow template must be on classpath");
      document = Loader.loadPDF(stream.readAllBytes());
    }
  }

  @AfterEach
  void tearDown() throws IOException {
    if (document != null) {
      document.close();
    }
  }

  @Nested
  class Scenario5OverflowWithContinuationSheet {

    @Test
    void shallSplitTextAndWriteMainFieldWithSuffix() {
      final var longValue = "A".repeat(50) + " " + "B".repeat(50) + " " + "C".repeat(50);
      final var maxLength = 60;
      final var field =
          CustomPdfField.builder()
              .value(longValue)
              .maxLength(maxLength)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("Funktionsnedsättning")
                      .build())
              .build();

      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(FUNKTIONSNEDSATTNING_FIELD_ID, field);

      filler.fill(document, fields, null);

      final var mainFieldValue = getFieldValue(FUNKTIONSNEDSATTNING_FIELD_ID);
      assertTrue(
          mainFieldValue.endsWith("... Se fortsättningsblad!"),
          "Main field should end with overflow suffix but was: " + mainFieldValue);
      assertTrue(
          mainFieldValue.length() <= maxLength, "Main field length should not exceed maxLength");
    }

    @Test
    void shallWriteRemainderToOverflowFieldWithLabel() {
      final var longValue = "A".repeat(50) + " " + "B".repeat(50) + " " + "C".repeat(50);
      final var maxLength = 60;
      final var field =
          CustomPdfField.builder()
              .value(longValue)
              .maxLength(maxLength)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("Funktionsnedsättning")
                      .build())
              .build();

      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(FUNKTIONSNEDSATTNING_FIELD_ID, field);

      filler.fill(document, fields, null);

      final var overflowValue = getFieldValue(OVERFLOW_FIELD_ID);
      assertTrue(
          overflowValue.startsWith("Funktionsnedsättning\n"),
          "Overflow should start with label but was: " + overflowValue);
      assertTrue(
          overflowValue.contains("... "), "Overflow remainder should be prefixed with '... '");
    }

    @Test
    void shallUseShorterSuffixWhenMaxLengthIs22OrLess() {
      final var longValue = "This text is too long for the tiny field";
      final var maxLength = 20;
      final var field =
          CustomPdfField.builder()
              .value(longValue)
              .maxLength(maxLength)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("Test")
                      .build())
              .build();

      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(FUNKTIONSNEDSATTNING_FIELD_ID, field);

      filler.fill(document, fields, null);

      final var mainFieldValue = getFieldValue(FUNKTIONSNEDSATTNING_FIELD_ID);
      assertTrue(
          mainFieldValue.endsWith("..."),
          "Main field should end with short suffix '...' but was: " + mainFieldValue);
      assertTrue(
          !mainFieldValue.contains("Se fortsättningsblad"),
          "Should not use long suffix when maxLength <= 22");
    }
  }

  @Nested
  class Scenario6AccumulatingMultipleOverflowContributions {

    @Test
    void shallAccumulateMultipleFieldsToSameOverflowField() {
      final var value1 = "A".repeat(50) + " " + "B".repeat(50) + " overflow part one";
      final var value2 = "C".repeat(50) + " " + "D".repeat(50) + " overflow part two";
      final var maxLength = 60;

      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(
          FUNKTIONSNEDSATTNING_FIELD_ID,
          CustomPdfField.builder()
              .value(value1)
              .maxLength(maxLength)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("Funktionsnedsättning")
                      .build())
              .build());
      fields.put(
          AKTIVITETSBEGRANSNING_FIELD_ID,
          CustomPdfField.builder()
              .value(value2)
              .maxLength(maxLength)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("Aktivitetsbegränsning")
                      .build())
              .build());

      filler.fill(document, fields, null);

      final var overflowValue = getFieldValue(OVERFLOW_FIELD_ID);
      final var labelIndex1 = overflowValue.indexOf("Funktionsnedsättning");
      final var labelIndex2 = overflowValue.indexOf("Aktivitetsbegränsning");
      assertTrue(labelIndex1 >= 0, "Should contain first label");
      assertTrue(labelIndex2 >= 0, "Should contain second label");
      assertTrue(
          labelIndex1 < labelIndex2,
          "First label should appear before second (insertion order preserved)");
    }

    @Test
    void shallPreserveInsertionOrderInOverflowAccumulation() {
      final var maxLength = 30;

      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(
          FUNKTIONSNEDSATTNING_FIELD_ID,
          CustomPdfField.builder()
              .value("First field with enough text to overflow the limit set")
              .maxLength(maxLength)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("First")
                      .build())
              .build());
      fields.put(
          AKTIVITETSBEGRANSNING_FIELD_ID,
          CustomPdfField.builder()
              .value("Second field with enough text to overflow the limit set")
              .maxLength(maxLength)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("Second")
                      .build())
              .build());

      filler.fill(document, fields, null);

      final var overflowValue = getFieldValue(OVERFLOW_FIELD_ID);
      assertTrue(
          overflowValue.indexOf("First") < overflowValue.indexOf("Second"),
          "Labels must appear in insertion order. Overflow content: " + overflowValue);
    }
  }

  @Nested
  class OverflowPageRemoval {

    private static final int OVERFLOW_PAGE_INDEX = 4;

    @Test
    void shallRemoveOverflowPageWhenNoFieldsOverflow() {
      final var initialPageCount = document.getNumberOfPages();
      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(PATIENT_ID_FIELD_ID_1, CustomPdfField.builder().value("Short value").build());

      filler.fill(document, fields, OVERFLOW_PAGE_INDEX);

      assertEquals(initialPageCount - 1, document.getNumberOfPages());
    }

    @Test
    void shallNotRemovePageWhenOverflowPageIndexIsNull() {
      final var initialPageCount = document.getNumberOfPages();
      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(PATIENT_ID_FIELD_ID_1, CustomPdfField.builder().value("Short value").build());

      filler.fill(document, fields, null);

      assertEquals(initialPageCount, document.getNumberOfPages());
    }
  }

  private String getFieldValue(String fieldId) {
    return document.getDocumentCatalog().getAcroForm().getField(fieldId).getValueAsString();
  }
}
