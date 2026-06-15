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

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.html.HTML.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Element;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.Question;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionConverter {

  public static List<Element> question(Question question, boolean isSubQuestion) {
    final var name =
        new Element(Tag.H3.toString())
            .addClass("text-sm font-bold")
            .text(question.getName() == null ? "" : question.getName());

    if (isSubQuestion) {
      name.addClass("pt-[1mm] px-[5mm] text-neutral-600");
    } else {
      name.addClass("pt-[1mm] px-[5mm]");
    }

    final var list = new ArrayList<Element>();
    list.add(name);
    list.add(ElementValueConverter.html(question.getValue()));

    question.getSubQuestions().forEach(subQuestion -> list.addAll(question(subQuestion, true)));
    return list;
  }
}
