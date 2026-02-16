package se.inera.intyg.certificateprintservice.pdfgenerator;

import se.inera.intyg.certificateprintservice.pdfgenerator.api.Certificate;

public interface PrintCertificateGenerator {

  byte[] generate(Certificate certificate);

}