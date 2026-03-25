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
