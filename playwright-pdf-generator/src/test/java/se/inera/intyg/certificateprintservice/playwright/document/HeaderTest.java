package se.inera.intyg.certificateprintservice.playwright.document;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.ALT;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.ATTRIBUTES;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.DIV;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.HEADER;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.IMG;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.NUM_ATTRIBUTES;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.NUM_CHILDREN;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.P;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.SRC;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TAG_TYPE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TEXT;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TITLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.attributes;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.attributesSize;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HeaderTest {

  private static final String CERTIFICATE_NAME = "certificateName";
  private static final String CERTIFICATE_TYPE = "certificateType";
  private static final String CERTIFICATE_VERSION = "certificateVersion";
  private static final byte[] RECIPIENT_LOGO = "recipientLogo".getBytes();
  private static final String PERSON_ID = "personId";
  private static final String RECIPIENT_NAME = "recipientName";
  private static final String TYPE_VERSION_TEXT = "(%s)";
  private static final String DRAFT_ALERT_MESSAGE = "Detta är en utskrift av ett elektroniskt intygsutkast och ska INTE skickas till %s.";
  private static final String SENT_ALERT_MESSAGE = "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren. Notera att intyget redan har skickats till %s.";
  private static final String SIGNED_ALERT_MESSAGE = "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.";
  private static final String LEFT_MARGIN_TEXT = "%s %s %s - Fastställd av %s";
  private static final String CERTIFICATE_ID = "certificateId";
  private static final String RIGHT_MARGIN_TEXT = "Intygs-ID: %s";
  private static final String RECIPIENT_ID = "recipientId";
  private static final String DRAFT_ALERT_INFO_TEXT = "arbetsgivaren";

  private final Header.HeaderBuilder headerBuilder = Header.builder()
      .certificateName(CERTIFICATE_NAME)
      .certificateType(CERTIFICATE_TYPE)
      .certificateVersion(CERTIFICATE_VERSION)
      .personId(PERSON_ID)
      .recipientLogo(RECIPIENT_LOGO)
      .recipientName(RECIPIENT_NAME)
      .draftAlertInfoText(DRAFT_ALERT_INFO_TEXT)
      .leftMarginInfo(LeftMarginInfo.builder()
          .certificateType(CERTIFICATE_TYPE)
          .recipientName(RECIPIENT_NAME)
          .recipientId(RECIPIENT_ID)
          .certificateVersion(CERTIFICATE_VERSION)
          .leftMarginText(LEFT_MARGIN_TEXT)
          .build())
      .rightMarginInfo(RightMarginInfo.builder().certificateId(CERTIFICATE_ID).build())
      .watermark(Watermark.builder().build());

  @Nested
  class HeaderWrapper {

    @Test
    void BaseWrapper() {
      final var header = headerBuilder.isDraft(false).isSent(false).build();
      final var element = header.create();
      assertAll(
          () -> assertEquals(DIV, element.tag(), TAG_TYPE),
          () -> assertEquals(3, element.children().size(), NUM_CHILDREN),
          () -> assertEquals(0, attributesSize(element), NUM_ATTRIBUTES)
      );
    }

    @Nested
    class Header {

      @Test
      void headerWrapper() {
        final var header = headerBuilder.isDraft(true).isSent(false).build();
        final var element = header.create().child(0);
        assertAll(
            () -> assertEquals(DIV, element.tag(), TAG_TYPE),
            () -> assertEquals(2, element.children().size(), NUM_CHILDREN),
            () -> assertEquals(2, attributesSize(element), NUM_ATTRIBUTES),
            () -> assertEquals(HEADER, attributes(element, TITLE), ATTRIBUTES),
            () -> assertEquals(
                "margin: 10mm 20mm; display: grid; width: 17cm; font-family: 'Liberation Sans', sans-serif; font-size: 10pt;",
                attributes(element, STYLE), ATTRIBUTES)
        );
      }

      @Nested
      class PageHeader {

        @Test
        void pageHeaderWrapper() {
          final var header = headerBuilder.isDraft(true).isSent(false).build();
          final var element = header.create().child(0).child(0);
          assertAll(
              () -> assertEquals(DIV, element.tag(), TAG_TYPE),
              () -> assertEquals(2, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals("display: flex; top: 0; left: 0; margin-bottom: 10mm",
                  attributes(element, STYLE), ATTRIBUTES)
          );
        }

        @Nested
        class PageHeaderElements {

          @Test
          void recipientLogoWrapper() {
            final var header = headerBuilder.isDraft(true).isSent(false).build();
            final var element = header.create().child(0).child(0).child(0);
            assertAll(
                () -> assertEquals(DIV, element.tag(), TAG_TYPE),
                () -> assertEquals(1, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(0, attributesSize(element), NUM_ATTRIBUTES)
            );
          }

          @Test
          void personIdWrapper() {
            final var header = headerBuilder.isDraft(true).isSent(false).build();
            final var element = header.create().child(0).child(0).child(1);
            assertAll(
                () -> assertEquals(DIV, element.tag(), TAG_TYPE),
                () -> assertEquals(2, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                () -> assertEquals("float: right; text-align: right; width: 100%;",
                    attributes(element, STYLE), ATTRIBUTES)
            );
          }

          @Nested
          class RecipientLogo {

            @Test
            void recipientLogo() throws NullPointerException {
              final var header = headerBuilder.isDraft(true).isSent(false).build();
              final var element = header.create().child(0).child(0).child(0).child(0);
              assertAll(
                  () -> assertEquals(IMG, element.tag(), TAG_TYPE),
                  () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                  () -> assertEquals(3, attributesSize(element), NUM_ATTRIBUTES),
                  () -> assertEquals("data:image/png;base64, cmVjaXBpZW50TG9nbw==",
                      attributes(element, SRC), ATTRIBUTES),
                  () -> assertEquals("%s logotyp".formatted(RECIPIENT_NAME),
                      attributes(element, ALT), ATTRIBUTES),
                  () -> assertEquals("max-height: 15mm; max-width: 35mm;",
                      attributes(element, STYLE), ATTRIBUTES)
              );
            }
          }

          @Nested
          class PersonId {

            @Test
            void personIdHeader() throws NullPointerException {
              final var header = headerBuilder.isDraft(true).isSent(false).build();
              final var element = header.create().child(0).child(0).child(1).child(0);
              assertAll(
                  () -> assertEquals(P, element.tag(), TAG_TYPE),
                  () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                  () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                  () -> assertEquals("Person- /samordningsnr", element.text(), TEXT),
                  () -> assertEquals("font-weight: bold; margin: 0;", attributes(element, STYLE),
                      ATTRIBUTES)
              );
            }

            @Test
            void personId() throws NullPointerException {
              final var header = headerBuilder.isDraft(true).isSent(false).build();
              final var element = header.create().child(0).child(0).child(1).child(1);
              assertAll(
                  () -> assertEquals(P, element.tag(), TAG_TYPE),
                  () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                  () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                  () -> assertEquals(PERSON_ID, element.text(), TEXT),
                  () -> assertEquals("margin: 0;", attributes(element, STYLE), ATTRIBUTES)
              );
            }
          }
        }
      }

      @Nested
      class CertificateHeader {

        @Test
        void certificateHeaderWrapper() {
          final var header = headerBuilder.isDraft(true).isSent(false).build();
          final var element = header.create().child(0).child(1);
          assertAll(
              () -> assertEquals(DIV, element.tag(), TAG_TYPE),
              () -> assertEquals(2, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals("margin-bottom: 5mm", attributes(element, STYLE), ATTRIBUTES)
          );
        }

        @Nested
        class CertificateHeaderElements {

          @Test
          void titleWrapper() {
            final var header = headerBuilder.isDraft(true).isSent(false).build();
            final var element = header.create().child(0).child(1).child(0);
            assertAll(
                () -> assertEquals(DIV, element.tag(), TAG_TYPE),
                () -> assertEquals(2, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                () -> assertEquals(
                    "font-size: 14pt; border-bottom: black solid 1px; margin: 0; padding-bottom: 1mm;",
                    attributes(element, STYLE), ATTRIBUTES)
            );
          }

          @Test
          void alertWrapper() {
            final var header = headerBuilder.isDraft(true).isSent(false).build();
            final var element = header.create().child(0).child(1).child(1);
            assertAll(
                () -> assertEquals(DIV, element.tag(), TAG_TYPE),
                () -> assertEquals(1, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                () -> assertEquals("margin-top: 5mm; padding: 3mm 5mm; border: red solid 1px;",
                    attributes(element, STYLE), ATTRIBUTES)
            );
          }

          @Nested
          class Title {

            @Test
            void certificateName() throws NullPointerException {
              final var header = headerBuilder.isDraft(true).isSent(false).build();
              final var element = header.create().child(0).child(1).child(0).child(0);
              assertAll(
                  () -> assertEquals(P, element.tag(), TAG_TYPE),
                  () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                  () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                  () -> assertEquals(CERTIFICATE_NAME, element.text(), TEXT),
                  () -> assertEquals("font-weight: bold; display: inline; margin: 0;",
                      attributes(element, STYLE), ATTRIBUTES)
              );
            }

            @Test
            void certificateType() throws NullPointerException {
              final var expectedText = TYPE_VERSION_TEXT.formatted(CERTIFICATE_TYPE);
              final var header = headerBuilder.isDraft(true).isSent(false).build();
              final var element = header.create().child(0).child(1).child(0).child(1);
              assertAll(
                  () -> assertEquals(P, element.tag(), TAG_TYPE),
                  () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                  () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                  () -> assertEquals(expectedText, element.text(), TEXT),
                  () -> assertEquals("display: inline; margin: 0;", attributes(element, STYLE),
                      ATTRIBUTES)
              );
            }
          }

          @Nested
          class Alert {

            @Test
            void alertMessageDraft() throws NullPointerException {
              final var header = headerBuilder.isDraft(true).isSent(false)
                  .isCanSendElectronically(true).build();
              final var element = header.create().child(0).child(1).child(1).child(0);
              assertAll(
                  () -> assertEquals(P, element.tag(), TAG_TYPE),
                  () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                  () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                  () -> assertEquals(DRAFT_ALERT_MESSAGE.formatted(RECIPIENT_NAME), element.text(),
                      TEXT),
                  () -> assertEquals("margin: 0;", attributes(element, STYLE), ATTRIBUTES)
              );
            }

            @Test
            void CannotSendAlertMessageDraft() throws NullPointerException {
              final var header = headerBuilder.isDraft(true).isSent(false)
                  .isCanSendElectronically(false).build();
              final var element = header.create().child(0).child(1).child(1).child(0);
              assertAll(
                  () -> assertEquals(P, element.tag(), TAG_TYPE),
                  () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                  () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                  () -> assertEquals(DRAFT_ALERT_MESSAGE.formatted("arbetsgivaren"), element.text(),
                      TEXT),
                  () -> assertEquals("margin: 0;", attributes(element, STYLE), ATTRIBUTES)
              );
            }

            @Test
            void alertMessageSigned() throws NullPointerException {
              final var header = headerBuilder.isDraft(false).isSent(false)
                  .isCanSendElectronically(true).build();
              final var element = header.create().child(0).child(1).child(1).child(0);
              assertAll(
                  () -> assertEquals(P, element.tag(), TAG_TYPE),
                  () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                  () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                  () -> assertEquals(SIGNED_ALERT_MESSAGE, element.text(), TEXT),
                  () -> assertEquals("margin: 0;", attributes(element, STYLE), ATTRIBUTES)
              );
            }

            @Test
            void alertMessageSent() throws NullPointerException {
              final var header = headerBuilder.isDraft(false).isSent(true)
                  .isCanSendElectronically(true).build();
              final var element = header.create().child(0).child(1).child(1).child(0);
              assertAll(
                  () -> assertEquals(P, element.tag(), TAG_TYPE),
                  () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                  () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                  () -> assertEquals(SENT_ALERT_MESSAGE.formatted(RECIPIENT_NAME), element.text(),
                      TEXT),
                  () -> assertEquals("margin: 0;", attributes(element, STYLE), ATTRIBUTES)
              );
            }

            @Test
            void alertMessageCannotSent() throws NullPointerException {
              final var header = headerBuilder.isDraft(false).isSent(false)
                  .isCanSendElectronically(false).build();
              final var element = header.create().child(0).child(1).child(1).child(0);
              assertAll(
                  () -> assertEquals(P, element.tag(), TAG_TYPE),
                  () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                  () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
                  () -> assertEquals(SIGNED_ALERT_MESSAGE, element.text(),
                      TEXT),
                  () -> assertEquals("margin: 0;", attributes(element, STYLE), ATTRIBUTES)
              );
            }
          }
        }
      }
    }

    @Nested
    class Watermark {

      String expectedText = "UTKAST";

      @Test
      void watermarkDraft() {
        final var header = headerBuilder.isDraft(true).isSent(false).build();
        final var element = header.create();
        assertNotEquals(0, element.getElementsMatchingText(expectedText).size());
      }

      @Test
      void watermarkSigned() {
        final var header = headerBuilder.isDraft(false).isSent(false).build();
        final var element = header.create().child(2);
        assertEquals(0, element.getElementsMatchingText(expectedText).size());
      }

      @Test
      void watermarkSent() {
        final var header = headerBuilder.isDraft(false).isSent(true).build();
        final var element = header.create().child(2);
        assertEquals(0, element.getElementsMatchingText(expectedText).size());
      }
    }

    @Nested
    class RightMarginInfo {

      String expectedText = RIGHT_MARGIN_TEXT.formatted(CERTIFICATE_ID);

      @Test
      void rightMarginInfoDraft() {
        final var header = headerBuilder.isDraft(true).isSent(false).build();
        final var element = header.create();
        assertEquals(0, element.getElementsMatchingText(expectedText).size());
      }

      @Test
      void rightMarginInfoSigned() {
        final var header = headerBuilder.isDraft(false).isSent(false).build();
        final var element = header.create().child(2);
        assertNotEquals(0, element.getElementsMatchingText(expectedText).size());
      }

      @Test
      void rightMarginInfoSent() {
        final var header = headerBuilder.isDraft(false).isSent(true).build();
        final var element = header.create().child(2);
        assertNotEquals(0, element.getElementsMatchingText(expectedText).size());
      }
    }

    @Nested
    class LeftMarginInfo {

      String expectedText = LEFT_MARGIN_TEXT.formatted(RECIPIENT_ID, CERTIFICATE_TYPE,
          CERTIFICATE_VERSION,
          RECIPIENT_NAME);

      @Test
      void leftMarginInfoDraft() {
        final var header = headerBuilder.isDraft(true).isSent(false).build();
        final var element = header.create().child(1);
        assertNotEquals(0, element.getElementsMatchingText(expectedText).size());
      }

      @Test
      void leftMarginInfoSigned() {
        final var header = headerBuilder.isDraft(false).isSent(false).build();
        final var element = header.create().child(1);
        assertNotEquals(0, element.getElementsMatchingText(expectedText).size());
      }

      @Test
      void leftMarginInfoSent() {
        final var header = headerBuilder.isDraft(false).isSent(true).build();
        final var element = header.create().child(1);
        assertNotEquals(0, element.getElementsMatchingText(expectedText).size());
      }
    }
  }

}
