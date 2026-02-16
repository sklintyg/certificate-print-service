package se.inera.intyg.certificateprintservice.playwright.document;

import static se.inera.intyg.certificateprintservice.playwright.document.Constants.RIGHT_MARGIN_INFO_STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.element.ElementProvider.element;

import javax.swing.text.html.HTML.Tag;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jsoup.nodes.Element;

@Builder
@Getter
@EqualsAndHashCode
public class RightMarginInfo {

  String certificateId;

  private static final String CERTIFICATE_ID_TEXT = "Intygs-ID: %s";

  public Element create() {
    return element(Tag.DIV)
        .attr(STYLE, RIGHT_MARGIN_INFO_STYLE)
        .appendChild(rightMarginInfo());
  }

  private Element rightMarginInfo() {
    final var info = CERTIFICATE_ID_TEXT.formatted(certificateId);
    return element(Tag.P)
        .attr(STYLE, "margin: 0;")
        .text(info);
  }

}
