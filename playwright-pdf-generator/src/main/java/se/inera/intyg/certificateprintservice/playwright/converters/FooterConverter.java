package se.inera.intyg.certificateprintservice.playwright.converters;

import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Metadata;
import se.inera.intyg.certificateprintservice.playwright.document.Footer;

@Component
public class FooterConverter {

  public Footer convert(Metadata metadata) {
    return Footer.builder()
        .applicationOrigin(metadata.getApplicationOrigin())
        .build();
  }

}
