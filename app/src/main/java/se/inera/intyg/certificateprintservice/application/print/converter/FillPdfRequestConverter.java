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
package se.inera.intyg.certificateprintservice.application.print.converter;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.FillPdfRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.PdfFieldFillOptionsDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.PdfMetadataOptionsDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.fill.CertificateStatus;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.fill.FillPdf;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.fill.FillPdfMetadata;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.fill.PdfFieldFillOption;

@Component
public class FillPdfRequestConverter {

  public FillPdf convert(FillPdfRequestDTO request) {
    return FillPdf.builder()
        .template(Base64.getDecoder().decode(request.getTemplate()))
        .metadata(convertMetadata(request.getMetadata()))
        .fields(convertFields(request.getFields()))
        .build();
  }

  private FillPdfMetadata convertMetadata(PdfMetadataOptionsDTO dto) {
    return FillPdfMetadata.builder()
        .status(CertificateStatus.valueOf(dto.getStatus().name()))
        .isSent(dto.isSent())
        .sentRecipientName(dto.getSentRecipientName())
        .availableForCitizen(dto.isAvailableForCitizen())
        .certificateId(dto.getCertificateId())
        .additionalInfoText(dto.getAdditionalInfoText())
        .addPageNumbers(dto.isAddPageNumbers())
        .signaturePageIndex(dto.getSignaturePageIndex())
        .signatureTagIndex(dto.getSignatureTagIndex())
        .signedDateFieldId(dto.getSignedDateFieldId())
        .startMcid(dto.getStartMcid())
        .untaggedWatermarks(
            dto.getUntaggedWatermarks() != null
                ? dto.getUntaggedWatermarks()
                : Collections.emptyList())
        .build();
  }

  private Map<String, PdfFieldFillOption> convertFields(
      Map<String, PdfFieldFillOptionsDTO> fields) {
    if (fields == null) {
      return Collections.emptyMap();
    }
    return fields.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> convertField(e.getValue())));
  }

  private PdfFieldFillOption convertField(PdfFieldFillOptionsDTO dto) {
    return PdfFieldFillOption.builder().value(dto.getValue()).build();
  }
}
