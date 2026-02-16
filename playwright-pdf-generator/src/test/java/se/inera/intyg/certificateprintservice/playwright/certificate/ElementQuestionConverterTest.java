package se.inera.intyg.certificateprintservice.playwright.certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Question;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueText;

class ElementQuestionConverterTest {

  @Test
  void shouldReturnHtmlForQuestionWithoutSubQuestions() {
    final var result = QuestionConverter.question(
        Question.builder()
            .id("ID")
            .name("Name Question")
            .value(ElementValueText.builder()
                .text("Example text for value")
                .build()
            )
            .subQuestions(Collections.emptyList())
            .build()
        , false
    );

    assertEquals(
        "[<h3 class=\"text-sm font-bold pt-[1mm] px-[5mm]\">Name Question</h3>, <p class=\"text-sm italic px-[5mm]\">Example text for value</p>]",
        result.toString()
    );
  }

  @Test
  void shouldReturnHtmlForQuestionWithSubQuestions() {
    final var result = QuestionConverter.question(
        Question.builder()
            .id("ID")
            .name("Name Question")
            .value(ElementValueText.builder()
                .text("Example text for value")
                .build()
            )
            .subQuestions(
                List.of(
                    Question.builder()
                        .id("ID 2")
                        .name("Name Question 2")
                        .value(ElementValueText.builder()
                            .text("Example text for value 2")
                            .build()
                        )
                        .subQuestions(Collections.emptyList())
                        .build()
                )
            )
            .build()
        , true
    );

    assertEquals(
        "[<h3 class=\"text-sm font-bold pt-[1mm] px-[5mm] text-neutral-600\">Name Question</h3>, <p class=\"text-sm italic px-[5mm]\">Example text for value</p>, <h3 class=\"text-sm font-bold pt-[1mm] px-[5mm] text-neutral-600\">Name Question 2</h3>, <p class=\"text-sm italic px-[5mm]\">Example text for value 2</p>]",
        result.toString()
    );
  }

  @Test
  void shouldReturnHtmlForQuestionAndQuestionNameAsEmptyString() {
    final var result = QuestionConverter.question(
        Question.builder()
            .id("ID")
            .value(ElementValueText.builder()
                .text("Example text for value")
                .build()
            )
            .subQuestions(Collections.emptyList())
            .build()
        , false
    );

    assertEquals(
        "[<h3 class=\"text-sm font-bold pt-[1mm] px-[5mm]\"></h3>, <p class=\"text-sm italic px-[5mm]\">Example text for value</p>]",
        result.toString()
    );
  }
}