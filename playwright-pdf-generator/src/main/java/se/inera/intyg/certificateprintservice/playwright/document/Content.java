package se.inera.intyg.certificateprintservice.playwright.document;

import static se.inera.intyg.certificateprintservice.playwright.element.ElementProvider.element;

import java.util.List;
import javax.swing.text.html.HTML.Tag;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jsoup.nodes.Element;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Category;
import se.inera.intyg.certificateprintservice.playwright.certificate.CategoryConverter;
import se.inera.intyg.certificateprintservice.playwright.element.ContentElementFactory;

@Builder
@Getter
@EqualsAndHashCode
public class Content {

  List<Category> categories;
  String certificateName;
  String certificateType;
  String certificateVersion;
  String recipientName;
  String personId;
  String issuerName;
  String issuingUnit;
  List<String> issuingUnitInfo;
  String signDate;
  String description;
  boolean isDraft;
  boolean isSent;
  boolean isCanSendElectronically;
  String draftAlertInfoText;

  public Element create() {
    return element(Tag.DIV)
        .appendChild(ContentElementFactory.hiddenAccessibleHeader(certificateName, certificateType,
            certificateVersion, recipientName, personId, isDraft, isSent, isCanSendElectronically,
            draftAlertInfoText))
        .appendChildren(content())
        .appendChildren(List.of(
            ContentElementFactory.issuerInfo(issuerName, issuingUnit, issuingUnitInfo,
                signDate, isDraft),
            ContentElementFactory.certificateInformation(certificateName, description,
                isCanSendElectronically)));
  }

  private List<Element> content() {
    return categories.stream()
        .map(CategoryConverter::category)
        .toList();
  }

}
