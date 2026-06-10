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
package se.inera.intyg.certificateprintservice.application.print.custom.converter;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfFieldDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintRequestDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CertificateStatus;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdf;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfMetadata;

@Component
public class CustomPdfRequestConverter {

  public CustomPdf convert(CustomPrintRequestDTO request) {
    return CustomPdf.builder()
        .template(Base64.getDecoder().decode(request.getTemplate()))
        .metadata(convertMetadata(request.getMetadata()))
        .fields(convertFields(request.getFields()))
        .build();
  }

  private CustomPdfMetadata convertMetadata(CustomPdfMetadataDTO dto) {
    return CustomPdfMetadata.builder()
        .status(CertificateStatus.valueOf(dto.getStatus().name()))
        .sent(dto.isSent())
        .sentRecipientName(dto.getSentRecipientName())
        .availableForCitizen(dto.isAvailableForCitizen())
        .certificateId(dto.getCertificateId())
        .additionalInfoText(dto.getAdditionalInfoText())
        .signaturePageIndex(dto.getSignaturePageIndex())
        .signatureTagIndex(dto.getSignatureTagIndex())
        .signedDateFieldId(dto.getSignedDateFieldId())
        .startMcid(dto.getStartMcid())
        .title(dto.getTitle())
        .build();
  }

  private Map<String, CustomPdfField> convertFields(Map<String, CustomPdfFieldDTO> fields) {
    if (fields == null) {
      return Collections.emptyMap();
    }
    return fields.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> convertField(e.getValue())));
  }

  private CustomPdfField convertField(CustomPdfFieldDTO dto) {
    return CustomPdfField.builder().value(dto.getValue()).build();
  }
}
