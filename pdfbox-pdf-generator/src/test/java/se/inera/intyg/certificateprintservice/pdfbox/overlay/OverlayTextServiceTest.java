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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.TAGGED_PDF_RESOURCE;

import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210CustomPdfMetadata;

@ExtendWith(MockitoExtension.class)
class OverlayTextServiceTest {

  @Mock private PdfTextGenerator pdfTextGenerator;
  @InjectMocks private OverlayTextService overlayTextService;

  private PDDocument document;

  @BeforeEach
  void setUp() throws IOException {
    try (InputStream stream = getClass().getResourceAsStream(TAGGED_PDF_RESOURCE)) {
      Assertions.assertNotNull(stream);
      document = Loader.loadPDF(stream.readAllBytes());
    }
  }

  @Nested
  class DraftWatermark {

    @Test
    void shallDrawDraftWatermarkWhenAddDraftWatermarkIsTrue() throws IOException {
      overlayTextService.drawOverlays(document, TestDataFK7210CustomPdfMetadata.draftMetadata());

      verify(pdfTextGenerator).addWatermark(eq(document), eq("UTKAST"), anyInt());
    }

    @Test
    void shallNotDrawDraftWatermarkWhenAddDraftWatermarkIsFalse() throws IOException {
      overlayTextService.drawOverlays(document, TestDataFK7210CustomPdfMetadata.signedMetadata());

      verify(pdfTextGenerator, never()).addWatermark(any(), eq("UTKAST"), anyInt());
    }
  }

  @Nested
  class WatermarkText {

    @Test
    void shallDrawMultipleWatermarksWhenMetadataContainsMultiple() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.signedAndSentMetadata());

      // One watermark with specific positioning, two watermarks as sent text
      verify(pdfTextGenerator, times(1))
          .addDigitalSignatureText(
              eq(document),
              contains(
                  "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren."),
              eq(100.0f),
              eq(50.0f),
              anyInt(),
              anyInt(),
              anyInt());
      verify(pdfTextGenerator, times(2)).addSentText(any(), any(), anyInt());
    }

    @Test
    void shallDrawWatermarkWithSpecificPositioning() throws IOException {
      overlayTextService.drawOverlays(document, TestDataFK7210CustomPdfMetadata.signedMetadata());

      verify(pdfTextGenerator)
          .addDigitalSignatureText(
              eq(document),
              contains("Detta är en utskrift av ett elektroniskt intyg"),
              eq(100.0f),
              eq(50.0f),
              anyInt(),
              eq(15),
              eq(3));
    }
  }

  @Nested
  class MarginText {

    @Test
    void shallDrawMarginTextWhenMetadataContainsRightMarginText() throws IOException {
      overlayTextService.drawOverlays(document, TestDataFK7210CustomPdfMetadata.signedMetadata());

      verify(pdfTextGenerator)
          .addMarginText(
              eq(document),
              contains(TestDataFK7210CustomPdfMetadata.RIGHT_MARGIN_TEXT),
              anyInt(),
              eq(0));
    }

    @Test
    void shallNotDrawMarginTextWhenMetadataDoesNotContainRightMarginText() throws IOException {
      overlayTextService.drawOverlays(document, TestDataFK7210CustomPdfMetadata.draftMetadata());

      verify(pdfTextGenerator, never()).addMarginText(any(), any(), anyInt(), anyInt());
    }
  }
}
