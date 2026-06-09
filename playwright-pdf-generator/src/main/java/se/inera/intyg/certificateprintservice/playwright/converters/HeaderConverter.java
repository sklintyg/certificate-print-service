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
package se.inera.intyg.certificateprintservice.playwright.converters;

import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.Metadata;
import se.inera.intyg.certificateprintservice.playwright.document.Header;
import se.inera.intyg.certificateprintservice.playwright.document.Watermark;

@Component
@RequiredArgsConstructor
public class HeaderConverter {

  private final LeftMarginInfoConverter leftMarginInfoConverter;
  private final RightMarginInfoConverter rightMarginInfoConverter;

  private static final String HEADER = "header";

  public Header convert(Metadata metadata) {
    return Header.builder()
        .certificateName(metadata.getName())
        .certificateType(metadata.getTypeId())
        .certificateVersion(metadata.getVersion())
        .personId(metadata.getPersonId())
        .recipientName(metadata.getRecipientName())
        .recipientLogo(metadata.getRecipientLogo())
        .leftMarginInfo(leftMarginInfoConverter.convert(metadata))
        .rightMarginInfo(rightMarginInfoConverter.convert(metadata))
        .watermark(Watermark.builder().build())
        .isDraft(metadata.isDraft())
        .isSent(metadata.isSent())
        .isCanSendElectronically(metadata.isCanSendElectronically())
        .draftAlertInfoText(
            metadata.getGeneralPrintText() != null
                ? metadata.getGeneralPrintText().getDraftAlertInfoText()
                : null)
        .build();
  }

  public int headerHeight(Page page, String header) {
    page.setContent(header);
    return (int) page.getByTitle(HEADER).evaluate("node => node.offsetHeight");
  }
}
