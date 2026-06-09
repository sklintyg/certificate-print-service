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

import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.Certificate;
import se.inera.intyg.certificateprintservice.playwright.document.Content;

@Component
public class ContentConverter {

  public Content convert(Certificate certificate) {
    final var metadata = certificate.getMetadata();
    return Content.builder()
        .categories(certificate.getCategories())
        .certificateName(metadata.getName())
        .certificateType(metadata.getTypeId())
        .certificateVersion(metadata.getVersion())
        .recipientName(metadata.getRecipientName())
        .personId(metadata.getPersonId())
        .issuerName(metadata.getIssuerName())
        .issuingUnit(metadata.getIssuingUnit())
        .issuingUnitInfo(metadata.getIssuingUnitInfo())
        .signDate(metadata.isSigned() ? metadata.getSigningDateAsString() : null)
        .description(metadata.getDescription())
        .isDraft(metadata.isDraft())
        .isSent(metadata.isSent())
        .isCanSendElectronically(certificate.getMetadata().isCanSendElectronically())
        .build();
  }
}
