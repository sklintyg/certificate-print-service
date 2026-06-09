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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateResponseDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.FillPdfRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.fill.FillPdfResponseDTO;

@RestController
@RequestMapping("api/print")
@RequiredArgsConstructor
public class PrintController {

  private final GeneratePrintService printService;
  private final FillPdfService fillPdfService;

  @PostMapping()
  PrintCertificateResponseDTO get(@RequestBody PrintCertificateRequestDTO request) {
    return printService.get(request);
  }

  @PostMapping("/fill")
  FillPdfResponseDTO fill(@RequestBody FillPdfRequestDTO request) {
    return fillPdfService.fill(request);
  }
}
