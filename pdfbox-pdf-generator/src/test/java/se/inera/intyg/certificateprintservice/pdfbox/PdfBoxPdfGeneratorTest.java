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
package se.inera.intyg.certificateprintservice.pdfbox;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210CustomPdf.build7210CustomPdfWithTemplate;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.TAGGED_PDF_RESOURCE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.Loader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.AcroFormFiller;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.FieldValueProcessor;
import se.inera.intyg.certificateprintservice.pdfbox.overlay.OverlayTextService;
import se.inera.intyg.certificateprintservice.pdfbox.overlay.PdfTextGenerator;

class PdfBoxPdfGeneratorTest {

  private final se.inera.intyg.certificateprintservice.pdfbox.acroform.TextFieldAppearance
      textFieldAppearance =
          new se.inera.intyg.certificateprintservice.pdfbox.acroform.TextFieldAppearance();

  private final PdfBoxPdfGenerator generator =
      new PdfBoxPdfGenerator(
          new AcroFormFiller(
              new FieldValueProcessor(),
              new se.inera.intyg.certificateprintservice.pdfbox.acroform.OverflowFieldWriter(
                  new se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow
                      .OverflowPaginationService(
                      new se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow
                          .OverflowPagePaginator(
                          new se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow
                              .TextLineWrapper(),
                          new se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow
                              .OverflowPageCapacityCalculator()),
                      new se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow
                          .OverflowPageRenderer(
                          new se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow
                              .OverflowPageStructureCloner()),
                      textFieldAppearance)),
              textFieldAppearance),
          new OverlayTextService(new PdfTextGenerator()));

  @Test
  void shallThrowWhenTemplateIsNull() {
    final var request = build7210CustomPdfWithTemplate(null);

    assertThrows(IllegalArgumentException.class, () -> generator.get(request));
  }

  @Test
  void shallThrowWhenTemplateIsEmpty() {
    final var request = build7210CustomPdfWithTemplate(new byte[0]);

    assertThrows(IllegalArgumentException.class, () -> generator.get(request));
  }

  @Test
  void shallReturnNonEmptyBytesForValidTemplateWithNoFields() throws IOException {
    final var request = build7210CustomPdfWithTemplate(buildFk7210PdfTemplate());

    final var result = generator.get(request);

    assertNotNull(result);
    assertTrue(result.length > 0);
  }

  private byte[] buildFk7210PdfTemplate() throws IOException {
    try (InputStream stream = getClass().getResourceAsStream(TAGGED_PDF_RESOURCE);
        final var out = new ByteArrayOutputStream()) {
      Assertions.assertNotNull(stream);
      final var document = Loader.loadPDF(stream.readAllBytes());
      document.save(out);
      return out.toByteArray();
    }
  }
}
