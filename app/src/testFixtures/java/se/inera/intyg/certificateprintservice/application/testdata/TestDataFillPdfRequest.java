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
import se.inera.intyg.certificateprintservice.application.print.dto.fill.CertificateStatusDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.FillPdfRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.PdfMetadataOptionsDTO;

public class TestDataFillPdfRequest {

  public static final String VALID_TEMPLATE =
      Base64.getEncoder().encodeToString("pdf-bytes".getBytes(StandardCharsets.UTF_8));
  public static final String CERTIFICATE_ID = "cert-id";

  private TestDataFillPdfRequest() {
    throw new IllegalStateException("Utility class");
  }

  public static PdfMetadataOptionsDTO.PdfMetadataOptionsDTOBuilder validMetadataBuilder() {
    return PdfMetadataOptionsDTO.builder()
        .status(CertificateStatusDTO.DRAFT)
        .certificateId(CERTIFICATE_ID);
  }

  public static FillPdfRequestDTO.FillPdfRequestDTOBuilder validRequestBuilder() {
    return FillPdfRequestDTO.builder()
        .template(VALID_TEMPLATE)
        .metadata(validMetadataBuilder().build())
        .fields(Collections.emptyMap());
  }

  public static FillPdfRequestDTO validRequest() {
    return validRequestBuilder().build();
  }
}
