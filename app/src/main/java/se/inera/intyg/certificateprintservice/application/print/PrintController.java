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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintResponseDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.service.CustomPrintService;
import se.inera.intyg.certificateprintservice.application.print.general.dto.PrintCertificateRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.general.dto.PrintCertificateResponseDTO;
import se.inera.intyg.certificateprintservice.application.print.general.service.GeneralPrintService;

@RestController
@RequestMapping("api/print")
@RequiredArgsConstructor
public class PrintController {

  private final GeneralPrintService generalPrintService;
  private final CustomPrintService customPrintService;

  @PostMapping("/general")
  PrintCertificateResponseDTO general(@RequestBody PrintCertificateRequestDTO request) {
    return generalPrintService.get(request);
  }

  @PostMapping("/custom")
  CustomPrintResponseDTO custom(@Valid @RequestBody CustomPrintRequestDTO request) {
    return customPrintService.get(request);
  }
}
