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
package se.inera.intyg.certificateprintservice.application.print;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.application.print.converter.custom.CustomPdfRequestConverter;
import se.inera.intyg.certificateprintservice.application.print.dto.custom.CertificateStatusDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.custom.CustomPdfMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.custom.CustomPrintRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.custom.CustomPrintResponseDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.CustomPdfGenerator;

@Service
@RequiredArgsConstructor
public class CustomPrintService {

  private final CustomPdfGenerator customPdfGenerator;
  private final CustomPdfRequestConverter customPdfRequestConverter;

  public CustomPrintResponseDTO get(CustomPrintRequestDTO request) {
    validateCrossFieldConstraints(request.getMetadata());
    final var customPdf = customPdfRequestConverter.convert(request);
    return CustomPrintResponseDTO.builder()
        .pdfData(customPdfGenerator.get(customPdf))
        .build();
  }

  private void validateCrossFieldConstraints(CustomPdfMetadataDTO metadata) {
    validateSentRecipient(metadata);
    validateSignedDateField(metadata);
  }

  private void validateSentRecipient(CustomPdfMetadataDTO metadata) {
    if (metadata.isSent() && isBlank(metadata.getSentRecipientName())) {
      throw new IllegalArgumentException(
          "Invalid request - sentRecipientName is required when isSent is true");
    }
  }

  private void validateSignedDateField(CustomPdfMetadataDTO metadata) {
    if (CertificateStatusDTO.SIGNED.equals(metadata.getStatus()) && isBlank(
        metadata.getSignedDateFieldId())) {
      throw new IllegalArgumentException(
          "Invalid request - signedDateFieldId is required when status is SIGNED");
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
