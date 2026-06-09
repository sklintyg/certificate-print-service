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
package se.inera.intyg.certificateprintservice.application.testdata;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import se.inera.intyg.certificateprintservice.application.print.dto.custom.CertificateStatusDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.custom.CustomPdfFieldDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.custom.CustomPdfMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.custom.CustomPrintRequestDTO;

public class TestDataCustomPrintRequest {

  public static final byte[] TEMPLATE_BYTES = "pdf-template".getBytes(StandardCharsets.UTF_8);
  public static final String VALID_TEMPLATE = Base64.getEncoder().encodeToString(TEMPLATE_BYTES);
  public static final String CERTIFICATE_ID = "cert-id";

  private TestDataCustomPrintRequest() {
    throw new IllegalStateException("Utility class");
  }

  public static CustomPdfMetadataDTO.CustomPdfMetadataDTOBuilder validMetadataBuilder() {
    return CustomPdfMetadataDTO.builder()
        .status(CertificateStatusDTO.DRAFT)
        .certificateId(CERTIFICATE_ID);
  }

  public static CustomPdfMetadataDTO.CustomPdfMetadataDTOBuilder fullMetadataBuilder() {
    return CustomPdfMetadataDTO.builder()
        .status(CertificateStatusDTO.SIGNED)
        .isSent(true)
        .sentRecipientName("Försäkringskassan")
        .availableForCitizen(true)
        .certificateId("cert-123")
        .additionalInfoText("Webcert 2.0")
        .signaturePageIndex(0)
        .signatureTagIndex(5)
        .signedDateFieldId("signed-date-field")
        .startMcid(100);
  }

  public static CustomPrintRequestDTO buildRequest(
      CustomPdfMetadataDTO metadata, Map<String, CustomPdfFieldDTO> fields) {
    return CustomPrintRequestDTO.builder()
        .template(VALID_TEMPLATE)
        .metadata(metadata)
        .fields(fields)
        .build();
  }

  public static CustomPrintRequestDTO.CustomPrintRequestDTOBuilder validRequestBuilder() {
    return CustomPrintRequestDTO.builder()
        .template(VALID_TEMPLATE)
        .metadata(validMetadataBuilder().build())
        .fields(Collections.emptyMap());
  }

  public static CustomPrintRequestDTO validRequest() {
    return validRequestBuilder().build();
  }
}
