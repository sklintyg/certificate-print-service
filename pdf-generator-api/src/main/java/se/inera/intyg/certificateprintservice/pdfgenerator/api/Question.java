package se.inera.intyg.certificateprintservice.pdfgenerator.api;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValue;

@Value
@Builder
public class Question {

  String id;
  String name;
  ElementValue value;
  List<Question> subQuestions;
}