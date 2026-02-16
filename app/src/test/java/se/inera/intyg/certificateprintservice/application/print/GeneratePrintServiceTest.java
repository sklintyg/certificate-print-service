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
import se.inera.intyg.certificateprintservice.application.print.converter.PrintCertificateRequestConverter;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateCategoryDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateMetadataDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateResponseDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.PrintCertificateGenerator;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Certificate;

@ExtendWith(MockitoExtension.class)
class GeneratePrintServiceTest {

  private static final PrintCertificateRequestDTO REQUEST = PrintCertificateRequestDTO.builder()
      .categories(List.of(PrintCertificateCategoryDTO.builder().build()))
      .metadata(PrintCertificateMetadataDTO.builder().build())
      .build();
  @Mock
  PrintCertificateGenerator printCertificateGenerator;
  @Mock
  PrintCertificateRequestConverter printCertificateRequestConverter;
  @InjectMocks
  GeneratePrintService generatePrintService;

  @Test
  void shallThrowIfRequestIsNull() {
    assertThrows(IllegalArgumentException.class, () -> generatePrintService.get(null));
  }

  @Test
  void shallThrowIfMetadataIsNull() {
    final var request = PrintCertificateRequestDTO.builder()
        .categories(List.of(PrintCertificateCategoryDTO.builder().build()))
        .build();

    final var illegalArgumentException = assertThrows(IllegalArgumentException.class,
        () -> generatePrintService.get(request));
    assertEquals("Invalid request - Missing required parameter metadata",
        illegalArgumentException.getMessage());
  }

  @Test
  void shallReturnPrintCertificateResponse() {
    final var expectedPdfData = "expectedResult".getBytes(StandardCharsets.UTF_8);
    final var expectedResult = PrintCertificateResponseDTO.builder()
        .pdfData(expectedPdfData)
        .build();

    final var printCertificateRequest = Certificate.builder().build();

    doReturn(printCertificateRequest).when(printCertificateRequestConverter).convert(REQUEST);
    doReturn(expectedPdfData).when(printCertificateGenerator).generate(printCertificateRequest);

    final var actualResult = generatePrintService.get(REQUEST);
    assertEquals(expectedResult, actualResult);
  }
}