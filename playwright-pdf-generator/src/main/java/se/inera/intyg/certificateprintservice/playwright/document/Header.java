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
package se.inera.intyg.certificateprintservice.playwright.document;

import static se.inera.intyg.certificateprintservice.playwright.document.Constants.HEADER;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.HEADER_STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TITLE;
import static se.inera.intyg.certificateprintservice.playwright.element.ElementProvider.element;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.html.HTML.Tag;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jsoup.nodes.Element;
import se.inera.intyg.certificateprintservice.playwright.element.HeaderElementFactory;

@Builder
@Value
@EqualsAndHashCode
public class Header {

  String certificateName;
  String certificateType;
  String certificateVersion;
  String personId;
  String recipientName;
  LeftMarginInfo leftMarginInfo;
  RightMarginInfo rightMarginInfo;
  Watermark watermark;
  byte[] recipientLogo;
  boolean isDraft;
  boolean isSent;
  boolean isCanSendElectronically;
  String draftAlertInfoText;

  public Element create() {
    final var headerElements = new ArrayList<>(List.of(header(), leftMarginInfo.create()));

    if (!isDraft) {
      headerElements.add(rightMarginInfo.create());
    }

    if (isDraft) {
      headerElements.add(watermark.create());
    }

    return element(Tag.DIV).appendChildren(headerElements);
  }

  private Element header() {
    return element(Tag.DIV)
        .attr(STYLE, HEADER_STYLE)
        .attr(TITLE, HEADER)
        .appendChildren(List.of(pageHeader(), certificateHeader()));
  }

  private Element pageHeader() {
    return element(Tag.DIV)
        .attr(STYLE, "display: flex; top: 0; left: 0; margin-bottom: 10mm")
        .appendChildren(
            List.of(
                HeaderElementFactory.recipientLogo(recipientLogo, recipientName),
                HeaderElementFactory.personId(personId)));
  }

  private Element certificateHeader() {
    return element(Tag.DIV)
        .attr(STYLE, "margin-bottom: 5mm")
        .appendChildren(
            List.of(
                HeaderElementFactory.title(certificateName, certificateType, certificateVersion),
                HeaderElementFactory.alert(
                    recipientName, isDraft, isSent, isCanSendElectronically, draftAlertInfoText)));
  }
}
