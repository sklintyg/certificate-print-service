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

class RightMarginInfoTest {

  private static final String CERTIFICATE_ID = "certificateId";
  private static final String RIGHT_MARGIN_TEXT = "Intygs-ID: %s";

  private final RightMarginInfo rightMarginInfo = RightMarginInfo.builder()
      .certificateId(CERTIFICATE_ID)
      .build();

  @Nested
  class Wrapper {

    @Test
    void wrapperDiv() {
      final var element = rightMarginInfo.create();
      assertAll(
          () -> assertEquals(DIV, element.tag(), TAG_TYPE),
          () -> assertEquals(1, element.children().size(), NUM_CHILDREN),
          () -> assertEquals(1, element.attributes().asList().size(), NUM_ATTRIBUTES),
          () -> assertEquals(
              "position: absolute; left: 20cm; bottom: 35mm; width: 100%; transform: rotate(-90deg) translateY(-50%); transform-origin: top left; font-family: 'Liberation Sans', sans-serif; font-size: 10pt;",
              attributes(element, STYLE), ATTRIBUTES)

      );
    }

    @Nested
    class WrapperNodes {

      @Test
      void pElementFirst() {
        final var expectedText = RIGHT_MARGIN_TEXT.formatted(CERTIFICATE_ID);
        final var element = rightMarginInfo.create().child(0);
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
