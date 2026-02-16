package se.inera.intyg.certificateprintservice.application.print.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateCategoryDTO.PrintCertificateCategoryDTOBuilder;

@Value
@Builder
@JsonDeserialize(builder = PrintCertificateCategoryDTOBuilder.class)
public class PrintCertificateCategoryDTO {

  String id;
  String name;
  List<PrintCertificateQuestionDTO> questions;

  @JsonPOJOBuilder(withPrefix = "")
  public static class PrintCertificateCategoryDTOBuilder {

  }
}