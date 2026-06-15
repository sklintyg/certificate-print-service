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

import javax.swing.text.html.HTML.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Element;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.Category;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryConverter {

  public static Element category(Category category) {
    final var div =
        new Element(Tag.DIV.toString())
            .addClass("box-decoration-clone border border-solid border-black mb-[5mm] pb-[3mm]");

    final var title =
        new Element(Tag.H2.toString())
            .addClass("text-base font-bold uppercase border-b border-black border-solid px-[5mm]")
            .text(category.getName());

    div.appendChild(title);
    category
        .getQuestions()
        .forEach(question -> div.appendChildren(QuestionConverter.question(question, false)));
    return div;
  }
}
