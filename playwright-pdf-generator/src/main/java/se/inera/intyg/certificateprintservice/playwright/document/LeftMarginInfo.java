package se.inera.intyg.certificateprintservice.playwright.document;

import static se.inera.intyg.certificateprintservice.playwright.document.Constants.LEFT_MARGIN_INFO_STYLE;
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
public class LeftMarginInfo {

  String certificateType;
  String recipientName;
  String certificateVersion;
  String recipientId;
  String leftMarginText;

  private static final String LEFT_MARGIN_TEXT = "%s %s - Fastställd av %s";

  public Element create() {
    return element(Tag.DIV)
        .attr(STYLE, LEFT_MARGIN_INFO_STYLE)
        .appendChild(leftMarginInfo());
  }

  private Element leftMarginInfo() {

    if (isLeftMarginTextPresent()) {
      final var info = leftMarginText.formatted(recipientId, certificateType, certificateVersion,
          recipientName);
      return element(Tag.P)
          .attr(STYLE, "margin: 0;")
          .text(info);
    }

    final var info = LEFT_MARGIN_TEXT.formatted(certificateType, certificateVersion,
        recipientName);
    return element(Tag.P)
        .attr(STYLE, "margin: 0;")
        .text(info);
  }

  private boolean isLeftMarginTextPresent() {
    return leftMarginText != null && !leftMarginText.isBlank();
  }

}
