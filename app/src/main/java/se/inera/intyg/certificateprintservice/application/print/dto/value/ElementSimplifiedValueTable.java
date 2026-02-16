package se.inera.intyg.certificateprintservice.application.print.dto.value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValueTable.ElementSimplifiedValueTableBuilder;

@Value
@Builder
@JsonDeserialize(builder = ElementSimplifiedValueTableBuilder.class)
public class ElementSimplifiedValueTable implements ElementSimplifiedValue {

  @Getter(onMethod = @__(@Override))
  ElementSimplifiedType type = ElementSimplifiedType.TABLE;
  List<String> headings;
  List<List<String>> values;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ElementSimplifiedValueTableBuilder {

  }
}
