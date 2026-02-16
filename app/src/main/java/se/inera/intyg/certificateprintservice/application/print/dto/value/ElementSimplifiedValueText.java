package se.inera.intyg.certificateprintservice.application.print.dto.value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValueText.ElementSimplifiedValueTextBuilder;

@Value
@Builder
@JsonDeserialize(builder = ElementSimplifiedValueTextBuilder.class)
public class ElementSimplifiedValueText implements ElementSimplifiedValue {

  @Getter(onMethod = @__(@Override))
  ElementSimplifiedType type = ElementSimplifiedType.TEXT;
  String text;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ElementSimplifiedValueTextBuilder {

  }
}