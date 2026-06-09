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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.CertificateStatus;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.CustomPdfMetadata;

@ExtendWith(MockitoExtension.class)
class OverlayTextServiceTest {

  @Mock private PdfTextGenerator pdfTextGenerator;
  @InjectMocks private OverlayTextService overlayTextService;

  private PDDocument document;

  @BeforeEach
  void setUp() {
    document = new PDDocument();
    document.addPage(new PDPage());
  }

  @Nested
  class DraftWatermark {

    @Test
    void shallDrawWatermarkWhenStatusIsDraft() throws IOException {
      overlayTextService.drawOverlays(document, metadataWithStatus(CertificateStatus.DRAFT));

      verify(pdfTextGenerator).addWatermark(eq(document), eq("UTKAST"), anyInt());
    }

    @Test
    void shallNotDrawWatermarkWhenStatusIsNotDraft() throws IOException {
      overlayTextService.drawOverlays(document, metadataWithStatus(CertificateStatus.SIGNED));

      verify(pdfTextGenerator, never()).addWatermark(any(), any(), anyInt());
    }
  }

  @Nested
  class SentText {

    @Test
    void shallDrawSentTextWhenIsSent() throws IOException {
      final var metadata =
          CustomPdfMetadata.builder()
              .status(CertificateStatus.LOCKED_DRAFT)
              .isSent(true)
              .sentRecipientName("Försäkringskassan")
              .availableForCitizen(false)
              .certificateId("id")
              .startMcid(0)
              .build();

      overlayTextService.drawOverlays(document, metadata);

      verify(pdfTextGenerator).addSentText(eq(document), contains("Försäkringskassan"), anyInt());
    }

    @Test
    void shallDrawCitizenVisibilityLineWhenAvailableForCitizen() throws IOException {
      final var metadata =
          CustomPdfMetadata.builder()
              .status(CertificateStatus.LOCKED_DRAFT)
              .isSent(true)
              .sentRecipientName("FK")
              .availableForCitizen(true)
              .certificateId("id")
              .startMcid(0)
              .build();

      overlayTextService.drawOverlays(document, metadata);

      verify(pdfTextGenerator).addSentVisibilityText(eq(document), any(), anyInt());
    }

    @Test
    void shallNotDrawCitizenLineWhenNotAvailableForCitizen() throws IOException {
      final var metadata =
          CustomPdfMetadata.builder()
              .status(CertificateStatus.LOCKED_DRAFT)
              .isSent(true)
              .sentRecipientName("FK")
              .availableForCitizen(false)
              .certificateId("id")
              .startMcid(0)
              .build();

      overlayTextService.drawOverlays(document, metadata);

      verify(pdfTextGenerator, never()).addSentVisibilityText(any(), any(), anyInt());
    }

    @Test
    void shallNotDrawSentTextWhenNotSent() throws IOException {
      overlayTextService.drawOverlays(document, metadataWithStatus(CertificateStatus.SIGNED));

      verify(pdfTextGenerator, never()).addSentText(any(), any(), anyInt());
    }
  }

  @Nested
  class MarginText {

    @Test
    void shallDrawMarginTextWhenStatusIsSigned() throws IOException {
      final var metadata =
          CustomPdfMetadata.builder()
              .status(CertificateStatus.SIGNED)
              .isSent(false)
              .certificateId("cert-123")
              .additionalInfoText("Webcert")
              .addPageNumbers(false)
              .startMcid(0)
              .build();

      overlayTextService.drawOverlays(document, metadata);

      verify(pdfTextGenerator).addMarginText(eq(document), contains("cert-123"), anyInt(), eq(0));
    }

    @Test
    void shallNotDrawMarginTextWhenStatusIsNotSigned() throws IOException {
      overlayTextService.drawOverlays(document, metadataWithStatus(CertificateStatus.DRAFT));

      verify(pdfTextGenerator, never()).addMarginText(any(), any(), anyInt(), anyInt());
    }
  }

  @Nested
  class PageNumbers {

    @Test
    void shallDrawPageNumbersWhenAddPageNumbersIsTrue() throws IOException {
      final var metadata =
          CustomPdfMetadata.builder()
              .status(CertificateStatus.LOCKED_DRAFT)
              .isSent(false)
              .certificateId("id")
              .addPageNumbers(true)
              .startMcid(0)
              .build();

      overlayTextService.drawOverlays(document, metadata);

      verify(pdfTextGenerator).addPageNumber(eq(document), eq(0), eq(1), anyInt());
    }

    @Test
    void shallNotDrawPageNumbersWhenAddPageNumbersIsFalse() throws IOException {
      final var metadata =
          CustomPdfMetadata.builder()
              .status(CertificateStatus.LOCKED_DRAFT)
              .isSent(false)
              .certificateId("id")
              .addPageNumbers(false)
              .startMcid(0)
              .build();

      overlayTextService.drawOverlays(document, metadata);

      verify(pdfTextGenerator, never()).addPageNumber(any(), anyInt(), anyInt(), anyInt());
    }
  }

  // --- helpers ---

  private CustomPdfMetadata metadataWithStatus(CertificateStatus status) {
    return CustomPdfMetadata.builder()
        .status(status)
        .isSent(false)
        .certificateId("cert-id")
        .addPageNumbers(false)
        .startMcid(0)
        .build();
  }
}
