package se.inera.intyg.certificateprintservice.playwright.document;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.ATTRIBUTES;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.BR;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.CLASS;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.DIV;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.H1;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.H2;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.H3;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.NUM_ATTRIBUTES;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.NUM_CHILDREN;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.P;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STRONG;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TAG_TYPE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TEXT;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.attributes;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.attributesSize;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Category;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Question;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueText;

class ContentTest {

  private static final String CATEGORY_ID = "categoryId";
  private static final String CATEGORY_NAME = "categoryName";
  private static final String QUESTION_ID = "questionId";
  private static final String QUESTION_NAME = "questionName";
  private static final String QUESTION_VALUE = "questionValue";
  private static final String SUB_QUESTION_ID = "subQuestionId";
  private static final String SUB_QUESTION_NAME = "subQuestionName";
  private static final String SUB_QUESTION_VALUE = "subQuestionValue";
  private static final String CERTIFICATE_TYPE = "certificateType";
  private static final String CERTIFICATE_VERSION = "certificateVersion";
  private static final String RECIPIENT_NAME = "recipientName";
  private static final String PERSON_ID = "personId";
  private static final String ISSUER_NAME = "issuerName";
  private static final String ISSUING_UNIT = "issuingUnit";
  private static final String ADDRESS = "address";
  private static final String ZIP_CODE = "zipCode";
  private static final String CITY = "city";
  private static final String CERTIFICATE_NAME = "certificateName";
  private static final String DESCRIPTION = "description";
  private static final String CONTACT_INFO_HEADER = "Kontaktuppgifter:";
  private static final String SIGN_DATE = "2025-01-20";
  private static final String ISSUER_NAME_HEADER = "Intygsutfärdare:";
  private static final String SIGN_DATE_HEADER = "Intyget signerades:";
  private static final String SKICKA_INTYG_TILL_MOTTAGARE = "Skicka intyg till mottagare";
  private static final String KAN_EJ_SKICKA_INTYG_TILL_MOTTAGARE = "Hantera intyg";
  private static final String INFO_1177 = "Du kan hantera ditt intyg genom att logga in på 1177.se Där kan du till exempel skicka intyget till mottagaren";
  private static final String KAN_EJ_SKICKA_INFO_1177 = "Du kan hantera ditt intyg genom att logga in på 1177.se";
  private static final String DRAFT_ALERT_MESSAGE = "Detta är en utskrift av ett elektroniskt intygsutkast och ska INTE skickas till %s.";
  private static final String SENT_ALERT_MESSAGE = "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren. Notera att intyget redan har skickats till %s.";
  private static final String SIGNED_ALERT_MESSAGE = "Detta är en utskrift av ett elektroniskt intyg. Intyget har signerats elektroniskt av intygsutfärdaren.";
  private static final String DRAFT_ALERT_TEXT = "arbetsgivaren";

  private static final List<Question> SUB_QUESTIONS = List.of(Question.builder()
      .id(SUB_QUESTION_ID)
      .name(SUB_QUESTION_NAME)
      .subQuestions(Collections.emptyList())
      .value(ElementValueText.builder()
          .text(SUB_QUESTION_VALUE)
          .build())
      .build());

  private static final List<Question> QUESTIONS = List.of(
      Question.builder()
          .id(QUESTION_ID)
          .name(QUESTION_NAME)
          .value(ElementValueText.builder()
              .text(QUESTION_VALUE)
              .build())
          .subQuestions(SUB_QUESTIONS)
          .build());

  private final static List<Category> CATEGORIES = List.of(
      Category.builder()
          .id(CATEGORY_ID)
          .name(CATEGORY_NAME)
          .questions(QUESTIONS)
          .build());


  private final Content.ContentBuilder contentBuilder = Content.builder()
      .categories(CATEGORIES)
      .certificateName(CERTIFICATE_NAME)
      .certificateType(CERTIFICATE_TYPE)
      .certificateVersion(CERTIFICATE_VERSION)
      .recipientName(RECIPIENT_NAME)
      .personId(PERSON_ID)
      .issuerName(ISSUER_NAME)
      .issuingUnit(ISSUING_UNIT)
      .issuingUnitInfo(List.of(ADDRESS, ZIP_CODE, CITY))
      .certificateName(CERTIFICATE_NAME)
      .draftAlertInfoText(DRAFT_ALERT_TEXT)
      .description(DESCRIPTION);

  @Nested
  class ContentWrapper {

    @Test
    void contentWrapper() {
      final var content = contentBuilder.signDate(null).isDraft(true).isSent(false).build();
      final var element = content.create();
      assertAll(
          () -> assertEquals(DIV, element.tag(), TAG_TYPE),
          () -> assertEquals(4, element.children().size(), NUM_CHILDREN),
          () -> assertEquals(0, attributesSize(element), NUM_ATTRIBUTES)
      );
    }

    @Nested
    class HiddenAccessibleHeader {

      @Test
      void hiddenAccessibleHeaderWrapper() {
        final var content = contentBuilder.signDate(null).isDraft(true).isSent(false).build();
        final var element = content.create().child(0);
        assertAll(
            () -> assertEquals(DIV, element.tag(), TAG_TYPE),
            () -> assertEquals(3, element.children().size(), NUM_CHILDREN),
            () -> assertEquals(0, attributesSize(element), NUM_ATTRIBUTES)
        );
      }

      @Nested
      class HiddenAccessibleHeaderElements {

        @Test
        void personIdText() {
          final var content = contentBuilder.signDate(null).isDraft(true).isSent(false).build();
          final var element = content.create().child(0).child(0);
          assertAll(
              () -> assertEquals(P, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals("Person- /samordningsnr personId", element.text(), TEXT),
              () -> assertEquals("absolute h-px w-[17cm] text-[1px] -z-50 text-white",
                  attributes(element, CLASS), ATTRIBUTES)
          );
        }

        @Test
        void certificateInfoText() {
          final var content = contentBuilder.signDate(null).isDraft(true).isSent(false).build();
          final var element = content.create().child(0).child(1);
          assertAll(
              () -> assertEquals(H1, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals(
                  "%s".formatted(CERTIFICATE_TYPE),
                  element.text(), TEXT),
              () -> assertEquals("absolute h-px w-[17cm] text-[1px] -z-50 text-white",
                  attributes(element, CLASS), ATTRIBUTES)
          );
        }

        @Test
        void alertTextDraftIfCanSendElectronicllyIsTrue() {
          final var content = contentBuilder.signDate(null).isDraft(true).isSent(false)
              .isCanSendElectronically(true).build();
          final var element = content.create().child(0).child(2);
          assertAll(
              () -> assertEquals(P, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals(DRAFT_ALERT_MESSAGE.formatted(RECIPIENT_NAME), element.text(),
                  TEXT),
              () -> assertEquals("absolute h-px w-[17cm] text-[1px] -z-50 text-white",
                  attributes(element, CLASS), ATTRIBUTES)
          );
        }

        @Test
        void alertTextDraftIfCanSendElectronicllyIsFalse() {
          final var content = contentBuilder.signDate(null).isDraft(true).isSent(false)
              .isCanSendElectronically(false).build();
          final var element = content.create().child(0).child(2);
          assertAll(
              () -> assertEquals(P, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals(DRAFT_ALERT_MESSAGE.formatted("arbetsgivaren"), element.text(),
                  TEXT),
              () -> assertEquals("absolute h-px w-[17cm] text-[1px] -z-50 text-white",
                  attributes(element, CLASS), ATTRIBUTES)
          );
        }

        @Test
        void alertTextSigned() {
          final var content = contentBuilder.signDate(SIGN_DATE).isDraft(false).isSent(false)
              .isCanSendElectronically(true)
              .build();
          final var element = content.create().child(0).child(2);
          assertAll(
              () -> assertEquals(P, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals(SIGNED_ALERT_MESSAGE, element.text(), TEXT),
              () -> assertEquals("absolute h-px w-[17cm] text-[1px] -z-50 text-white",
                  attributes(element, CLASS), ATTRIBUTES));
        }

        @Test
        void alertTextSent() {
          final var content = contentBuilder.signDate(SIGN_DATE).isDraft(false).isSent(true)
              .isCanSendElectronically(true)
              .build();
          final var element = content.create().child(0).child(2);
          assertAll(
              () -> assertEquals(P, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals(SENT_ALERT_MESSAGE.formatted(RECIPIENT_NAME), element.text(),
                  TEXT),
              () -> assertEquals("absolute h-px w-[17cm] text-[1px] -z-50 text-white",
                  attributes(element, CLASS), ATTRIBUTES));
        }
      }
    }

    @Nested
    class Categories {

      @Test
      void categoriesWrapper() {
        final var content = contentBuilder.isDraft(true).signDate(null).isSent(false).build();
        final var element = content.create().child(1);
        assertAll(
            () -> assertEquals(DIV, element.tag(), TAG_TYPE),
            () -> assertEquals(5, element.children().size(), NUM_CHILDREN),
            () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
            () -> assertEquals(
                "box-decoration-clone border border-solid border-black mb-[5mm] pb-[3mm]",
                attributes(element, CLASS), ATTRIBUTES));
      }

      @Nested
      class CategoriesElements {

        @Test
        void categoryName() {
          final var content = contentBuilder.isDraft(true).signDate(null).isSent(false).build();
          final var element = content.create().child(1).child(0);
          assertAll(
              () -> assertEquals(H2, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals(CATEGORY_NAME, element.text(), TEXT),
              () -> assertEquals(
                  "text-base font-bold uppercase border-b border-black border-solid px-[5mm]",
                  attributes(element, CLASS), ATTRIBUTES));
        }

        @Test
        void questionName() {
          final var content = contentBuilder.isDraft(true).signDate(null).isSent(false).build();
          final var element = content.create().child(1).child(1);
          assertAll(
              () -> assertEquals(H3, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals(QUESTION_NAME, element.text(), TEXT),
              () -> assertEquals("text-sm font-bold pt-[1mm] px-[5mm]", attributes(element, CLASS),
                  ATTRIBUTES)
          );
        }

        @Test
        void questionValue() {
          final var content = contentBuilder.isDraft(true).signDate(null).isSent(false).build();
          final var element = content.create().child(1).child(2);
          assertAll(
              () -> assertEquals(P, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals(QUESTION_VALUE, element.text(), TEXT),
              () -> assertEquals("text-sm italic px-[5mm]", attributes(element, CLASS),
                  ATTRIBUTES)
          );
        }

        @Test
        void subQuestionName() {
          final var content = contentBuilder.isDraft(true).signDate(null).isSent(false).build();
          final var element = content.create().child(1).child(3);
          assertAll(
              () -> assertEquals(H3, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals(SUB_QUESTION_NAME, element.text(), TEXT),
              () -> assertEquals("text-sm font-bold pt-[1mm] px-[5mm] text-neutral-600",
                  attributes(element, CLASS), ATTRIBUTES)
          );
        }

        @Test
        void subQuestionValue() {
          final var content = contentBuilder.isDraft(true).signDate(null).isSent(false).build();
          final var element = content.create().child(1).child(4);
          assertAll(
              () -> assertEquals(P, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), NUM_ATTRIBUTES),
              () -> assertEquals(SUB_QUESTION_VALUE, element.text(), TEXT),
              () -> assertEquals("text-sm italic px-[5mm]", attributes(element, CLASS), ATTRIBUTES)
          );
        }
      }
    }

    @Nested
    class IssuerInfo {

      @Test
      void issuerInfoWrapperDraft() {
        final var content = contentBuilder.isDraft(true).signDate(null).build();
        final var element = content.create().child(2);
        assertAll(
            () -> assertEquals(DIV, element.tag(), TAG_TYPE),
            () -> assertEquals(1, element.children().size(), NUM_CHILDREN),
            () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
            () -> assertEquals("break-inside-avoid", attributes(element, CLASS), ATTRIBUTES)
        );
      }

      @Test
      void issuerInfoWrapperSigned() {
        final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
        final var element = content.create().child(2);
        assertAll(
            () -> assertEquals(DIV, element.tag(), TAG_TYPE),
            () -> assertEquals(3, element.children().size(), NUM_CHILDREN),
            () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
            () -> assertEquals("break-inside-avoid", attributes(element, CLASS), ATTRIBUTES)
        );
      }

      @Nested
      class IssuerInfoWrappers {

        @Test
        void issuerInfoDraft() {
          final var content = contentBuilder.isDraft(true).signDate(null).build();
          final var element = content.create().child(2).child(0);
          assertAll(
              () -> assertEquals(DIV, element.tag(), TAG_TYPE),
              () -> assertEquals(5, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
              () -> assertEquals("grid mt-[5mm]", attributes(element, CLASS), ATTRIBUTES)
          );
        }

        @Test
        void issuerInfoSignedIssuerName() {
          final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
          final var element = content.create().child(2).child(0);
          assertAll(
              () -> assertEquals(DIV, element.tag(), TAG_TYPE),
              () -> assertEquals(2, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
              () -> assertEquals("grid mt-[5mm]", attributes(element, CLASS), ATTRIBUTES)
          );
        }

        @Test
        void issuerInfoSignedContactInfo() {
          final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
          final var element = content.create().child(2).child(1);
          assertAll(
              () -> assertEquals(DIV, element.tag(), TAG_TYPE),
              () -> assertEquals(5, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
              () -> assertEquals("grid mt-[5mm]", attributes(element, CLASS), ATTRIBUTES)
          );
        }

        @Test
        void issuerInfoSignedSignDate() {
          final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
          final var element = content.create().child(2).child(2);
          assertAll(
              () -> assertEquals(DIV, element.tag(), TAG_TYPE),
              () -> assertEquals(2, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
              () -> assertEquals("grid mt-[5mm]", attributes(element, CLASS), ATTRIBUTES)
          );
        }

        @Nested
        class IssuerName {

          @Test
          void issuerNameHeader() {
            final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
            final var element = content.create().child(2).child(0).child(0);
            assertAll(
                () -> assertEquals(P, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
                () -> assertEquals(ISSUER_NAME_HEADER, element.text(), TEXT),
                () -> assertEquals("font-bold", attributes(element, CLASS), ATTRIBUTES)
            );
          }

          @Test
          void issuerName() {
            final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
            final var element = content.create().child(2).child(0).child(1);
            assertAll(
                () -> assertEquals(P, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(0, attributesSize(element), ATTRIBUTES),
                () -> assertEquals(ISSUER_NAME, element.text(), TEXT)
            );
          }
        }

        @Nested
        class ContactInfo {

          @Test
          void contactInfoHeader() {
            final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
            final var element = content.create().child(2).child(1).child(0);
            assertAll(
                () -> assertEquals(P, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
                () -> assertEquals(CONTACT_INFO_HEADER, element.text(), TEXT),
                () -> assertEquals("font-bold", attributes(element, CLASS), ATTRIBUTES)
            );
          }

          @Test
          void issuingUnit() {
            final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
            final var element = content.create().child(2).child(1).child(1);
            assertAll(
                () -> assertEquals(P, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(0, attributesSize(element), ATTRIBUTES),
                () -> assertEquals(ISSUING_UNIT, element.text(), TEXT)
            );
          }

          @Test
          void address() {
            final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
            final var element = content.create().child(2).child(1).child(2);
            assertAll(
                () -> assertEquals(P, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(0, attributesSize(element), ATTRIBUTES),
                () -> assertEquals(ADDRESS, element.text(), TEXT)
            );
          }

          @Test
          void zipCode() {
            final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
            final var element = content.create().child(2).child(1).child(3);
            assertAll(
                () -> assertEquals(P, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(0, attributesSize(element), ATTRIBUTES),
                () -> assertEquals(ZIP_CODE, element.text(), TEXT)
            );
          }

          @Test
          void city() {
            final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
            final var element = content.create().child(2).child(1).child(4);
            assertAll(
                () -> assertEquals(P, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(0, attributesSize(element), ATTRIBUTES),
                () -> assertEquals(CITY, element.text(), TEXT)
            );
          }
        }

        @Nested
        class SignDate {

          @Test
          void signDateHeader() {
            final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
            final var element = content.create().child(2).child(2).child(0);
            assertAll(
                () -> assertEquals(P, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
                () -> assertEquals(SIGN_DATE_HEADER, element.text(), TEXT),
                () -> assertEquals("font-bold", attributes(element, CLASS), ATTRIBUTES)
            );
          }

          @Test
          void signDate() {
            final var content = contentBuilder.isDraft(false).signDate(SIGN_DATE).build();
            final var element = content.create().child(2).child(2).child(1);
            assertAll(
                () -> assertEquals(P, element.tag(), TAG_TYPE),
                () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
                () -> assertEquals(0, attributesSize(element), ATTRIBUTES),
                () -> assertEquals(SIGN_DATE, element.text(), TEXT)
            );
          }
        }
      }
    }

    @Nested
    class CertificateInfo {

      @Test
      void certificateInfoInfoWrapper() {
        final var content = contentBuilder.isDraft(true).signDate(null).build();
        final var element = content.create().child(3);
        assertAll(
            () -> assertEquals(DIV, element.tag(), TAG_TYPE),
            () -> assertEquals(5, element.children().size(), NUM_CHILDREN),
            () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
            () -> assertEquals("break-before-page", attributes(element, CLASS), ATTRIBUTES)
        );
      }

      @Nested
      class CertificateInfoElements {

        @Test
        void certificateName() {
          final var content = contentBuilder.isDraft(true).signDate(null).build();
          final var element = content.create().child(3).child(0);
          assertAll(
              () -> assertEquals(STRONG, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(0, attributesSize(element), ATTRIBUTES),
              () -> assertEquals(CERTIFICATE_NAME, element.text(), TEXT)
          );
        }

        @Test
        void description() {
          final var content = contentBuilder.isDraft(true).signDate(null).build();
          final var element = content.create().child(3).child(1);
          assertAll(
              () -> assertEquals(DIV, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
              () -> assertEquals(DESCRIPTION, element.text(), TEXT),
              () -> assertEquals("whitespace-pre-line", attributes(element, CLASS), ATTRIBUTES)
          );
        }

        @Test
        void shouldParseDescriptionWithLinksLineBreaksAndLists() {
          final var description =
              "This is a description with a <A href=\"http://www.test.test\">link</A>."
                  + "<BR>And a new line."
                  + "<LINK:some-link-id>" + "<ul><li>List item 1</li><li>List item 2</li></ul>";
          final var content = contentBuilder.description(description).isDraft(false)
              .signDate(SIGN_DATE).build();
          final var element = content.create().child(3).child(1);

          assertAll(
              () -> assertEquals(DIV, element.tag(), TAG_TYPE),
              () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
              () -> assertEquals("whitespace-pre-line", attributes(element, CLASS), ATTRIBUTES),
              () -> assertEquals(
                  "This is a description with a link. And a new line. List item 1 List item 2",
                  element.text(),
                  "Text content should have LINK tag removed and include list items"),
              () -> assertEquals(1, element.select("a").size(), "Should have one anchor tag"),
              () -> assertEquals("link", element.select("a").first().text(), "Anchor text"),
              () -> assertEquals("http://www.test.test", element.select("a").first().attr("href"),
                  "Anchor href"),
              () -> assertEquals(1, element.select("br").size(), "Should have one br tag"),
              () -> assertEquals(1, element.select("ul").size(), "Should have one ul tag"),
              () -> assertEquals("list-disc pl-5", element.select("ul").first().attr("class"),
                  "UL should have list-disc pl-5 classes"),
              () -> assertEquals(2, element.select("li").size(), "Should have two li tags"),
              () -> assertEquals("List item 1", element.select("li").get(0).text(),
                  "First li text"),
              () -> assertEquals("List item 2", element.select("li").get(1).text(),
                  "Second li text")
          );
        }

        @Test
        void emptyLine() {
          final var content = contentBuilder.isDraft(true).signDate(null).build();
          final var element = content.create().child(3).child(2);
          assertAll(
              () -> assertEquals(BR, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(0, attributesSize(element), ATTRIBUTES)
          );
        }

        @Test
        void sendCertificateToRecipientText() {
          final var content = contentBuilder.isDraft(true).signDate(null)
              .isCanSendElectronically(true).build();
          final var element = content.create().child(3).child(3);
          assertAll(
              () -> assertEquals(STRONG, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(0, attributesSize(element), ATTRIBUTES),
              () -> assertEquals(SKICKA_INTYG_TILL_MOTTAGARE, element.text(), TEXT)
          );
        }

        @Test
        void cannotSendCertificateToRecipientText() {
          final var content = contentBuilder.isDraft(true).signDate(null)
              .isCanSendElectronically(false).build();
          final var element = content.create().child(3).child(3);
          assertAll(
              () -> assertEquals(STRONG, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(0, attributesSize(element), ATTRIBUTES),
              () -> assertEquals(KAN_EJ_SKICKA_INTYG_TILL_MOTTAGARE, element.text(), TEXT)
          );
        }

        @Test
        void info1177() {
          final var content = contentBuilder.isDraft(true).signDate(null)
              .isCanSendElectronically(true).build();
          final var element = content.create().child(3).child(4);
          assertAll(
              () -> assertEquals(P, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
              () -> assertEquals(INFO_1177, element.text(), TEXT),
              () -> assertEquals("whitespace-pre-line", attributes(element, CLASS), ATTRIBUTES)
          );
        }

        @Test
        void cannotSendInfo1177() {
          final var content = contentBuilder.isDraft(true).signDate(null)
              .isCanSendElectronically(true).build();
          final var element = content.create().child(3).child(4);
          assertAll(
              () -> assertEquals(P, element.tag(), TAG_TYPE),
              () -> assertEquals(0, element.children().size(), NUM_CHILDREN),
              () -> assertEquals(1, attributesSize(element), ATTRIBUTES),
              () -> assertEquals(INFO_1177, element.text(), TEXT),
              () -> assertEquals("whitespace-pre-line", attributes(element, CLASS), ATTRIBUTES)
          );
        }
      }
    }
  }

}
