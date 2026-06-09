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
package se.inera.intyg.certificateprintservice.application.print.general.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.certificateprintservice.application.print.general.dto.PrintCertificateMetadataDTO.PrintCertificateMetadataDTOBuilder;

@Value
@Builder
@JsonDeserialize(builder = PrintCertificateMetadataDTOBuilder.class)
public class PrintCertificateMetadataDTO {

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
  List<String> unitInformation;
  GeneralPrintTextDTO generalPrintText;

  @JsonPOJOBuilder(withPrefix = "")
  public static class PrintCertificateMetadataDTOBuilder {}
}
