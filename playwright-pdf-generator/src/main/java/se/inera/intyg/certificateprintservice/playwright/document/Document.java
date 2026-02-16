package se.inera.intyg.certificateprintservice.playwright.document;

import static se.inera.intyg.certificateprintservice.playwright.document.Constants.CONTENT;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.SRC;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TITLE;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.swing.text.html.HTML.Tag;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.core.io.Resource;

@Builder
@Getter
@EqualsAndHashCode
public class Document {

  Content content;
  String certificateName;
  String certificateType;
  String certificateVersion;
  String tailWindScript;
  boolean isDraft;

  public org.jsoup.nodes.Document build(Resource template, int headerHeight) throws IOException {
    final var document = document(template);
    setPageStyle(document, headerHeight);
    setTitle(document);
    setTailWindScript(document);
    getElement(document, CONTENT).appendChild(content.create());
    return document;
  }

  private Element getElement(org.jsoup.nodes.Document document, String name) {
    return Objects.requireNonNull(document.getElementById(name));
  }

  private void setPageStyle(org.jsoup.nodes.Document document, int headerHeight) {
    final var style = """
        @page {
          margin: calc(%spx + 16mm) 20mm 39mm 20mm;
        }""".formatted(headerHeight);
    Objects.requireNonNull(document.getElementById(STYLE)).append(style);
  }

  private void setTitle(org.jsoup.nodes.Document document) {
    final var titleElement = getElement(document, TITLE);
    final var title = "%s (%s v%s)".formatted(certificateName, certificateType, certificateVersion);
    titleElement.appendText(title);
  }

  private org.jsoup.nodes.Document document(Resource template) throws IOException {
    return Jsoup.parse(template.getInputStream(), StandardCharsets.UTF_8.name(),
        "", Parser.xmlParser());
  }

  private void setTailWindScript(org.jsoup.nodes.Document document) {
    final var script = "data:text/javascript;base64, %s".formatted(tailWindScript);
    document.getElementsByTag(Tag.SCRIPT.toString())
        .attr(SRC, script);
  }

}
