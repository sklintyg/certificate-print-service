package se.inera.intyg.certificateprintservice.playwright.converters;

import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Metadata;
import se.inera.intyg.certificateprintservice.playwright.document.RightMarginInfo;

@Component
public class RightMarginInfoConverter {

  public RightMarginInfo convert(Metadata metadata) {
    return RightMarginInfo.builder()
        .certificateId(metadata.getCertificateId())
        .build();
  }

}
