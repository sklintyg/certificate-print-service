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
import java.awt.Color;
import java.io.IOException;
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
    int mcid = MaxMCIDExtractor.findNextMcid(document);

    if (metadata.isAddDraftWatermark()) {
      pdfTextGenerator.addWatermark(document, "UTKAST", ++mcid);
    }
    for (CustomText customText : metadata.getCustomTextList()) {
      pdfTextGenerator.drawText(document, TextInfo.builder()
          .customText(customText)
          .color(Color.gray)
          .mcid(++mcid)
          .build());
    }
    if (!StringUtils.isBlank(metadata.getRightMarginText())) {
      pdfTextGenerator.addMarginText(
          document, metadata.getRightMarginText(), ++mcid, 0);
    }
  }
}
