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
import se.inera.intyg.certificateprintservice.application.print.converter.FillPdfRequestConverter;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.CertificateStatusDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.FillPdfRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.FillPdfResponseDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.FillPdfGenerator;

@Service
@RequiredArgsConstructor
public class FillPdfService {

  private final FillPdfGenerator fillPdfGenerator;
  private final FillPdfRequestConverter fillPdfRequestConverter;

  public FillPdfResponseDTO fill(FillPdfRequestDTO request) {
    validateRequest(request);
    final var fillPdf = fillPdfRequestConverter.convert(request);
    return FillPdfResponseDTO.builder()
        .pdfData(fillPdfGenerator.fill(fillPdf))
        .build();
  }

  private void validateRequest(FillPdfRequestDTO request) {
    if (request == null) {
      throw new IllegalArgumentException("Invalid request - request is null");
    }
    if (request.getTemplate() == null || request.getTemplate().isBlank()) {
      throw new IllegalArgumentException("Invalid request - Missing required parameter template");
    }
    if (request.getMetadata() == null) {
      throw new IllegalArgumentException("Invalid request - Missing required parameter metadata");
    }
    if (request.getFields() == null) {
      throw new IllegalArgumentException("Invalid request - Missing required parameter fields");
    }

    final var metadata = request.getMetadata();

    if (metadata.getCertificateId() == null || metadata.getCertificateId().isBlank()) {
      throw new IllegalArgumentException(
          "Invalid request - Missing required metadata parameter certificateId");
    }
    if (metadata.getPatientId() == null || metadata.getPatientId().isBlank()) {
      throw new IllegalArgumentException(
          "Invalid request - Missing required metadata parameter patientId");
    }
    if (metadata.isSent()
        && (metadata.getSentRecipientName() == null
            || metadata.getSentRecipientName().isBlank())) {
      throw new IllegalArgumentException(
          "Invalid request - sentRecipientName is required when isSent is true");
    }
    if (CertificateStatusDTO.SIGNED.equals(metadata.getStatus())
        && (metadata.getSignedDateFieldId() == null
            || metadata.getSignedDateFieldId().isBlank())) {
      throw new IllegalArgumentException(
          "Invalid request - signedDateFieldId is required when status is SIGNED");
    }
    if (metadata.getOverflowPageIndex() != null
        && (metadata.getPatientIdFieldId() == null
            || metadata.getPatientIdFieldId().isBlank())) {
      throw new IllegalArgumentException(
          "Invalid request - patientIdFieldId is required when overflowPageIndex is set");
    }
  }
}
