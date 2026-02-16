package se.inera.intyg.certificateprintservice.playwright.element;

import static se.inera.intyg.certificateprintservice.playwright.document.Constants.ALT;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.SRC;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.element.ElementProvider.element;

import java.util.Base64;
import java.util.List;
import javax.swing.text.html.HTML.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Element;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderElementFactory {

  private static final String DRAFT_ALERT_MESSAGE = "Detta är en utskrift av ett elektroniskt intygsutkast och ska INTE skickas till %s.";
  private static final String SENT_ALERT_MESSAGE = "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren. Notera att intyget redan har skickats till %s.";
  private static final String SIGNED_ALERT_MESSAGE = "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.";
  private static final String PERSON_SAMORDNINGS_NR = "Person- /samordningsnr";

  public static Element recipientLogo(byte[] logoBytes, String recipientName) {
    final var logoBase64 = new String(Base64.getEncoder().encode(logoBytes));
    return element(Tag.DIV).appendChild(
        element(Tag.IMG)
            .attr(STYLE, "max-height: 15mm; max-width: 35mm;")
            .attr(SRC, "data:image/png;base64, " + logoBase64)
            .attr(ALT, "%s logotyp".formatted(recipientName))
    );
  }

  public static Element personId(String personId) {
    return element(Tag.DIV)
        .attr(STYLE, "float: right; text-align: right; width: 100%;")
        .appendChildren(List.of(
                element(Tag.P)
                    .attr(STYLE, "font-weight: bold; margin: 0;")
                    .appendText(PERSON_SAMORDNINGS_NR),
                element(Tag.P).appendText(personId)
                    .attr(STYLE, "margin: 0;")
            )
        );
  }

  public static Element title(String name, String type, String version) {
    return element(Tag.DIV)
        .attr(STYLE,
            "font-size: 14pt; border-bottom: black solid 1px; margin: 0; padding-bottom: 1mm;")
        .appendChildren(List.of(
            element(Tag.P)
                .attr(STYLE, "font-weight: bold; display: inline; margin: 0;")
                .appendText(name),
            element(Tag.P)
                .attr(STYLE, "display: inline; margin: 0;")
                .appendText(" (%s)".formatted(type))
        ));
  }

  public static Element alert(String recipientName, boolean isDraft, boolean isSent,
      boolean isCanSendElectronically, String draftAlertInfoText) {
    final var alertMessage = alertMessage(recipientName, isDraft, isSent, isCanSendElectronically,
        draftAlertInfoText);
    return element(Tag.DIV)
        .attr(STYLE, "margin-top: 5mm; padding: 3mm 5mm; border: red solid 1px;")
        .appendChild(element(Tag.P)
            .attr(STYLE, "margin: 0;")
            .appendText(alertMessage));
  }

  public static String alertMessage(String recipientName, boolean isDraft, boolean isSent,
      boolean isCanSendElectronically, String draftAlertInfoText) {
    if (isDraft && !isCanSendElectronically && draftAlertInfoText != null) {
      return DRAFT_ALERT_MESSAGE.formatted(draftAlertInfoText);
    }
    if (isDraft) {
      return DRAFT_ALERT_MESSAGE.formatted(recipientName);
    }
    if (isSent) {
      return
          SENT_ALERT_MESSAGE.formatted(recipientName);
    }
    return SIGNED_ALERT_MESSAGE;
  }

}
