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
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.CertificateStatus;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.CustomPdfMetadata;

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
    void shallDrawWatermarkWhenStatusIsDraft() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.metadataWithStatus(CertificateStatus.DRAFT));

      verify(pdfTextGenerator).addWatermark(eq(document), eq("UTKAST"), anyInt());
    }

    @Test
    void shallNotDrawWatermarkWhenStatusIsNotDraft() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.metadataWithStatus(CertificateStatus.SIGNED));

      verify(pdfTextGenerator, never()).addWatermark(any(), any(), anyInt());
    }
  }

  @Nested
  class SentText {

    @Test
    void shallDrawSentTextWhenIsSent() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.signedAndSentMetadata());

      verify(pdfTextGenerator)
          .addSentText(
              eq(document), contains(TestDataFK7210CustomPdfMetadata.RECIPIENT_NAME), anyInt());
    }

    @Test
    void shallDrawCitizenVisibilityLineWhenAvailableForCitizen() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.signedAndSentMetadata());

      verify(pdfTextGenerator).addSentVisibilityText(eq(document), any(), anyInt());
    }

    @Test
    void shallNotDrawCitizenLineWhenNotAvailableForCitizen() throws IOException {
      final var metadata =
          CustomPdfMetadata.builder()
              .status(CertificateStatus.SIGNED)
              .isSent(true)
              .sentRecipientName(TestDataFK7210CustomPdfMetadata.RECIPIENT_NAME)
              .availableForCitizen(false)
              .certificateId(TestDataFK7210CustomPdfMetadata.CERTIFICATE_ID)
              .signedDateFieldId(TestDataFK7210CustomPdfMetadata.SIGNED_DATE_FIELD_ID)
              .startMcid(TestDataFK7210CustomPdfMetadata.START_MCID)
              .build();

      overlayTextService.drawOverlays(document, metadata);

      verify(pdfTextGenerator, never()).addSentVisibilityText(any(), any(), anyInt());
    }

    @Test
    void shallNotDrawSentTextWhenNotSent() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.metadataWithStatus(CertificateStatus.SIGNED));

      verify(pdfTextGenerator, never()).addSentText(any(), any(), anyInt());
    }
  }

  @Nested
  class MarginText {

    @Test
    void shallDrawMarginTextWhenStatusIsSigned() throws IOException {
      overlayTextService.drawOverlays(document, TestDataFK7210CustomPdfMetadata.signedMetadata());

      verify(pdfTextGenerator)
          .addMarginText(
              eq(document),
              contains(TestDataFK7210CustomPdfMetadata.CERTIFICATE_ID),
              anyInt(),
              eq(0));
    }

    @Test
    void shallNotDrawMarginTextWhenStatusIsNotSigned() throws IOException {
      overlayTextService.drawOverlays(
          document, TestDataFK7210CustomPdfMetadata.metadataWithStatus(CertificateStatus.DRAFT));

      verify(pdfTextGenerator, never()).addMarginText(any(), any(), anyInt(), anyInt());
    }
  }
}
