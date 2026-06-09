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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.CertificateStatus;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.CustomPdfMetadata;

@Service
@RequiredArgsConstructor
public class OverlayTextService {

  private static final String WATERMARK_TEXT = "UTKAST";
  private static final String DIGITALLY_SIGNED_TEXT =
      "Detta är en utskrift av ett elektroniskt intyg. "
          + "Intyget har signerats elektroniskt av intygsutfärdaren.";

  static final int SIGNATURE_X_PADDING = 60;
  static final int SIGNATURE_Y_PADDING = 2;

  private final PdfTextGenerator pdfTextGenerator;

  public void drawOverlays(PDDocument document, CustomPdfMetadata metadata) throws IOException {
    final var mcid = new AtomicInteger(metadata.getStartMcid());

    drawDraftWatermark(document, metadata, mcid);
    drawSignatureText(document, metadata, mcid);
    drawSentText(document, metadata, mcid);
    drawMarginText(document, metadata, mcid, 0);
  }

  private void drawDraftWatermark(
      PDDocument document, CustomPdfMetadata metadata, AtomicInteger mcid) throws IOException {
    if (metadata.getStatus().equals(CertificateStatus.DRAFT)) {
      pdfTextGenerator.addWatermark(document, WATERMARK_TEXT, mcid.getAndIncrement());
    }
  }

  private void drawSignatureText(
      PDDocument document, CustomPdfMetadata metadata, AtomicInteger mcid) throws IOException {
    if (!metadata.getStatus().equals(CertificateStatus.SIGNED)) {
      return;
    }

    final var acroForm = document.getDocumentCatalog().getAcroForm();
    final var signedDateField = acroForm.getField(metadata.getSignedDateFieldId());

    if (signedDateField == null) {
      throw new IllegalStateException("Signed date field is missing");
    }

    final var rectangle = signedDateField.getWidgets().getFirst().getRectangle();
    final var xPosition = rectangle.getUpperRightX() + SIGNATURE_X_PADDING;
    final var yPosition = rectangle.getLowerLeftY() + SIGNATURE_Y_PADDING;

    pdfTextGenerator.addDigitalSignatureText(
        document,
        DIGITALLY_SIGNED_TEXT,
        xPosition,
        yPosition,
        mcid.getAndIncrement(),
        metadata.getSignatureTagIndex(),
        metadata.getSignaturePageIndex());
  }

  private void drawSentText(PDDocument document, CustomPdfMetadata metadata, AtomicInteger mcid)
      throws IOException {
    if (!metadata.isSent()) {
      return;
    }

    final var sentLine =
        "Intyget har skickats digitalt till %s".formatted(metadata.getSentRecipientName());

    pdfTextGenerator.addSentText(document, sentLine, mcid.getAndIncrement());

    if (metadata.isAvailableForCitizen()) {
      pdfTextGenerator.addSentVisibilityText(
          document, "Du kan se intyget genom att logga in på 1177.se", mcid.getAndIncrement());
    }
  }

  private void drawMarginText(
      PDDocument document, CustomPdfMetadata metadata, AtomicInteger mcid, int pageIndex)
      throws IOException {
    if (!metadata.getStatus().equals(CertificateStatus.SIGNED)) {
      return;
    }

    final var text =
        "Intygsid: %s. %s".formatted(metadata.getCertificateId(), metadata.getAdditionalInfoText());

    pdfTextGenerator.addMarginText(document, text, mcid.getAndIncrement(), pageIndex);
  }
}
