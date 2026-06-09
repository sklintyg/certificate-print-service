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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.certificateprintservice.application.print.converter.general.PrintCertificateRequestConverter;
import se.inera.intyg.certificateprintservice.application.print.dto.general.PrintCertificateCategoryDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.general.PrintCertificateMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.general.PrintCertificateRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.general.PrintCertificateResponseDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.PrintCertificateGenerator;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.Certificate;

@ExtendWith(MockitoExtension.class)
class GeneralPrintServiceTest {

  private static final PrintCertificateRequestDTO REQUEST =
      PrintCertificateRequestDTO.builder()
          .categories(List.of(PrintCertificateCategoryDTO.builder().build()))
          .metadata(PrintCertificateMetadataDTO.builder().build())
          .build();
  @Mock PrintCertificateGenerator printCertificateGenerator;
  @Mock PrintCertificateRequestConverter printCertificateRequestConverter;
  @InjectMocks GeneralPrintService generalPrintService;

  @Test
  void shallThrowIfRequestIsNull() {
    assertThrows(IllegalArgumentException.class, () -> generalPrintService.get(null));
  }

  @Test
  void shallThrowIfMetadataIsNull() {
    final var request =
        PrintCertificateRequestDTO.builder()
            .categories(List.of(PrintCertificateCategoryDTO.builder().build()))
            .build();

    final var illegalArgumentException =
        assertThrows(IllegalArgumentException.class, () -> generalPrintService.get(request));
    assertEquals(
        "Invalid request - Missing required parameter metadata",
        illegalArgumentException.getMessage());
  }

  @Test
  void shallReturnPrintCertificateResponse() {
    final var expectedPdfData = "expectedResult".getBytes(StandardCharsets.UTF_8);
    final var expectedResult =
        PrintCertificateResponseDTO.builder().pdfData(expectedPdfData).build();

    final var printCertificateRequest = Certificate.builder().build();

    doReturn(printCertificateRequest).when(printCertificateRequestConverter).convert(REQUEST);
    doReturn(expectedPdfData).when(printCertificateGenerator).generate(printCertificateRequest);

    final var actualResult = generalPrintService.get(REQUEST);
    assertEquals(expectedResult, actualResult);
  }
}
