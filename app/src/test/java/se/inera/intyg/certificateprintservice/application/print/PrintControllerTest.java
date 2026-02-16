package se.inera.intyg.certificateprintservice.application.print;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateResponseDTO;

@ExtendWith(MockitoExtension.class)
class PrintControllerTest {

  @Mock
  GeneratePrintService generatePrintService;
  @InjectMocks
  PrintController printController;

  @Test
  void shallReturnPrintCertificateResponse() {
    final var request = PrintCertificateRequestDTO.builder().build();
    final var expectedResponse = PrintCertificateResponseDTO.builder()
        .build();

    doReturn(expectedResponse).when(generatePrintService).get(request);

    final var actualResponse = printController.get(request);
    assertEquals(expectedResponse, actualResponse);
  }
}