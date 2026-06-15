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
package se.inera.intyg.certificateprintservice.application.print.general.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.certificateprintservice.application.print.general.dto.PrintCertificateCategoryDTO;
import se.inera.intyg.certificateprintservice.application.print.general.dto.PrintCertificateMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.general.dto.PrintCertificateRequestDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.Category;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.Metadata;

@ExtendWith(MockitoExtension.class)
class PrintCertificateConverterTest {

  private static final String ID_1 = "ID_1";
  private static final String ID_2 = "ID_2";
  @Mock private PrintCertificateCategoryConverter categoryConverter;
  @Mock private PrintCertificateMetadataConverter metadataConverter;
  @InjectMocks private PrintCertificateRequestConverter printCertificateRequestConverter;

  @Test
  void shallConvertCategories() {
    final var certificateCategory1 = Category.builder().id(ID_1).build();
    final var certificateCategory2 = Category.builder().id(ID_2).build();
    final var expectedCategories = List.of(certificateCategory1, certificateCategory2);

    final var certificateDTOCategory1 = PrintCertificateCategoryDTO.builder().id(ID_1).build();
    final var certificateDTOCategory2 = PrintCertificateCategoryDTO.builder().id(ID_2).build();
    final var request =
        PrintCertificateRequestDTO.builder()
            .categories(List.of(certificateDTOCategory1, certificateDTOCategory2))
            .metadata(PrintCertificateMetadataDTO.builder().build())
            .build();

    doReturn(certificateCategory1).when(categoryConverter).convert(certificateDTOCategory1);
    doReturn(certificateCategory2).when(categoryConverter).convert(certificateDTOCategory2);

    final var actualCategories = printCertificateRequestConverter.convert(request).getCategories();
    assertEquals(expectedCategories, actualCategories);
  }

  @Test
  void shallConvertMetadata() {
    final var expectedMetadata = Metadata.builder().build();
    final var metadataDTO = PrintCertificateMetadataDTO.builder().build();
    final var certificateDTOCategory1 = PrintCertificateCategoryDTO.builder().id(ID_1).build();
    final var certificateDTOCategory2 = PrintCertificateCategoryDTO.builder().id(ID_2).build();

    final var request =
        PrintCertificateRequestDTO.builder()
            .categories(List.of(certificateDTOCategory1, certificateDTOCategory2))
            .metadata(metadataDTO)
            .build();

    doReturn(expectedMetadata).when(metadataConverter).convert(metadataDTO);

    final var actualMetadata = printCertificateRequestConverter.convert(request).getMetadata();
    assertEquals(expectedMetadata, actualMetadata);
  }
}
