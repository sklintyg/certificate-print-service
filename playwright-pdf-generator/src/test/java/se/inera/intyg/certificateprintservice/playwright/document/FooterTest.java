package se.inera.intyg.certificateprintservice.playwright.document;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.ATTRIBUTES;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.CLASS;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.DIV;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.NUM_ATTRIBUTES;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.NUM_CHILDREN;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.P;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.SPAN;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TAG_TYPE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TEXT;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.attributes;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.attributesSize;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FooterTest {

  private static final String APPLICATION_ORIGIN = "applicationOrigin";
  private static final String FOOTER_TEXT = "Utskriften skapades med %s - en tjänst som drivs av Inera AB";
  private static final String FOOTER_LINK_URL = "https://inera.se";
  private static final String FOOTER_LINK_TEXT = "www.inera.se";

  private final Footer footer = Footer.builder()
      .applicationOrigin(APPLICATION_ORIGIN)
      .build();

  @Nested
  class FooterWrapper {

    @Test
    void FooterWrapper() {
      final var element = footer.create();
      assertAll(
          () -> assertEquals(DIV, element.tag(), TAG_TYPE),
          () -> assertEquals(1, element.children().size(), NUM_CHILDREN),
          () -> assertEquals(0, attributesSize(element), ATTRIBUTES)
      );
    }

    @Nested
    class Footer {

      @Test
      void footer() throws NullPointerException {
        final var expectedText = FOOTER_TEXT.formatted(APPLICATION_ORIGIN);
        final var element = footer.create().child(0);
        assertAll(
            () -> assertEquals(DIV, element.tag(), TAG_TYPE),
            () -> assertEquals(2, element.children().size(), NUM_CHILDREN),
            () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
            () -> assertEquals(
                "margin: 0 20mm; width: 17cm; height: 25mm; border-top: black solid 1px; justify-content: space-between; display: flex; font-family: 'Liberation Sans', sans-serif; font-size: 10pt;",
                attributes(element, STYLE), ATTRIBUTES)
        );
      }

      @Nested
      class FooterInfoWrapper {

        @Test
        void footerInfoWrapper() {
          final var element = footer.create().child(0).child(0);
          assertAll(
              () -> assertEquals(DIV, element.tag(), TAG_TYPE),
              () -> assertEquals(2, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(0, attributesSize(element), ATTRIBUTES)
          );
        }

        @Nested
        class FooterInfo {

          @Test
          void footerText() throws NullPointerException {
            final var expectedText = FOOTER_TEXT.formatted(APPLICATION_ORIGIN);
            final var element = footer.create().child(0).child(0).child(0);
            assertAll(
                () -> assertEquals(P, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(expectedText, element.text(), TEXT),
                () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                () -> assertEquals("display: block margin-top: 5mm; margind-bottom: 2mm;",
                    attributes(element, STYLE), ATTRIBUTES)
            );
          }

          @Test
          void footerLink() {
            final var element = footer.create().child(0).child(0).child(1);
            assertAll(
                () -> assertEquals(P, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(FOOTER_LINK_TEXT, element.text(), TEXT),
                () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES)
            );
          }
        }
      }

      @Nested
      class PageNumber {

        @Test
        void pageNumber() {
          final var element = footer.create().child(0).child(1);
          assertAll(
              () -> assertEquals(DIV, element.tag(), TAG_TYPE),
              () -> assertEquals(4, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals("margin-top: 5mm;", attributes(element, STYLE), ATTRIBUTES)
          );
        }

        @Nested
        class PageNumberElements {

          @Test
          void currentPage() {
            final var element = footer.create().child(0).child(1).child(0);
            assertAll(
                () -> assertEquals(SPAN, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                () -> assertEquals("pageNumber", attributes(element, CLASS), ATTRIBUTES)
            );
          }

          @Test
          void pageNumberLeftParenthesis() {
            final var element = footer.create().child(0).child(1).child(1);
            assertAll(
                () -> assertEquals(SPAN, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(0, attributesSize(element), NUM_ATTRIBUTES),
                () -> assertEquals("(", element.text(), TEXT)
            );
          }

          @Test
          void totalPages() {
            final var element = footer.create().child(0).child(1).child(2);
            assertAll(
                () -> assertEquals(SPAN, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                () -> assertEquals("totalPages", attributes(element, CLASS), ATTRIBUTES)
            );
          }

          @Test
          void pageNumberRightParenthesis() {
            final var element = footer.create().child(0).child(1).child(3);
            assertAll(
                () -> assertEquals(SPAN, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(0, attributesSize(element), NUM_ATTRIBUTES),
                () -> assertEquals(")", element.text(), TEXT)
            );
          }
        }
      }
    }
  }

}
