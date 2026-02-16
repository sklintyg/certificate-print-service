package se.inera.intyg.certificateprintservice.playwright.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Metadata;

class FooterConverterTest {

  private final FooterConverter footerConverter = new FooterConverter();

  private static final String APPLICATION_ORIGIN = "applicationOrigin";
  private static final Metadata METADATA = Metadata.builder()
      .applicationOrigin(APPLICATION_ORIGIN)
      .build();

  @Test
  void shouldSetApplicationOrigin() {
    final var response = footerConverter.convert(METADATA);
    assertEquals(APPLICATION_ORIGIN, response.getApplicationOrigin());
  }

}
