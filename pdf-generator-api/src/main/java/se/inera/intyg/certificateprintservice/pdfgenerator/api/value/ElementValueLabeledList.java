package se.inera.intyg.certificateprintservice.pdfgenerator.api.value;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ElementValueLabeledList implements ElementValue {

  List<ElementValueLabeledText> list;
}