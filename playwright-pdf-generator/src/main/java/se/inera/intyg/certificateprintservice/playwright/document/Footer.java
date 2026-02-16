package se.inera.intyg.certificateprintservice.playwright.document;

import static se.inera.intyg.certificateprintservice.playwright.document.Constants.FOOTER_STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.element.ElementProvider.element;

import java.util.List;
import javax.swing.text.html.HTML.Tag;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jsoup.nodes.Element;
import se.inera.intyg.certificateprintservice.playwright.element.FooterElementFactory;

@Builder
@Getter
@EqualsAndHashCode
public class Footer {

  String applicationOrigin;

  public Element create() {
    return element(Tag.DIV)
        .appendChild(footer());
  }

  public Element footer() {
    return element(Tag.DIV)
        .attr(STYLE, FOOTER_STYLE)
        .appendChildren(List.of(
            FooterElementFactory.info(applicationOrigin),
            FooterElementFactory.pageNumber()));
  }

}
