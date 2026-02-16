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

class WatermarkTest {

  private static final String UTKAST = "UTKAST";
  private final Watermark watermark = Watermark.builder().build();


  @Nested
  class Wrapper {

    @Test
    void wrapperDiv() {
      final var element = watermark.create();
      assertAll(
          () -> assertEquals(DIV, element.tag(), TAG_TYPE),
          () -> assertEquals(1, element.children().size(), NUM_CHILDREN),
          () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
          () -> assertEquals(
              "position: absolute; top: calc(50% - 8mm); left: 50%; transform: translateX(-50%) translateY(-50%) rotate(315deg); color: rgb(128, 128, 128); opacity: 0.5; z-index: -1; font-family: 'Liberation Sans', sans-serif; font-size: 100pt;",
              attributes(element, STYLE), ATTRIBUTES)
      );
    }

    @Nested
    class WrapperElements {

      @Test
      void watermark() {
        final var element = watermark.create().child(0);
        assertAll(
            () -> assertEquals(P, element.tag(), TAG_TYPE),
            () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
            () -> assertEquals(UTKAST, element.text(), TEXT),
            () -> assertEquals(0, attributesSize(element), NUM_ATTRIBUTES)
        );
      }
    }
  }

}
