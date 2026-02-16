package se.inera.intyg.certificateprintservice.playwright.certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Category;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Question;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueText;

class ElementCategoryConverterTest {

  @Test
  void shouldReturnHtmlForCategory() {
    final var result = CategoryConverter.category(
        Category.builder()
            .id("ID")
            .name("Name Category")
            .questions(List.of(
                Question.builder()
                    .id("ID")
                    .name("Name Question")
                    .value(ElementValueText.builder()
                        .text("Example text for value")
                        .build()
                    )
                    .subQuestions(Collections.emptyList())
                    .build()
            ))
            .build()
    );

    assertEquals(
        """
            <div class="box-decoration-clone border border-solid border-black mb-[5mm] pb-[3mm]">
             <h2 class="text-base font-bold uppercase border-b border-black border-solid px-[5mm]">Name Category</h2>
             <h3 class="text-sm font-bold pt-[1mm] px-[5mm]">Name Question</h3>
             <p class="text-sm italic px-[5mm]">Example text for value</p>
            </div>""",
        result.toString()
    );
  }
}