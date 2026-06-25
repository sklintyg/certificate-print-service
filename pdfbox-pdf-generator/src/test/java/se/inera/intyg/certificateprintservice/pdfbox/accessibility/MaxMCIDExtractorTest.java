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
package se.inera.intyg.certificateprintservice.pdfbox.accessibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.TAGGED_PDF_RESOURCE;

import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MaxMCIDExtractorTest {

  private PDDocument document;

  @AfterEach
  void tearDown() throws IOException {
    if (document != null) {
      document.close();
    }
  }

  @Nested
  class WithTaggedPdf {

    @BeforeEach
    void setUp() throws IOException {
      try (InputStream stream = getClass().getResourceAsStream(TAGGED_PDF_RESOURCE)) {
        document = Loader.loadPDF(stream.readAllBytes());
      }
    }

    @Test
    void shallReturn45ForTaggedTestTemplate() {
      assertEquals(45, MaxMCIDExtractor.findNextMcid(document));
    }
  }

  @Nested
  class WithUntaggedPdf {

    @Test
    void shallReturnMinusOneWhenNoMarkInfo() throws IOException {
      document = new PDDocument();
      document.addPage(new PDPage());

      assertEquals(-1, MaxMCIDExtractor.findNextMcid(document));
    }
  }
}
