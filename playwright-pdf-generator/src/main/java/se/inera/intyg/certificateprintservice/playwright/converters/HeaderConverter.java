package se.inera.intyg.certificateprintservice.playwright.converters;

import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Metadata;
import se.inera.intyg.certificateprintservice.playwright.document.Header;
import se.inera.intyg.certificateprintservice.playwright.document.Watermark;

@Component
@RequiredArgsConstructor
public class HeaderConverter {

  private final LeftMarginInfoConverter leftMarginInfoConverter;
  private final RightMarginInfoConverter rightMarginInfoConverter;

  private static final String HEADER = "header";

  public Header convert(Metadata metadata) {
    return Header.builder()
        .certificateName(metadata.getName())
        .certificateType(metadata.getTypeId())
        .certificateVersion(metadata.getVersion())
        .personId(metadata.getPersonId())
        .recipientName(metadata.getRecipientName())
        .recipientLogo(metadata.getRecipientLogo())
        .leftMarginInfo(leftMarginInfoConverter.convert(metadata))
        .rightMarginInfo(rightMarginInfoConverter.convert(metadata))
        .watermark(Watermark.builder().build())
        .isDraft(metadata.isDraft())
        .isSent(metadata.isSent())
        .isCanSendElectronically(metadata.isCanSendElectronically())
        .draftAlertInfoText(metadata.getGeneralPrintText() != null ? metadata.getGeneralPrintText()
            .getDraftAlertInfoText() : null)
        .build();
  }

  public int headerHeight(Page page, String header) {
    page.setContent(header);
    return (int) page.getByTitle(HEADER).evaluate("node => node.offsetHeight");
  }

}
