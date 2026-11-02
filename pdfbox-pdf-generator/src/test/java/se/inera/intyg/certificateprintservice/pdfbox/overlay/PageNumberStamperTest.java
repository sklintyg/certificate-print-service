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
package se.inera.intyg.certificateprintservice.pdfbox.overlay;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.TAGGED_PDF_RESOURCE;

import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PageNumberStamperTest {

  private final PageNumberStamper pageNumberStamper = new PageNumberStamper();

  private PDDocument document;

  @BeforeEach
  void setUp() throws IOException {
    try (InputStream stream = getClass().getResourceAsStream(TAGGED_PDF_RESOURCE)) {
      Assertions.assertNotNull(stream);
      document = Loader.loadPDF(stream.readAllBytes());
    }
  }

  @Nested
  class StampPageNumbers {

    @Test
    void shallNotThrowWhenStampingPageNumbers() {
      assertDoesNotThrow(() -> pageNumberStamper.stamp(document, 0));
    }

    @Test
    void shallStampAllPages() throws IOException {
      pageNumberStamper.stamp(document, 0);

      final var numberOfPages = document.getNumberOfPages();
      for (int i = 0; i < numberOfPages; i++) {
        final var page = document.getPage(i);
        final var contentStreams = page.getContentStreams();
        Assertions.assertTrue(
            contentStreams.hasNext(),
            "Page %d should have content streams after stamping".formatted(i));
      }
    }

    @Test
    void shallThrowWhenDocumentIsNull() {
      assertThrows(IllegalArgumentException.class, () -> pageNumberStamper.stamp(null, 0));
    }
  }
}
