/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.certificateprintservice.infrastructure.logging;

import static se.inera.intyg.certificateprintservice.MdcLogConstants.EVENT_ACTION;
import static se.inera.intyg.certificateprintservice.MdcLogConstants.EVENT_CERTIFICATE_ID;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.MdcCloseableMap;
import se.inera.intyg.certificateprintservice.MdcLogConstants;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.event.CertificatePrintEventSubscriber;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.event.model.CertificatePrintEvent;

@Service
@Slf4j
public class PrintCertificateEventLogService implements CertificatePrintEventSubscriber {

  @Override
  public void event(CertificatePrintEvent event) {
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(EVENT_ACTION, eventAction(event))
            .put(EVENT_CERTIFICATE_ID, eventCertificateId(event))
            .put(MdcLogConstants.EVENT_TYPE, eventType(event))
            .put(MdcLogConstants.EVENT_START, eventStart(event))
            .put(MdcLogConstants.EVENT_END, eventEnd(event))
            .put(MdcLogConstants.EVENT_DURATION, eventDuration(event))
            .build()) {
      log.info(
          "CertificatePrintEvent '{}' occurred on certificate '{}'.",
          event.getType().name(),
          event.getCertificateId());
    }
  }

  private static String eventAction(CertificatePrintEvent event) {
    return event.getType().action();
  }

  private static String eventType(CertificatePrintEvent event) {
    return Arrays.toString(new String[] {event.getType().actionType()});
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
