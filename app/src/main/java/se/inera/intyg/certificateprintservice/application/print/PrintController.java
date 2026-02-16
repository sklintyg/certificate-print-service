package se.inera.intyg.certificateprintservice.application.print;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateResponseDTO;

@RestController
@RequestMapping("api/print")
@RequiredArgsConstructor
public class PrintController {

  private final GeneratePrintService printService;

  @PostMapping()
  PrintCertificateResponseDTO get(@RequestBody PrintCertificateRequestDTO request) {
    return printService.get(request);
  }
}