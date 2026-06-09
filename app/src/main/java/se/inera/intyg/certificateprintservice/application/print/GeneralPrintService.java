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
import se.inera.intyg.certificateprintservice.application.print.converter.general.PrintCertificateRequestConverter;
import se.inera.intyg.certificateprintservice.application.print.dto.general.PrintCertificateRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.general.PrintCertificateResponseDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.PrintCertificateGenerator;

@Service
@RequiredArgsConstructor
public class GeneralPrintService {

  private final PrintCertificateGenerator printCertificateGenerator;
  private final PrintCertificateRequestConverter printCertificateRequestConverter;

  public PrintCertificateResponseDTO get(PrintCertificateRequestDTO request) {
    validateRequest(request);
    final var certificate = printCertificateRequestConverter.convert(request);
    return PrintCertificateResponseDTO.builder()
        .pdfData(printCertificateGenerator.generate(certificate))
        .build();
  }

  private void validateRequest(PrintCertificateRequestDTO request) {
    if (request == null) {
      throw new IllegalArgumentException("Invalid request - request is null");
    }

    if (request.getMetadata() == null) {
      throw new IllegalArgumentException("Invalid request - Missing required parameter metadata");
    }
  }
}
