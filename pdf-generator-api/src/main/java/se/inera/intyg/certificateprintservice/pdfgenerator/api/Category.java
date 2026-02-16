package se.inera.intyg.certificateprintservice.pdfgenerator.api;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Category {

  String id;
  String name;
  List<Question> questions;
}