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
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.TAGGED_PDF_RESOURCE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import org.apache.pdfbox.Loader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210CustomPdfMetadata;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.CustomPdf;

class PdfBoxPdfGeneratorTest {

  private final PdfBoxPdfGenerator generator =
      new PdfBoxPdfGenerator(new AcroFormFiller(), new OverlayTextService(new PdfTextGenerator()));

  @Test
  void shallThrowWhenTemplateIsNull() {
    final var request =
        CustomPdf.builder()
            .template(null)
            .metadata(TestDataFK7210CustomPdfMetadata.draftMetadata())
            .fields(Collections.emptyMap())
            .build();

    assertThrows(IllegalArgumentException.class, () -> generator.get(request));
  }

  @Test
  void shallThrowWhenTemplateIsEmpty() {
    final var request =
        CustomPdf.builder()
            .template(new byte[0])
            .metadata(TestDataFK7210CustomPdfMetadata.draftMetadata())
            .fields(Collections.emptyMap())
            .build();

    assertThrows(IllegalArgumentException.class, () -> generator.get(request));
  }

  @Test
  void shallReturnNonEmptyBytesForValidTemplateWithNoFields() throws IOException {
    final var request =
        CustomPdf.builder()
            .template(buildFk7210PdfTemplate())
            .metadata(TestDataFK7210CustomPdfMetadata.fullMetadata())
            .fields(Collections.emptyMap())
            .build();

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
