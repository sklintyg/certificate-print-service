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
  @Mock private PageNumberStamper pageNumberStamper;
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
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.metadataWithDraftWatermark());

      verify(pdfTextGenerator).addWatermark(eq(document), eq("UTKAST"), anyInt());
    }

    @Test
    void shallNotDrawDraftWatermarkWhenAddDraftWatermarkIsFalse() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.metadataWithCustomTextAndMargin());

      verify(pdfTextGenerator, never()).addWatermark(any(), eq("UTKAST"), anyInt());
    }
  }

  @Nested
  class WatermarkText {

    @Test
    void shallDrawWatermark() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.metadataWithDraftWatermark());

      verify(pdfTextGenerator, times(1)).addWatermark(eq(document), eq("UTKAST"), anyInt());
    }
  }

  @Nested
  class MarginText {

    @Test
    void shallDrawMarginTextWhenMetadataContainsRightMarginText() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.metadataWithCustomTextAndMargin());

      verify(pdfTextGenerator)
          .addMarginText(
              eq(document), contains(TestDataFK7210CustomPdfMetadata.RIGHT_MARGIN_TEXT), eq(47));
    }

    @Test
    void shallNotDrawMarginTextWhenMetadataDoesNotContainRightMarginText() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.metadataWithDraftWatermark());

      verify(pdfTextGenerator, never()).addMarginText(any(), any(), anyInt());
    }
  }

  @Nested
  class PageNumbers {

    @Test
    void shallStampPageNumbersWhenOverflowPageIndexIsSet() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.metadataWithPageNumbers());

      verify(pageNumberStamper).stamp(eq(document), anyInt());
    }

    @Test
    void shallNotStampPageNumbersWhenOverflowPageIndexIsNull() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.metadataWithoutPageNumbers());

      verify(pageNumberStamper, never()).stamp(any(), anyInt());
    }
  }
}
