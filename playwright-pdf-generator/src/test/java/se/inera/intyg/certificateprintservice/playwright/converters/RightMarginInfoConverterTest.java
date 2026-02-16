package se.inera.intyg.certificateprintservice.playwright.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Metadata;

class RightMarginInfoConverterTest {

  private final RightMarginInfoConverter rightMarginInfoConverter = new RightMarginInfoConverter();

  private static final String CERTIFICATE_ID = "certificateId";
  private static final Metadata METADATA = Metadata.builder()
      .certificateId(CERTIFICATE_ID)
      .build();

  @Test
  void shouldSetCertificateId() {
    final var response = rightMarginInfoConverter.convert(METADATA);
    assertEquals(CERTIFICATE_ID, response.getCertificateId());
  }

}
