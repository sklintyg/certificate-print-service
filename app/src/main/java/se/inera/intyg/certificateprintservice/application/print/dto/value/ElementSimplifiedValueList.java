package se.inera.intyg.certificateprintservice.application.print.dto.value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValueList.ElementSimplifiedValueListBuilder;

@Value
@Builder
@JsonDeserialize(builder = ElementSimplifiedValueListBuilder.class)
public class ElementSimplifiedValueList implements ElementSimplifiedValue {

  @Getter(onMethod = @__(@Override))
  ElementSimplifiedType type = ElementSimplifiedType.LIST;
  List<String> list;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ElementSimplifiedValueListBuilder {

  }
}