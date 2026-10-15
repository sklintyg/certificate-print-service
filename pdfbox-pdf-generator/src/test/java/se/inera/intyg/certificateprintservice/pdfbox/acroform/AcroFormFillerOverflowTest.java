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
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPagePaginator;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPageRenderer;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPageStructureCloner;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPaginationService;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.FieldGeometryResolver;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.FieldTextLayout;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.TextCapacityCalculator;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.TextLineWrapper;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.OverflowConfig;

class AcroFormFillerOverflowTest {

  private final TextFieldAppearance textFieldAppearance = new TextFieldAppearance();

  private final AcroFormFiller filler =
      new AcroFormFiller(
          new FieldValueProcessor(new FieldTextLayout()),
          new FieldGeometryResolver(),
          new OverflowFieldWriter(
              new OverflowPaginationService(
                  new OverflowPagePaginator(new TextLineWrapper(), new TextCapacityCalculator()),
                  new OverflowPageRenderer(new OverflowPageStructureCloner()),
                  textFieldAppearance)),
          textFieldAppearance);

  /** Comfortably more text than any field in the template can show, so a split is guaranteed. */
  private static final String TOO_LONG_FOR_THE_FIELD =
      "Patienten har en betydande nedsattning av funktionsformagan i bade axlar och armar "
          .repeat(30)
          .trim();

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
      final var field =
          CustomPdfField.builder()
              .value(TOO_LONG_FOR_THE_FIELD)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("Funktionsnedsättning")
                      .build())
              .build();

      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(FUNKTIONSNEDSATTNING_FIELD_ID, field);

      filler.fill(document, fields, null, null);

      final var mainFieldValue = getFieldValue(FUNKTIONSNEDSATTNING_FIELD_ID);
      assertTrue(
          mainFieldValue.endsWith("... Se fortsättningsblad!"),
          "Main field should end with overflow suffix but was: " + mainFieldValue);
    }

    @Test
    void shallWriteRemainderToOverflowFieldWithLabel() {
      final var field =
          CustomPdfField.builder()
              .value(TOO_LONG_FOR_THE_FIELD)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("Funktionsnedsättning")
                      .build())
              .build();

      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(FUNKTIONSNEDSATTNING_FIELD_ID, field);

      filler.fill(document, fields, null, null);

      final var overflowValue = getFieldValue(OVERFLOW_FIELD_ID);
      assertTrue(
          overflowValue.startsWith("Funktionsnedsättning\n"),
          "Overflow should start with label but was: " + overflowValue);
      assertTrue(
          overflowValue.contains("... "), "Overflow remainder should be prefixed with '... '");
    }

    @Test
    void shallUseShorterSuffixWhenFieldIsTooNarrowForTheLongOne() {
      final var field =
          CustomPdfField.builder()
              .value("This text is far too long for the narrow personal identity field")
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("Test")
                      .build())
              .build();

      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(PATIENT_ID_FIELD_ID_1, field);

      filler.fill(document, fields, null, null);

      final var mainFieldValue = getFieldValue(PATIENT_ID_FIELD_ID_1);
      assertTrue(
          mainFieldValue.endsWith("..."),
          "Main field should end with short suffix '...' but was: " + mainFieldValue);
      assertTrue(
          !mainFieldValue.contains("Se fortsättningsblad"),
          "Long suffix cannot fit a field this narrow");
    }
  }

  @Nested
  class Scenario6AccumulatingMultipleOverflowContributions {

    @Test
    void shallAccumulateMultipleFieldsToSameOverflowField() {
      final var value1 = TOO_LONG_FOR_THE_FIELD + " overflow part one";
      final var value2 = TOO_LONG_FOR_THE_FIELD + " overflow part two";

      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(
          FUNKTIONSNEDSATTNING_FIELD_ID,
          CustomPdfField.builder()
              .value(value1)
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
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("Aktivitetsbegränsning")
                      .build())
              .build());

      filler.fill(document, fields, null, null);

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
      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(
          FUNKTIONSNEDSATTNING_FIELD_ID,
          CustomPdfField.builder()
              .value("First " + TOO_LONG_FOR_THE_FIELD)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("First")
                      .build())
              .build());
      fields.put(
          AKTIVITETSBEGRANSNING_FIELD_ID,
          CustomPdfField.builder()
              .value("Second " + TOO_LONG_FOR_THE_FIELD)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId(OVERFLOW_FIELD_ID)
                      .overflowLabel("Second")
                      .build())
              .build());

      filler.fill(document, fields, null, null);

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

      filler.fill(document, fields, OVERFLOW_PAGE_INDEX, null);

      assertEquals(initialPageCount - 1, document.getNumberOfPages());
    }

    @Test
    void shallRemoveOverflowPageStructureWhenNoFieldsOverflow() {
      final var documentTag =
          (PDStructureElement)
              document.getDocumentCatalog().getStructureTreeRoot().getKids().getFirst();
      final var initialSectCount = documentTag.getKids().size();
      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(PATIENT_ID_FIELD_ID_1, CustomPdfField.builder().value("Short value").build());

      filler.fill(document, fields, OVERFLOW_PAGE_INDEX, null);

      assertEquals(initialSectCount - 1, documentTag.getKids().size());
    }

    @Test
    void shallNotRemovePageWhenOverflowPageIndexIsNull() {
      final var initialPageCount = document.getNumberOfPages();
      final var fields = new LinkedHashMap<String, CustomPdfField>();
      fields.put(PATIENT_ID_FIELD_ID_1, CustomPdfField.builder().value("Short value").build());

      filler.fill(document, fields, null, null);

      assertEquals(initialPageCount, document.getNumberOfPages());
    }
  }

  private String getFieldValue(String fieldId) {
    return document.getDocumentCatalog().getAcroForm().getField(fieldId).getValueAsString();
  }
}
