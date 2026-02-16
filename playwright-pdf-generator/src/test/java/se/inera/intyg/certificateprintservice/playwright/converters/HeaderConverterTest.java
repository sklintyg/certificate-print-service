package se.inera.intyg.certificateprintservice.playwright.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.HEADER;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.GeneralPrintText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Metadata;
import se.inera.intyg.certificateprintservice.playwright.document.LeftMarginInfo;
import se.inera.intyg.certificateprintservice.playwright.document.RightMarginInfo;
import se.inera.intyg.certificateprintservice.playwright.document.Watermark;

@ExtendWith(MockitoExtension.class)
class HeaderConverterTest {

  @Mock
  private LeftMarginInfoConverter leftMarginInfoConverter;
  @Mock
  private RightMarginInfoConverter rightMarginInfoConverter;

  @InjectMocks
  private HeaderConverter headerConverter;

  private static final String CERTIFICATE_NAME = "certificateName";
  private static final String TYPE_ID = "typeId";
  private static final String VERSION = "version";
  private static final String PERSON_ID = "personId";
  private static final String RECIPIENT_NAME = "recipientName";
  private static final String SIGNING_DATE = "2025-01-18";
  private static final String SENT_DATE = "2025-01-18";
  private static final byte[] RECIPIENT_LOGO = "recipientLogo".getBytes();
  private static final LeftMarginInfo LEFT_MARGIN_INFO = LeftMarginInfo.builder().build();
  private static final RightMarginInfo RIGHT_MARGIN_INFO = RightMarginInfo.builder().build();
  private static final Watermark WATERMARK = Watermark.builder().build();
  private static final int HEADER_HEIGHT = 77;

  private static final Metadata METADATA = Metadata.builder()
      .name(CERTIFICATE_NAME)
      .version(VERSION)
      .typeId(TYPE_ID)
      .personId(PERSON_ID)
      .recipientLogo(RECIPIENT_LOGO)
      .recipientName(RECIPIENT_NAME)
      .signingDate(SIGNING_DATE)
      .sentDate(SENT_DATE)
      .generalPrintText(GeneralPrintText.builder()
          .leftMarginInfoText("leftMarginInfoText")
          .draftAlertInfoText("draftAlertInfoText")
          .build())
      .build();

  @Test
  void shouldSetName() {
    final var response = headerConverter.convert(METADATA);
    assertEquals(CERTIFICATE_NAME, response.getCertificateName());
  }

  @Test
  void shouldSetVersion() {
    final var response = headerConverter.convert(METADATA);
    assertEquals(VERSION, response.getCertificateVersion());
  }

  @Test
  void shouldSetTypeId() {
    final var response = headerConverter.convert(METADATA);
    assertEquals(TYPE_ID, response.getCertificateType());
  }

  @Test
  void shouldSetPersonId() {
    final var response = headerConverter.convert(METADATA);
    assertEquals(PERSON_ID, response.getPersonId());
  }

  @Test
  void shouldSetRecipientLogo() {
    final var response = headerConverter.convert(METADATA);
    assertEquals(RECIPIENT_LOGO, response.getRecipientLogo());
  }

  @Test
  void shouldSetRecipientName() {
    final var response = headerConverter.convert(METADATA);
    assertEquals(RECIPIENT_NAME, response.getRecipientName());
  }

  @Test
  void shouldSetLeftMarginInfo() {
    when(leftMarginInfoConverter.convert(METADATA)).thenReturn(LEFT_MARGIN_INFO);
    final var response = headerConverter.convert(METADATA);
    assertEquals(LEFT_MARGIN_INFO, response.getLeftMarginInfo());
  }

  @Test
  void shouldSetRightMarginInfo() {
    when(rightMarginInfoConverter.convert(METADATA)).thenReturn(RIGHT_MARGIN_INFO);
    final var response = headerConverter.convert(METADATA);
    assertEquals(RIGHT_MARGIN_INFO, response.getRightMarginInfo());
  }

  @Test
  void shouldSetWatermark() {
    final var response = headerConverter.convert(METADATA);
    assertEquals(WATERMARK, response.getWatermark());
  }

  @Test
  void shouldSetIsDraft() {
    final var response = headerConverter.convert(METADATA);
    assertFalse(response.isDraft());
  }

  @Test
  void shouldSetIsSent() {
    final var response = headerConverter.convert(METADATA);
    assertTrue(response.isSent());
  }

  @Nested
  class HeaderHeight {

    @Test
    void shouldSetHeaderHeight() {
      final var page = mock(Page.class);
      final var locator = mock(Locator.class);
      when(page.getByTitle(anyString())).thenReturn(locator);
      when(locator.evaluate("node => node.offsetHeight")).thenReturn(HEADER_HEIGHT);
      final var headerHeight = headerConverter.headerHeight(page, HEADER);
      assertEquals(HEADER_HEIGHT, headerHeight);
    }
  }

}
