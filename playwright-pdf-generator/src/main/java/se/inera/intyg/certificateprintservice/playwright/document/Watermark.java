package se.inera.intyg.certificateprintservice.playwright.document;

import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.WATERMARK_STYLE;
import static se.inera.intyg.certificateprintservice.playwright.element.ElementProvider.element;

import javax.swing.text.html.HTML.Tag;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jsoup.nodes.Element;

@Builder
@Getter
@EqualsAndHashCode
public class Watermark {

  @Builder.Default
  String watermarkText = "UTKAST";

  public Element create() {
    return element(Tag.DIV)
        .attr(STYLE, WATERMARK_STYLE)
        .appendChild(element(Tag.P).text(watermarkText));
  }

}
