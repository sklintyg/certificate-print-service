package se.inera.intyg.certificateprintservice.playwright.element;

import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.element.ElementProvider.element;

import java.util.List;
import javax.swing.text.html.HTML.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Element;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FooterElementFactory {

  private static final String LINK_TEXT = "www.inera.se";
  private static final String FOOTER_INFO_TEXT = "Utskriften skapades med %s - en tjänst som drivs av Inera AB";

  public static Element info(String origin) {
    return element(Tag.DIV)
        .appendChildren(List.of(
            element(Tag.P)
                .attr(STYLE, "display: block margin-top: 5mm; margind-bottom: 2mm;")
                .text(FOOTER_INFO_TEXT.formatted(origin)),
            element(Tag.P)
                .attr(STYLE, "display: block margin-top: 5mm; margind-bottom: 2mm;")
                .text(LINK_TEXT)));
  }

  public static Element pageNumber() {
    return element(Tag.DIV)
        .attr(STYLE, "margin-top: 5mm;")
        .appendChildren(List.of(
            element(Tag.SPAN).addClass("pageNumber"),
            element(Tag.SPAN).text(" ("),
            element(Tag.SPAN).addClass("totalPages"),
            element(Tag.SPAN).text(")")
        ));
  }

}
