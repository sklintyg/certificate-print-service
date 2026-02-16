package se.inera.intyg.certificateprintservice.pdfgenerator.event;

import java.util.List;
import lombok.RequiredArgsConstructor;
import se.inera.intyg.certificateprintservice.pdfgenerator.event.model.CertificatePrintEvent;

@RequiredArgsConstructor
public class CertificatePrintEventService {

  private final List<CertificatePrintEventSubscriber> subscribers;

  public void publish(CertificatePrintEvent event) {
    if (event == null) {
      throw new IllegalArgumentException("Event is null and cannot be published!");
    }
    subscribers.forEach(birthReportEventSubscriber -> birthReportEventSubscriber.event(event));
  }
}