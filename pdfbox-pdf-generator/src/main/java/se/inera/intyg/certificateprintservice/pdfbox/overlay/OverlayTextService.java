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

import io.micrometer.common.util.StringUtils;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.pdfbox.accessibility.MaxMCIDExtractor;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfMetadata;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomText;

@Service
@RequiredArgsConstructor
public class OverlayTextService {

  private final PdfTextGenerator pdfTextGenerator;

  public void drawOverlays(PDDocument document, CustomPdfMetadata metadata) throws IOException {
    final var mcid = new AtomicInteger(MaxMCIDExtractor.findNextMcid(document));

    drawDraftWatermark(document, metadata, mcid);
    drawWaterMarks(document, metadata, mcid);
    drawMarginText(document, metadata, mcid);
  }

  private void drawDraftWatermark(
      PDDocument document, CustomPdfMetadata metadata, AtomicInteger mcid) throws IOException {
    if (metadata.isAddDraftWatermark()) {
      pdfTextGenerator.addWatermark(document, "UTKAST", mcid.getAndIncrement());
    }
  }

  private void drawWaterMarks(PDDocument document, CustomPdfMetadata metadata, AtomicInteger mcid)
      throws IOException {

    for (CustomText customText : metadata.getCustomTextList()) {
      drawText(document, customText, mcid.getAndIncrement());
    }
  }

  // TODO: refactor addDigitalSignatureText to include font size and be general drawtext, we should
  // be able to delete addsenttext method.
  private void drawText(PDDocument document, CustomText customText, int mcid) throws IOException {
    if (customText.getX() != null
        && customText.getY() != null
        && customText.getPageIndex() != null
        && customText.getTagIndex() != null) {
      pdfTextGenerator.addDigitalSignatureText(
          document,
          customText.getValue(),
          customText.getX(),
          customText.getY(),
          mcid,
          customText.getTagIndex(),
          customText.getPageIndex());
    } else {
      // For custom text without specific positioning, add as sent text
      pdfTextGenerator.addSentText(document, customText.getValue(), mcid);
    }
  }

  private void drawMarginText(PDDocument document, CustomPdfMetadata metadata, AtomicInteger mcid)
      throws IOException {
    if (StringUtils.isBlank(metadata.getRightMarginText())) {
      return;
    }

    pdfTextGenerator.addMarginText(
        document, metadata.getRightMarginText(), mcid.getAndIncrement(), 0);
  }
}
