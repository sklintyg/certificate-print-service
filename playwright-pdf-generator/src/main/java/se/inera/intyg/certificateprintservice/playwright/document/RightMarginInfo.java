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

import static se.inera.intyg.certificateprintservice.playwright.document.Constants.RIGHT_MARGIN_INFO_STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.element.ElementProvider.element;

import javax.swing.text.html.HTML.Tag;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jsoup.nodes.Element;

@Builder
@Getter
@EqualsAndHashCode
public class RightMarginInfo {

  String certificateId;

  private static final String CERTIFICATE_ID_TEXT = "Intygs-ID: %s";

  public Element create() {
    return element(Tag.DIV).attr(STYLE, RIGHT_MARGIN_INFO_STYLE).appendChild(rightMarginInfo());
  }

  private Element rightMarginInfo() {
    final var info = CERTIFICATE_ID_TEXT.formatted(certificateId);
    return element(Tag.P).attr(STYLE, "margin: 0;").text(info);
  }
}
