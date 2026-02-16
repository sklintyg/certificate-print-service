package se.inera.intyg.certificateprintservice.playwright.converters;

import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Metadata;
import se.inera.intyg.certificateprintservice.playwright.document.LeftMarginInfo;

@Component
public class LeftMarginInfoConverter {

  public LeftMarginInfo convert(Metadata metadata) {
    return LeftMarginInfo.builder()
        .certificateType(metadata.getTypeId())
        .recipientName(metadata.getRecipientName())
        .certificateVersion(metadata.getVersion())
        .recipientId(metadata.getRecipientId())
        .leftMarginText(metadata.getGeneralPrintText() != null ? metadata.getGeneralPrintText()
            .getLeftMarginInfoText() : null)
        .build();
  }

}
