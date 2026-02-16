package se.inera.intyg.certificateprintservice.application.print.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateQuestionDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValueLabeledList;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValueLabeledText;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValueList;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValueTable;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValueText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Question;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValue;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueLabeledList;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueLabeledText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueList;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueTable;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueText;

@Component
public class PrintCertificateQuestionConverter {

  public Question convert(PrintCertificateQuestionDTO question) {
    return Question.builder()
        .id(question.getId())
        .name(question.getName())
        .value(getElementValue(question))
        .subQuestions(question.getSubquestions().stream()
            .map(this::convert)
            .toList())
        .build();
  }

  private static ElementValue getElementValue(PrintCertificateQuestionDTO question) {
    if (question.getValue() instanceof ElementSimplifiedValueText text) {
      return ElementValueText.builder()
          .text(text.getText())
          .build();
    } else if (question.getValue() instanceof ElementSimplifiedValueList list) {
      return ElementValueList.builder()
          .list(list.getList())
          .build();
    } else if (question.getValue() instanceof ElementSimplifiedValueTable table) {
      return ElementValueTable.builder()
          .headings(table.getHeadings())
          .values(table.getValues())
          .build();
    } else if (question.getValue() instanceof ElementSimplifiedValueLabeledList labeledList) {
      return ElementValueLabeledList.builder()
          .list(labeledList.getList().stream()
              .map(labeledText -> ElementValueLabeledText.builder()
                  .label(labeledText.getLabel())
                  .text(labeledText.getText())
                  .build()
              )
              .toList()
          )
          .build();
    } else if (question.getValue() instanceof ElementSimplifiedValueLabeledText text) {
      return ElementValueLabeledText.builder()
          .label(text.getLabel())
          .text(text.getText())
          .build();
    }

    throw new IllegalArgumentException("Illegal value type");
  }
}
