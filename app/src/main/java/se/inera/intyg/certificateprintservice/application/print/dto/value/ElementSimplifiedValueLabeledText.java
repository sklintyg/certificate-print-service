package se.inera.intyg.certificateprintservice.application.print.dto.value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValueLabeledText.ElementSimplifiedValueLabeledTextBuilder;

@Value
@Builder
@JsonDeserialize(builder = ElementSimplifiedValueLabeledTextBuilder.class)
public class ElementSimplifiedValueLabeledText implements ElementSimplifiedValue {

  @Getter(onMethod = @__(@Override))
  ElementSimplifiedType type = ElementSimplifiedType.LABELED_TEXT;
  String label;
  String text;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ElementSimplifiedValueLabeledTextBuilder {

  }

}
