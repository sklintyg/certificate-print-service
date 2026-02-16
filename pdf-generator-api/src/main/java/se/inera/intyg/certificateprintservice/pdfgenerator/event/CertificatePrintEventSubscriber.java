package se.inera.intyg.certificateprintservice.pdfgenerator.event;


import se.inera.intyg.certificateprintservice.pdfgenerator.event.model.CertificatePrintEvent;

public interface CertificatePrintEventSubscriber {

  void event(CertificatePrintEvent event);

}