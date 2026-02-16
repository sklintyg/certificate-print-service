package se.inera.intyg.certificateprintservice.application.print.converter;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateQuestionDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValueList;
import se.inera.intyg.certificateprintservice.application.print.dto.value.ElementSimplifiedValueText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Question;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueList;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueText;

@ExtendWith(MockitoExtension.class)
class PrintCertificateQuestionConverterTest {

  private static final String ID = "ID_1";
  private static final String NAME = "NAME_1";
  private static final String TEXT = "TEXT_1";

  PrintCertificateQuestionConverter printCertificateQuestionConverter = new PrintCertificateQuestionConverter();

  PrintCertificateQuestionDTO.PrintCertificateQuestionDTOBuilder printCertificateQuestionDTOBuilder =
      PrintCertificateQuestionDTO.builder()
          .value(ElementSimplifiedValueText.builder().build())
          .subquestions(List.of());

  @Test
  void shallConvertId() {
    final var questionDTO = printCertificateQuestionDTOBuilder
        .id(ID)
        .build();

    final var result = printCertificateQuestionConverter.convert(questionDTO);

    assertEquals(ID, result.getId());
  }

  @Test
  void shallConvertName() {
    final var questionDTO = printCertificateQuestionDTOBuilder
        .name(NAME)
        .build();

    final var result = printCertificateQuestionConverter.convert(questionDTO);

    assertEquals(NAME, result.getName());
  }

  @Test
  void shallConvertValueText() {
    final var questionDTO = printCertificateQuestionDTOBuilder
        .value(ElementSimplifiedValueText.builder()
            .text(TEXT)
            .build())
        .build();

    final var result = printCertificateQuestionConverter.convert(questionDTO);

    assertEquals(ElementValueText.builder().text(TEXT).build(), result.getValue());
  }

  @Test
  void shallConvertValueList() {
    final var questionDTO = printCertificateQuestionDTOBuilder
        .value(ElementSimplifiedValueList.builder()
            .list(List.of(TEXT))
            .build())
        .build();

    final var result = printCertificateQuestionConverter.convert(questionDTO);

    assertEquals(ElementValueList.builder().list(List.of(TEXT)).build(), result.getValue());
  }

  @Test
  void shallConvertSubquestions() {
    final var questionDTO = printCertificateQuestionDTOBuilder
        .subquestions(List.of(printCertificateQuestionDTOBuilder.build())).build();

    final var result = printCertificateQuestionConverter.convert(questionDTO);

    assertEquals(List.of(Question.builder()
        .value(ElementValueText.builder().build())
        .subQuestions(List.of()).build()), result.getSubQuestions());
  }

}
