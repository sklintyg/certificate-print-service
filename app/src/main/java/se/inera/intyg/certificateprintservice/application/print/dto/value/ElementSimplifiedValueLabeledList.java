package se.inera.intyg.certificateprintservice.application.print.dto.value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValueLabeledList.ElementSimplifiedValueLabeledListBuilder;

@Value
@Builder
@JsonDeserialize(builder = ElementSimplifiedValueLabeledListBuilder.class)
public class ElementSimplifiedValueLabeledList implements ElementSimplifiedValue {

  @Getter(onMethod = @__(@Override))
  ElementSimplifiedType type = ElementSimplifiedType.LABELED_LIST;
  List<ElementSimplifiedValueLabeledText> list;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ElementSimplifiedValueLabeledListBuilder {

  }

}
