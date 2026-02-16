package se.inera.intyg.certificateprintservice.playwright;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.certificateprintservice.pdfgenerator.api.Certificate.builder;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.PdfOptions;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Category;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Certificate;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Metadata;
import se.inera.intyg.certificateprintservice.pdfgenerator.event.CertificatePrintEventService;
import se.inera.intyg.certificateprintservice.playwright.browserpool.BrowserPool;
import se.inera.intyg.certificateprintservice.playwright.browserpool.PlaywrightBrowser;
import se.inera.intyg.certificateprintservice.playwright.converters.ContentConverter;
import se.inera.intyg.certificateprintservice.playwright.converters.FooterConverter;
import se.inera.intyg.certificateprintservice.playwright.converters.HeaderConverter;
import se.inera.intyg.certificateprintservice.playwright.document.Content;
import se.inera.intyg.certificateprintservice.playwright.document.Footer;
import se.inera.intyg.certificateprintservice.playwright.document.Header;
import se.inera.intyg.certificateprintservice.playwright.document.LeftMarginInfo;
import se.inera.intyg.certificateprintservice.playwright.document.Watermark;

@ExtendWith(MockitoExtension.class)
class CertificatePrintGeneratorTest {

  @Mock
  private BrowserPool browserPool;
  @Mock
  private PlaywrightBrowser playwrightBrowser;
  @Mock
  private BrowserContext browserContext;
  @Mock
  private Page page;
  @Mock
  private Locator locator;
  @Mock
  private ContentConverter contentConverter;
  @Mock
  private FooterConverter footerConverter;
  @Mock
  private HeaderConverter headerConverter;
  @Mock
  private CertificatePrintEventService certificatePrintEventService;

  @InjectMocks
  private CertificatePrintGenerator certificatePrintGenerator;

  private static final String CERTIFICATE_NAME = "certificateName";
  private static final String CERTIFICATE_TYPE = "certificateType";
  private static final String CERTIFICATE_VERSION = "certificateVersion";
  private static final byte[] RECIPIENT_LOGO = "recipientLogo".getBytes();
  private static final String PERSON_ID = "personId";
  private static final String RECIPIENT_NAME = "recipientName";
  private static final String CATEGORY_ID = "categoryId";
  private static final String CATEGORY_NAME = "categoryName";
  private static final String ISSUING_UNIT = "issuingUnit";
  private static final String DESCRIPTION = "description";

  final static Certificate CERTIFICATE = builder()
      .metadata(Metadata.builder().build())
      .build();

  private final static List<Category> CATEGORIES = List.of(
      Category.builder()
          .id(CATEGORY_ID)
          .name(CATEGORY_NAME)
          .questions(Collections.emptyList())
          .build());

  private final Content contentBuilder = Content.builder()
      .categories(CATEGORIES)
      .certificateName(CERTIFICATE_NAME)
      .certificateType(CERTIFICATE_TYPE)
      .certificateVersion(CERTIFICATE_VERSION)
      .recipientName(RECIPIENT_NAME)
      .personId(PERSON_ID)
      .issuingUnit(ISSUING_UNIT)
      .issuingUnitInfo(List.of("address", "ZIP_CODE", "city"))
      .certificateName(CERTIFICATE_NAME)
      .description(DESCRIPTION)
      .isDraft(true)
      .build();

  private final Header header = Header.builder()
      .certificateName(CERTIFICATE_NAME)
      .certificateType(CERTIFICATE_TYPE)
      .certificateVersion(CERTIFICATE_VERSION)
      .personId(PERSON_ID)
      .recipientLogo(RECIPIENT_LOGO)
      .recipientName(RECIPIENT_NAME)
      .leftMarginInfo(LeftMarginInfo.builder()
          .certificateType(CERTIFICATE_TYPE).
          recipientName(RECIPIENT_NAME)
          .build())
      .watermark(Watermark.builder().build())
      .isDraft(true)
      .build();

  private static final Resource TEMPLATE = new ClassPathResource("testCertificateTemplate.html");
  private static final Resource TAILWIND = new ClassPathResource("testTailwindScript.js");
  private static final byte[] PDF = "pdf".getBytes();

  @Nested
  class Pdf {

    @BeforeEach
    void setup() throws Exception {
      ReflectionTestUtils.setField(certificatePrintGenerator, "template", TEMPLATE);
      when(browserPool.borrowObject()).thenReturn(playwrightBrowser);
      when(playwrightBrowser.getBrowserContext()).thenReturn(browserContext);
      when(browserContext.newPage()).thenReturn(page);
      when(headerConverter.convert(CERTIFICATE.getMetadata())).thenReturn(header);
      when(contentConverter.convert(CERTIFICATE)).thenReturn(contentBuilder);
      when(footerConverter.convert(CERTIFICATE.getMetadata())).thenReturn(Footer.builder().build());
    }

    @Test
    void shouldReturnPdfAsBytes() {
      when(page.pdf(any(PdfOptions.class))).thenReturn(PDF);
      final var pdf = certificatePrintGenerator.generate(CERTIFICATE);
      verify(page).setContent(anyString());
      verify(page).pdf(any(PdfOptions.class));
      assertEquals(PDF, pdf);
    }

    @Test
    void shouldThrowIllegalStateException() {
      when(page.pdf(any(PdfOptions.class))).thenThrow(IllegalStateException.class);
      assertThrows(IllegalStateException.class,
          () -> certificatePrintGenerator.generate(CERTIFICATE));
    }
  }

  @Test
  void shouldProcessTailwindScriptSuccessfully() {
    ReflectionTestUtils.setField(certificatePrintGenerator, "tailwindScript", TAILWIND);
    assertDoesNotThrow(() -> certificatePrintGenerator.afterPropertiesSet());
  }

}
