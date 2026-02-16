package se.inera.intyg.certificateprintservice.playwright.document;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.ATTRIBUTES;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.DIV;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.NUM_ATTRIBUTES;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.NUM_CHILDREN;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.P;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TAG_TYPE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TEXT;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.attributes;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.attributesSize;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LeftMarginInfoTest {

  private static final String CERTIFICATE_TYPE = "certificateType";
  private static final String RECIPIENT_NAME = "recipientName";
  private static final String LEFT_MARGIN_TEXT_SKR = "%s %s %s - Fastställd av %s";
  private static final String LEFT_MARGIN_TEXT = "%s %s - Fastställd av %s";
  public static final String VERSION = "version";
  public static final String RECIPIENT_ID = "recipientId";
  public static final String RECIPIENT_ID_TS = "TRANSP";

  private final LeftMarginInfo leftMarginInfoSKR = LeftMarginInfo.builder()
      .certificateType(CERTIFICATE_TYPE)
      .recipientName(RECIPIENT_NAME)
      .certificateVersion(VERSION)
      .recipientId(RECIPIENT_ID)
      .leftMarginText(LEFT_MARGIN_TEXT_SKR)
      .build();

  private final LeftMarginInfo leftMarginInfo = LeftMarginInfo.builder()
      .certificateType(CERTIFICATE_TYPE)
      .recipientName(RECIPIENT_NAME)
      .certificateVersion(VERSION)
      .recipientId(RECIPIENT_ID_TS)
      .build();

  @Nested
  class Wrapper {

    @Test
    void wrapperDiv() {
      final var element = leftMarginInfo.create();
      assertAll(
          () -> assertEquals(DIV, element.tag(), TAG_TYPE),
          () -> assertEquals(1, element.children().size(), NUM_CHILDREN),
          () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
          () -> assertEquals(
              "position: absolute; left: 1cm; bottom: 35mm; transform: rotate(-90deg) translateY(-50%); transform-origin: top left; font-family: 'Liberation Sans', sans-serif; font-size: 10pt;",
              attributes(element, STYLE), ATTRIBUTES)
      );
    }

    @Nested
    class WrapperElements {

      @Test
      void leftMarginInfoSKR() {
        final var expectedText = LEFT_MARGIN_TEXT_SKR.formatted(RECIPIENT_ID, CERTIFICATE_TYPE,
            VERSION,
            RECIPIENT_NAME);
        final var element = leftMarginInfoSKR.create().child(0);
        assertAll(
            () -> assertEquals(P, element.tag(), TAG_TYPE),
            () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
            () -> assertEquals(expectedText, element.text(), TEXT),
            () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
            () -> assertEquals("margin: 0;", attributes(element, STYLE), ATTRIBUTES)
        );
      }

      @Test
      void leftMarginInfoDefault() {
        final var expectedText = LEFT_MARGIN_TEXT.formatted(CERTIFICATE_TYPE, VERSION,
            RECIPIENT_NAME);
        final var element = leftMarginInfo.create().child(0);
        assertAll(
            () -> assertEquals(P, element.tag(), TAG_TYPE),
            () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
            () -> assertEquals(expectedText, element.text(), TEXT),
            () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
            () -> assertEquals("margin: 0;", attributes(element, STYLE), ATTRIBUTES)
        );
      }
    }
  }

}
