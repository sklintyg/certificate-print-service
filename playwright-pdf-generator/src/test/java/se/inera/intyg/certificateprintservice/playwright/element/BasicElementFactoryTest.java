/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    final var result =
        BasicElementFactory.table(
            ElementValueTable.builder()
                .headings(List.of("H1"))
                .values(List.of(List.of("D1")))
                .build());

    assertEquals(
        """
            <table class="text-sm mx-[5mm]">
             <tr class="border-b border-black border-solid">
              <th class="font-bold pr-[10mm]">H1</th>
             </tr>
             <tr>
              <td>D1</td>
             </tr>
            </table>""",
        result.toString());
  }

  @Test
  void shouldReturnParagraphWithContent() {
    assertEquals(
        "<p class=\"text-sm italic px-[5mm]\">answer</p>",
        BasicElementFactory.p("answer").toString());
  }

  @Test
  void shouldReturnLabeledList() {
    final var result =
        BasicElementFactory.labeledList(
            ElementValueLabeledList.builder()
                .list(
                    List.of(
                        ElementValueLabeledText.builder().label("L1").text("T1").build(),
                        ElementValueLabeledText.builder().label("L2").text("T2").build()))
                .build());

    assertEquals(
        """
        <div class="mb-2">
         <div class="mb-2">
          <p class="text-sm font-bold pt-[1mm] px-[5mm] text-neutral-600">L1</p>
          <p class="text-sm italic px-[5mm]">T1</p>
         </div>
         <div class="mb-2">
          <p class="text-sm font-bold pt-[1mm] px-[5mm] text-neutral-600">L2</p>
          <p class="text-sm italic px-[5mm]">T2</p>
         </div>
        </div>""",
        result.toString());
  }
}
