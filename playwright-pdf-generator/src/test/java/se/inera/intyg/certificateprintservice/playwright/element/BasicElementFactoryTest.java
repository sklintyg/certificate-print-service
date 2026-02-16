package se.inera.intyg.certificateprintservice.playwright.element;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueLabeledList;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueLabeledText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueTable;

class BasicElementFactoryTest {

  @Test
  void shouldReturnTableHtmlIfOneHeading() {
    final var result = BasicElementFactory.table(
        ElementValueTable.builder()
            .headings(List.of("H1"))
            .values(List.of(List.of("D1")))
            .build()
    );

    assertEquals("""
            <table class="text-sm mx-[5mm]">
             <tr class="border-b border-black border-solid">
              <th class="font-bold pr-[10mm]">H1</th>
             </tr>
             <tr>
              <td>D1</td>
             </tr>
            </table>""",
        result.toString()
    );
  }

  @Test
  void shouldReturnParagraphWithContent() {
    assertEquals("<p class=\"text-sm italic px-[5mm]\">answer</p>",
        BasicElementFactory.p("answer").toString());
  }

  @Test
  void shouldReturnLabeledList() {
    final var result = BasicElementFactory.labeledList(
        ElementValueLabeledList.builder()
            .list(List.of(
                ElementValueLabeledText
                    .builder()
                    .label("L1")
                    .text("T1")
                    .build(),
                ElementValueLabeledText
                    .builder()
                    .label("L2")
                    .text("T2")
                    .build()
            ))
            .build()
    );

    assertEquals("""
        <div class="mb-2">
         <div class="mb-2">
          <p class="text-sm font-bold pt-[1mm] px-[5mm] text-neutral-600">L1</p>
          <p class="text-sm italic px-[5mm]">T1</p>
         </div>
         <div class="mb-2">
          <p class="text-sm font-bold pt-[1mm] px-[5mm] text-neutral-600">L2</p>
          <p class="text-sm italic px-[5mm]">T2</p>
         </div>
        </div>""", result.toString());
  }
}