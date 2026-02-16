package se.inera.intyg.certificateprintservice.application.print.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateQuestionDTO.PrintCertificateQuestionDTOBuilder;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValue;

@Value
@Builder
@JsonDeserialize(builder = PrintCertificateQuestionDTOBuilder.class)
public class PrintCertificateQuestionDTO {

  String id;
  String name;
  ElementSimplifiedValue value;
  List<PrintCertificateQuestionDTO> subquestions;

  @JsonPOJOBuilder(withPrefix = "")
  public static class PrintCertificateQuestionDTOBuilder {

  }
}