package se.inera.intyg.certificateprintservice.infrastructure.logging;


import static se.inera.intyg.certificateprintservice.MdcLogConstants.EVENT_ACTION;
import static se.inera.intyg.certificateprintservice.MdcLogConstants.EVENT_CERTIFICATE_ID;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.MdcCloseableMap;
import se.inera.intyg.certificateprintservice.MdcLogConstants;
import se.inera.intyg.certificateprintservice.pdfgenerator.event.CertificatePrintEventSubscriber;
import se.inera.intyg.certificateprintservice.pdfgenerator.event.model.CertificatePrintEvent;

@Service
@Slf4j
public class PrintCertificateEventLogService implements CertificatePrintEventSubscriber {

  @Override
  public void event(CertificatePrintEvent event) {
    try (MdcCloseableMap mdc = MdcCloseableMap.builder()
        .put(EVENT_ACTION, eventAction(event))
        .put(EVENT_CERTIFICATE_ID, eventCertificateId(event))
        .put(MdcLogConstants.EVENT_TYPE, eventType(event))
        .put(MdcLogConstants.EVENT_START, eventStart(event))
        .put(MdcLogConstants.EVENT_END, eventEnd(event))
        .put(MdcLogConstants.EVENT_DURATION, eventDuration(event))
        .build()
    ) {
      log.info("CertificatePrintEvent '{}' occurred on certificate '{}'.",
          event.getType().name(), event.getCertificateId()
      );
    }
  }


  private static String eventAction(CertificatePrintEvent event) {
    return event.getType().action();
  }

  private static String eventType(CertificatePrintEvent event) {
    return Arrays.toString(new String[]{event.getType().actionType()});
  }

  private static String eventStart(CertificatePrintEvent event) {
    return event.getStart().toString();
  }

  private static String eventEnd(CertificatePrintEvent event) {
    return event.getEnd().toString();
  }

  private static String eventDuration(CertificatePrintEvent event) {
    return Long.toString(event.duration());
  }

  private static String eventCertificateId(CertificatePrintEvent event) {
    return event.getCertificateId();
  }
}