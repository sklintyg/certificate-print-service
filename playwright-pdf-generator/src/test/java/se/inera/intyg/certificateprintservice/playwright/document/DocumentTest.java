package se.inera.intyg.certificateprintservice.playwright.document;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.CONTENT;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.SCRIPT;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.SRC;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.STYLE;
import static se.inera.intyg.certificateprintservice.playwright.document.Constants.TITLE;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@ExtendWith(MockitoExtension.class)
class DocumentTest {

  private static final String TAILWIND_CSS_SCRIPT = "tailwindCssScript";
  private static final String CERTIFICATE_NAME = "certificateName";
  private static final String CERTIFICATE_TYPE = "certificateType";
  private static final String CERTIFICATE_VERSION = "certificateVersion";
  private static final String TEST_TEMPLATE_FILENAME = "testCertificateTemplate.html";
  private static final int HEADER_HEIGHT = 77;

  private final Document.DocumentBuilder documentBuilder = Document.builder()
      .content(Content.builder()
          .issuerName("issuerName")
          .issuingUnit("issuingUnit")
          .issuingUnitInfo(List.of("address", "zipCode", "city"))
          .certificateName(CERTIFICATE_NAME)
          .description("description")
          .categories(Collections.emptyList())
          .isDraft(true)
          .build())
      .certificateName(CERTIFICATE_NAME)
      .certificateType(CERTIFICATE_TYPE)
      .certificateVersion(CERTIFICATE_VERSION)
      .tailWindScript(TAILWIND_CSS_SCRIPT);

  private static final Resource template = new ClassPathResource(TEST_TEMPLATE_FILENAME);
  private static org.jsoup.nodes.Document jsoupDocument;
  private Document document;

  @BeforeAll
  static void init() throws IOException {
    jsoupDocument = Jsoup.parse(template.getInputStream(), StandardCharsets.UTF_8.name(),
        "", Parser.xmlParser());
  }

  @Nested
  class Draft {

    @BeforeEach
    void setupDocument() {
      document = documentBuilder.isDraft(true).build();
    }

    @Test
    void shouldSetPageStyle() throws IOException {
      final var elementBefore = jsoupDocument.getElementById(STYLE);
      final var elementAfter = document.build(template, HEADER_HEIGHT).getElementById(STYLE);
      assertEquals("", requireNonNull(elementBefore).text());
      assertEquals("@page { margin: calc(%spx + 16mm) 20mm 39mm 20mm; }".formatted(HEADER_HEIGHT),
          requireNonNull(elementAfter).text());
    }

    @Test
    void shouldSetContent() throws IOException {
      final var elementBefore = jsoupDocument.getElementById(CONTENT);
      final var elementAfter = document.build(template, HEADER_HEIGHT).getElementById(CONTENT);
      assertEquals(0, requireNonNull(elementBefore).children().size());
      assertNotEquals(0, requireNonNull(elementAfter).children().size());
    }

    @Test
    void shouldSetTitle() throws IOException {
      final var elementBefore = jsoupDocument.getElementById(TITLE);
      final var elementAfter = document.build(template, HEADER_HEIGHT).getElementById(TITLE);
      assertEquals("", requireNonNull(elementBefore).text());
      assertEquals("%s (%s v%s)".formatted(CERTIFICATE_NAME, CERTIFICATE_TYPE, CERTIFICATE_VERSION),
          requireNonNull(elementAfter).text());
    }

    @Test
    void shouldSetTailwindScript() throws IOException {
      final var elementBefore = jsoupDocument.getElementById(SCRIPT);
      final var elementAfter = document.build(template, HEADER_HEIGHT).getElementById(SCRIPT);
      assertNull(requireNonNull(elementBefore).attribute(SRC));
      assertEquals("data:text/javascript;base64, %s".formatted(TAILWIND_CSS_SCRIPT), requireNonNull(
          requireNonNull(elementAfter).attribute(SRC)).getValue());
    }
  }

  @Nested
  class Signed {

    @BeforeEach
    void setupDocument() {
      document = documentBuilder.isDraft(false).build();
    }

    @Test
    void shouldSetPageStyle() throws IOException {
      final var elementBefore = jsoupDocument.getElementById(STYLE);
      final var elementAfter = document.build(template, HEADER_HEIGHT).getElementById(STYLE);
      assertEquals("", requireNonNull(elementBefore).text());
      assertEquals("@page { margin: calc(%spx + 16mm) 20mm 39mm 20mm; }".formatted(HEADER_HEIGHT),
          requireNonNull(elementAfter).text());
    }

    @Test
    void shouldSetContent() throws IOException {
      final var elementBefore = jsoupDocument.getElementById(CONTENT);
      final var elementAfter = document.build(template, HEADER_HEIGHT).getElementById(CONTENT);
      assertEquals(0, requireNonNull(elementBefore).children().size());
      assertNotEquals(0, requireNonNull(elementAfter).children().size());
    }


    @Test
    void shouldSetTitle() throws IOException {
      final var elementBefore = jsoupDocument.getElementById(TITLE);
      final var elementAfter = document.build(template, HEADER_HEIGHT).getElementById(TITLE);
      assertEquals("", requireNonNull(elementBefore).text());
      assertEquals("%s (%s v%s)".formatted(CERTIFICATE_NAME, CERTIFICATE_TYPE, CERTIFICATE_VERSION),
          requireNonNull(elementAfter).text());
    }

    @Test
    void shouldSetTailwindScript() throws IOException {
      final var elementBefore = jsoupDocument.getElementById(SCRIPT);
      final var elementAfter = document.build(template, HEADER_HEIGHT).getElementById(SCRIPT);
      assertNull(requireNonNull(elementBefore).attribute(SRC));
      assertEquals("data:text/javascript;base64, %s".formatted(TAILWIND_CSS_SCRIPT), requireNonNull(
          requireNonNull(elementAfter).attribute(SRC)).getValue());
    }
  }

}
