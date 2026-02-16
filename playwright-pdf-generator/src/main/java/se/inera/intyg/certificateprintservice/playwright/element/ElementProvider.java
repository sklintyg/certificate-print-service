package se.inera.intyg.certificateprintservice.playwright.element;

import javax.swing.text.html.HTML.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Element;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElementProvider {

  public static Element element(Tag tag) {
    return new Element(tag.toString());
  }

}
