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
package se.inera.intyg.certificateprintservice.application.print.general.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.application.print.general.dto.GeneralPrintTextDTO;
import se.inera.intyg.certificateprintservice.application.print.general.dto.PrintCertificateMetadataDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.GeneralPrintText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.Metadata;

@Component
public class PrintCertificateMetadataConverter {

  public Metadata convert(PrintCertificateMetadataDTO metadata) {
    return Metadata.builder()
        .name(metadata.getName())
        .fileName(metadata.getFileName())
        .description(metadata.getDescription())
        .version(metadata.getVersion())
        .typeId(metadata.getTypeId())
        .certificateId(metadata.getCertificateId())
        .applicationOrigin(metadata.getApplicationOrigin())
        .personId(metadata.getPersonId())
        .recipientLogo(metadata.getRecipientLogo())
        .recipientName(metadata.getRecipientName())
        .recipientId(metadata.getRecipientId())
        .signingDate(metadata.getSigningDate())
        .sentDate(metadata.getSentDate())
        .issuingUnitInfo(metadata.getUnitInformation())
        .issuerName(metadata.getIssuerName())
        .issuingUnit(metadata.getIssuingUnit())
        .canSendElectronically(metadata.isCanSendElectronically())
        .generalPrintText(convertToGeneralPrintText(metadata.getGeneralPrintText()))
        .build();
  }

  private static GeneralPrintText convertToGeneralPrintText(GeneralPrintTextDTO text) {
    if (isGeneralPrintTextMissing(text)) {
      return null;
    }
    return GeneralPrintText.builder()
        .leftMarginInfoText(text.getLeftMarginInfoText())
        .draftAlertInfoText(text.getDraftAlertInfoText())
        .build();
  }

  private static boolean isGeneralPrintTextMissing(GeneralPrintTextDTO text) {
    return text == null
        || (text.getLeftMarginInfoText() == null && text.getDraftAlertInfoText() == null);
  }
}
