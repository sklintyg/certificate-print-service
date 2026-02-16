package se.inera.intyg.certificateprintservice.application.print;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.application.print.converter.PrintCertificateRequestConverter;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateResponseDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.PrintCertificateGenerator;

@Service
@RequiredArgsConstructor
public class GeneratePrintService {

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