package se.inera.intyg.certificateprintservice.application.print.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateCategoryDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateQuestionDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Question;

@ExtendWith(MockitoExtension.class)
class PrintCertificateCategoryConverterTest {

  private static final String ID_1 = "ID_1";
  private static final String QUESTION_1 = "question_1";
  private static final String QUESTION_2 = "question_2";
  @Mock
  PrintCertificateQuestionConverter printCertificateQuestionConverter;
  @InjectMocks
  PrintCertificateCategoryConverter printCertificateCategoryConverter;

  @Test
  void shallConvertId() {
    var category = PrintCertificateCategoryDTO.builder()
        .id(ID_1)
        .questions(List.of())
        .build();

    final var result = printCertificateCategoryConverter.convert(category);
    assertEquals(ID_1, result.getId());
  }

  @Test
  void shallConvertName() {
    var category = PrintCertificateCategoryDTO.builder()
        .name("name")
        .questions(List.of())
        .build();

    final var result = printCertificateCategoryConverter.convert(category);
    assertEquals("name", result.getName());
  }

  @Test
  void shallConvertQuestions() {

    final var q1 = Question.builder()
        .id(QUESTION_1)
        .build();
    final var q2 = Question.builder()
        .id(QUESTION_2)
        .build();

    var expected = List.of(q1,
        q2);

    final var q1DTO = PrintCertificateQuestionDTO.builder()
        .id(QUESTION_1)
        .build();

    final var q2DTO = PrintCertificateQuestionDTO.builder()
        .id(QUESTION_2)
        .build();

    var category = PrintCertificateCategoryDTO.builder()
        .questions(List.of(q1DTO,
            q2DTO))
        .build();
    doReturn(q1).when(printCertificateQuestionConverter).convert(q1DTO);
    doReturn(q2).when(printCertificateQuestionConverter).convert(q2DTO);

    final var result = printCertificateCategoryConverter.convert(category);
    assertEquals(expected, result.getQuestions());
  }


}