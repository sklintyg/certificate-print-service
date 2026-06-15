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
package se.inera.intyg.certificateprintservice.application.print.custom.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.doReturn;
import static se.inera.intyg.certificateprintservice.application.testdata.TestDataCustomPrintRequest.validRequest;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.certificateprintservice.application.print.custom.converter.CustomPdfRequestConverter;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.CustomPdfGenerator;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdf;

@ExtendWith(MockitoExtension.class)
class CustomPrintServiceTest {

  @Mock CustomPdfGenerator customPdfGenerator;
  @Mock CustomPdfRequestConverter customPdfRequestConverter;
  @InjectMocks CustomPrintService customPrintService;

  @Test
  void shallReturnFilledPdf() {
    final var request = validRequest();
    final var domain = CustomPdf.builder().build();
    final var expectedPdfData = "filled-pdf".getBytes(StandardCharsets.UTF_8);

    doReturn(domain).when(customPdfRequestConverter).convert(request);
    doReturn(expectedPdfData).when(customPdfGenerator).get(domain);

    final var actual = customPrintService.get(request);
    assertArrayEquals(expectedPdfData, actual.getPdfData());
  }
}
