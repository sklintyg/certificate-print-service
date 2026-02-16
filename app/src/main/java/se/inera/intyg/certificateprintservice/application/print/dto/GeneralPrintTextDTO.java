package se.inera.intyg.certificateprintservice.application.print.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.certificateprintservice.application.print.dto.GeneralPrintTextDTO.GeneralPrintTextDTOBuilder;

@Value
@Builder
@JsonDeserialize(builder = GeneralPrintTextDTOBuilder.class)
public class GeneralPrintTextDTO {

  String leftMarginInfoText;
  String draftAlertInfoText;

  @JsonPOJOBuilder(withPrefix = "")
  public static class GeneralPrintTextDTOBuilder {

  }
}
