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
    final var result =
        CategoryConverter.category(
            Category.builder()
                .id("ID")
                .name("Name Category")
                .questions(
                    List.of(
                        Question.builder()
                            .id("ID")
                            .name("Name Question")
                            .value(
                                ElementValueText.builder().text("Example text for value").build())
                            .subQuestions(Collections.emptyList())
                            .build()))
                .build());

    assertEquals(
        """
            <div class="box-decoration-clone border border-solid border-black mb-[5mm] pb-[3mm]">
             <h2 class="text-base font-bold uppercase border-b border-black border-solid px-[5mm]">Name Category</h2>
             <h3 class="text-sm font-bold pt-[1mm] px-[5mm]">Name Question</h3>
             <p class="text-sm italic px-[5mm]">Example text for value</p>
            </div>""",
        result.toString());
  }
}
