package se.inera.intyg.certificateprintservice.application.print.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.application.print.dto.GeneralPrintTextDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateMetadataDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.GeneralPrintText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Metadata;

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
    return text == null || (text.getLeftMarginInfoText() == null
        && text.getDraftAlertInfoText() == null);
  }
}