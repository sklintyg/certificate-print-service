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
package se.inera.intyg.certificateprintservice.playwright.element;

import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.element.ElementProvider.element;

import java.util.List;
import javax.swing.text.html.HTML.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Element;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FooterElementFactory {

  private static final String LINK_TEXT = "www.inera.se";
  private static final String FOOTER_INFO_TEXT =
      "Utskriften skapades med %s - en tjänst som drivs av Inera AB";

  public static Element info(String origin) {
    return element(Tag.DIV)
        .appendChildren(
            List.of(
                element(Tag.P)
                    .attr(STYLE, "display: block margin-top: 5mm; margind-bottom: 2mm;")
                    .text(FOOTER_INFO_TEXT.formatted(origin)),
                element(Tag.P)
                    .attr(STYLE, "display: block margin-top: 5mm; margind-bottom: 2mm;")
                    .text(LINK_TEXT)));
  }

  public static Element pageNumber() {
    return element(Tag.DIV)
        .attr(STYLE, "margin-top: 5mm;")
        .appendChildren(
            List.of(
                element(Tag.SPAN).addClass("pageNumber"),
                element(Tag.SPAN).text(" ("),
                element(Tag.SPAN).addClass("totalPages"),
                element(Tag.SPAN).text(")")));
  }
}
