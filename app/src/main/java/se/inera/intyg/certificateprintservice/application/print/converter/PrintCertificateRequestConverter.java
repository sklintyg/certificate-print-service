package se.inera.intyg.certificateprintservice.application.print.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateRequestDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Certificate;

@Component
@RequiredArgsConstructor
public class PrintCertificateRequestConverter {

  private final PrintCertificateCategoryConverter categoryConverter;
  private final PrintCertificateMetadataConverter metadataConverter;

  public Certificate convert(PrintCertificateRequestDTO request) {
    return Certificate.builder()
        .categories(
            request.getCategories().stream()
                .map(categoryConverter::convert)
                .toList()
        )
        .metadata(metadataConverter.convert(request.getMetadata()))
        .build();
  }
}