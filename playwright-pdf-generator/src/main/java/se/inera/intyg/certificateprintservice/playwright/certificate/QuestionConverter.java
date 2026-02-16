package se.inera.intyg.certificateprintservice.playwright.certificate;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.html.HTML.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Element;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Question;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionConverter {

  public static List<Element> question(Question question, boolean isSubQuestion) {
    final var name = new Element(Tag.H3.toString())
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

    question.getSubQuestions()
        .forEach(subQuestion -> list.addAll(question(subQuestion, true)));
    return list;
  }
}
