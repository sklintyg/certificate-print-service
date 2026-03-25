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
package se.inera.intyg.certificateprintservice.pdfgenerator.api;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Metadata {

  String name;
  String fileName;
  String version;
  String typeId;
  String certificateId;
  String signingDate;
  String sentDate;
  byte[] recipientLogo;
  String recipientName;
  String recipientId;
  boolean canSendElectronically;
  String applicationOrigin;
  String personId;
  String description;
  String issuerName;
  String issuingUnit;
  List<String> issuingUnitInfo;
  GeneralPrintText generalPrintText;

  public boolean isDraft() {
    return signingDate == null;
  }

  public boolean isSent() {
    return sentDate != null;
  }

  public boolean isSigned() {
    return signingDate != null;
  }

  public String getSigningDateAsString() {
    return LocalDate.parse(this.signingDate.split("T")[0]).toString();
  }
}
