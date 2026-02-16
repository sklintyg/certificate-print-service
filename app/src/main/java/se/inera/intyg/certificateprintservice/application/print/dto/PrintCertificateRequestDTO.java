package se.inera.intyg.certificateprintservice.application.print.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateRequestDTO.PrintCertificateRequestDTOBuilder;

@Value
@Builder
@JsonDeserialize(builder = PrintCertificateRequestDTOBuilder.class)
public class PrintCertificateRequestDTO {

  List<PrintCertificateCategoryDTO> categories;
  PrintCertificateMetadataDTO metadata;

  @JsonPOJOBuilder(withPrefix = "")
  public static class PrintCertificateRequestDTOBuilder {

  }
}