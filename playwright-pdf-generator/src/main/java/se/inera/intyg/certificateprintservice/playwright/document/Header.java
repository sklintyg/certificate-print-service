package se.inera.intyg.certificateprintservice.playwright.document;

import static se.inera.intyg.certificateprintservice.playwright.document.Constants.HEADER;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.HEADER_STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TITLE;
import static se.inera.intyg.certificateprintservice.playwright.element.ElementProvider.element;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.html.HTML.Tag;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jsoup.nodes.Element;
import se.inera.intyg.certificateprintservice.playwright.element.HeaderElementFactory;

@Builder
@Value
@EqualsAndHashCode
public class Header {

  String certificateName;
  String certificateType;
  String certificateVersion;
  String personId;
  String recipientName;
  LeftMarginInfo leftMarginInfo;
  RightMarginInfo rightMarginInfo;
  Watermark watermark;
  byte[] recipientLogo;
  boolean isDraft;
  boolean isSent;
  boolean isCanSendElectronically;
  String draftAlertInfoText;

  public Element create() {
    final var headerElements = new ArrayList<>(List.of(header(), leftMarginInfo.create()));

    if (!isDraft) {
      headerElements.add(rightMarginInfo.create());
    }

    if (isDraft) {
      headerElements.add(watermark.create());
    }

    return element(Tag.DIV).appendChildren(headerElements);
  }

  private Element header() {
    return element(Tag.DIV)
        .attr(STYLE, HEADER_STYLE)
        .attr(TITLE, HEADER)
        .appendChildren(List.of(pageHeader(), certificateHeader()));
  }

  private Element pageHeader() {
    return element(Tag.DIV)
        .attr(STYLE, "display: flex; top: 0; left: 0; margin-bottom: 10mm")
        .appendChildren(List.of(
            HeaderElementFactory.recipientLogo(recipientLogo, recipientName),
            HeaderElementFactory.personId(personId)));
  }

  private Element certificateHeader() {
    return element(Tag.DIV)
        .attr(STYLE, "margin-bottom: 5mm")
        .appendChildren(List.of(
            HeaderElementFactory.title(certificateName, certificateType, certificateVersion),
            HeaderElementFactory.alert(recipientName, isDraft, isSent, isCanSendElectronically,
                draftAlertInfoText)
        ));
  }

}
