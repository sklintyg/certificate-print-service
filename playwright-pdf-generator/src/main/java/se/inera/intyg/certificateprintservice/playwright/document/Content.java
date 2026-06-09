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

import static se.inera.intyg.certificateprintservice.playwright.element.ElementProvider.element;

import java.util.List;
import javax.swing.text.html.HTML.Tag;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jsoup.nodes.Element;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.Category;
import se.inera.intyg.certificateprintservice.playwright.certificate.CategoryConverter;
import se.inera.intyg.certificateprintservice.playwright.element.ContentElementFactory;

@Builder
@Getter
@EqualsAndHashCode
public class Content {

  List<Category> categories;
  String certificateName;
  String certificateType;
  String certificateVersion;
  String recipientName;
  String personId;
  String issuerName;
  String issuingUnit;
  List<String> issuingUnitInfo;
  String signDate;
  String description;
  boolean isDraft;
  boolean isSent;
  boolean isCanSendElectronically;
  String draftAlertInfoText;

  public Element create() {
    return element(Tag.DIV)
        .appendChild(
            ContentElementFactory.hiddenAccessibleHeader(
                certificateName,
                certificateType,
                certificateVersion,
                recipientName,
                personId,
                isDraft,
                isSent,
                isCanSendElectronically,
                draftAlertInfoText))
        .appendChildren(content())
        .appendChildren(
            List.of(
                ContentElementFactory.issuerInfo(
                    issuerName, issuingUnit, issuingUnitInfo, signDate, isDraft),
                ContentElementFactory.certificateInformation(
                    certificateName, description, isCanSendElectronically)));
  }

  private List<Element> content() {
    return categories.stream().map(CategoryConverter::category).toList();
  }
}
