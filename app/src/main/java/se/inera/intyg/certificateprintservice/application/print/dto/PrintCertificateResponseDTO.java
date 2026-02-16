package se.inera.intyg.certificateprintservice.application.print.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateResponseDTO.PrintCertificateResponseDTOBuilder;

@Value
@Builder
@JsonDeserialize(builder = PrintCertificateResponseDTOBuilder.class)
public class PrintCertificateResponseDTO {

  byte[] pdfData;

  @JsonPOJOBuilder(withPrefix = "")
  public static class PrintCertificateResponseDTOBuilder {

  }
}