package se.inera.intyg.certificateprintservice.playwright.certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueLabeledList;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueLabeledText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueList;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueTable;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueText;
import se.inera.intyg.certificateprintservice.playwright.element.BasicElementFactory;

class ElementValueConverterTest {

  @Test
  void shouldReturnValueForText() {
    final var result = ElementValueConverter.html(
        ElementValueText.builder()
            .text("Example text for value")
            .build()
    );

    assertEquals(
        "<p class=\"text-sm italic px-[5mm]\">Example text for value</p>",
        result.toString()
    );
  }

  @Test
  void shouldReturnValueForList() {
    final var result = ElementValueConverter.html(
        ElementValueList.builder()
            .list(List.of("Example text for value", "Example 2"))
            .build()
    );

    assertEquals(
        "<p class=\"text-sm italic px-[5mm]\">Example text for value, Example 2</p>",
        result.toString()
    );
  }

  @Test
  void shouldReturnValueTable() {
    final var result = BasicElementFactory.table(
        ElementValueTable.builder()
            .headings(List.of("H1", "H2"))
            .values(List.of(List.of("D1", "D2"), List.of("D11", "D21")))
            .build()
    );

    assertEquals("""
            <table class="text-sm mx-[5mm]">
             <tr class="border-b border-black border-solid">
              <th class="font-bold pr-[10mm]">H1</th>
              <th class="font-bold pr-[10mm]">H2</th>
             </tr>
             <tr>
              <td>D1</td>
              <td>D2</td>
             </tr>
             <tr>
              <td>D11</td>
              <td>D21</td>
             </tr>
            </table>"""
        , result.toString());
  }

  @Test
  void shouldReturnValueForLabeledList() {
    final var result = ElementValueConverter.html(
        ElementValueLabeledList.builder()
            .list(List.of(
                ElementValueLabeledText.builder()
                    .label("Label 1")
                    .text("Example text for value")
                    .build(),
                ElementValueLabeledText.builder()
                    .label("Label 2")
                    .text("Example 2")
                    .build()
            ))
            .build()
    );

    assertEquals(
        """
            <div class="mb-2">
             <div class="mb-2">
              <p class="text-sm font-bold pt-[1mm] px-[5mm] text-neutral-600">Label 1</p>
              <p class="text-sm italic px-[5mm]">Example text for value</p>
             </div>
             <div class="mb-2">
              <p class="text-sm font-bold pt-[1mm] px-[5mm] text-neutral-600">Label 2</p>
              <p class="text-sm italic px-[5mm]">Example 2</p>
             </div>
            </div>""",
        result.toString()
    );
  }
}