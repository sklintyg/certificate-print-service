package se.inera.intyg.certificateprintservice.playwright.converters;

import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Certificate;
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
