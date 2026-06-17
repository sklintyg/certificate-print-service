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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.AccessibilityMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.AppearanceDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfFieldDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomTextDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.FontStyleEnumDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.OverflowConfigDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.AccessibilityMetadata;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.Appearance;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdf;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfMetadata;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.OverflowConfig;

@Component
public class CustomPdfRequestConverter {

  public CustomPdf convert(CustomPrintRequestDTO request) {
    return new CustomPdf(
        Base64.getDecoder().decode(request.getTemplate()),
        convertMetadata(request.getMetadata()),
        convertFields(request.getFields()));
  }

  private CustomPdfMetadata convertMetadata(CustomPdfMetadataDTO dto) {
    return new CustomPdfMetadata(
        convertCustomTexts(dto.getCustomTexts()),
        dto.getRightMarginText(),
        convertAccessibilityMetadata(dto.getAccessibilityMetadata()),
        dto.isAddDraftWatermark());
  }

  private List<CustomText> convertCustomTexts(List<CustomTextDTO> customTextDTOList) {
    return customTextDTOList.stream().map(this::convertCustomText).toList();
  }

  private CustomText convertCustomText(CustomTextDTO dto) {
    return new CustomText(
        dto.value(),
        dto.x(),
        dto.y(),
        convertAppearance(dto.appearance()),
        dto.pageIndex(),
        dto.tagIndex());
  }

  private Appearance convertAppearance(AppearanceDTO dto) {
    return new Appearance(dto.fontSize(), FontStyleEnumDTO.toFontStyle(dto.style()));
  }

  private AccessibilityMetadata convertAccessibilityMetadata(AccessibilityMetadataDTO dto) {
    return new AccessibilityMetadata(dto.title());
  }

  private Map<String, CustomPdfField> convertFields(Map<String, CustomPdfFieldDTO> fields) {
    if (fields == null) {
      return Collections.emptyMap();
    }
    return fields.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                e -> convertField(e.getValue()),
                (a, b) -> a,
                LinkedHashMap::new));
  }

  private CustomPdfField convertField(CustomPdfFieldDTO dto) {
    return new CustomPdfField(
        dto.getValue(),
        dto.getOffset(),
        dto.getAppearance(),
        dto.getMaxLength(),
        dto.isShouldRemoveLineBreaks(),
        convertOverflowConfig(dto.getOverflow()));
  }

  private OverflowConfig convertOverflowConfig(OverflowConfigDTO dto) {
    if (dto == null) {
      return null;
    }
    return new OverflowConfig(dto.getOverflowFieldId(), dto.getOverflowLabel());
  }
}
