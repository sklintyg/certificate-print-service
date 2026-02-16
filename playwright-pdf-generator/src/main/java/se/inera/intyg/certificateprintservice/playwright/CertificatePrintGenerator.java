package se.inera.intyg.certificateprintservice.playwright;

import com.microsoft.playwright.Page.PdfOptions;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.pdfgenerator.PrintCertificateGenerator;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Certificate;
import se.inera.intyg.certificateprintservice.pdfgenerator.event.CertificatePrintEventService;
import se.inera.intyg.certificateprintservice.pdfgenerator.event.model.CertificatePrintEvent;
import se.inera.intyg.certificateprintservice.pdfgenerator.event.model.CertificatePrintEventType;
import se.inera.intyg.certificateprintservice.playwright.browserpool.BrowserPool;
import se.inera.intyg.certificateprintservice.playwright.browserpool.PlaywrightBrowser;
import se.inera.intyg.certificateprintservice.playwright.converters.ContentConverter;
import se.inera.intyg.certificateprintservice.playwright.converters.FooterConverter;
import se.inera.intyg.certificateprintservice.playwright.converters.HeaderConverter;
import se.inera.intyg.certificateprintservice.playwright.document.Document;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificatePrintGenerator implements PrintCertificateGenerator, InitializingBean {

  @Value("classpath:templates/certificateTemplate.html")
  private Resource template;
  @Value("classpath:templates/tailwindCSS.js")
  private Resource tailwindScript;

  private final BrowserPool browserPool;
  private final ContentConverter contentConverter;
  private final FooterConverter footerConverter;
  private final HeaderConverter headerConverter;
  private final CertificatePrintEventService certificatePrintEventService;
  private String tailwindCSS;

  @Override
  public void afterPropertiesSet() throws Exception {
    tailwindCSS = new String(Base64.getEncoder().encode(tailwindScript.getContentAsByteArray()));
  }

  @Override
  public byte[] generate(final Certificate certificate) {
    final var start = LocalDateTime.now(ZoneId.systemDefault());

    PlaywrightBrowser playwrightBrowser = null;
    try {
      playwrightBrowser = browserPool.borrowObject();
      return createPdf(playwrightBrowser, certificate);
    } catch (Exception e) {
      throw new IllegalStateException("Failure creating certificate pdf", e);
    } finally {
      browserPool.returnObject(playwrightBrowser);
      certificatePrintEventService.publish(
          CertificatePrintEvent.builder()
              .start(start)
              .end(LocalDateTime.now(ZoneId.systemDefault()))
              .type(CertificatePrintEventType.CREATED)
              .certificateId(certificate.getMetadata().getCertificateId())
              .build()
      );
    }
  }

  private byte[] createPdf(PlaywrightBrowser playwrightBrowser, Certificate certificate)
      throws IOException {
    try (
        final var context = playwrightBrowser.getBrowserContext();
        final var page = context.newPage();
    ) {
      final var metadata = certificate.getMetadata();
      final var header = headerConverter.convert(metadata).create().html();
      final var footer = footerConverter.convert(metadata).create().html();
      final var headerHeight = headerConverter.headerHeight(page, header);

      final var document = Document.builder()
          .content(contentConverter.convert(certificate))
          .certificateName(metadata.getName())
          .certificateType(metadata.getTypeId())
          .certificateVersion(metadata.getVersion())
          .tailWindScript(tailwindCSS)
          .isDraft(metadata.isDraft())
          .build();

      final var jsoupDocument = document.build(template, headerHeight);
      page.setContent(jsoupDocument.html());
      return page.pdf(pdfOptions(header, footer));
    }
  }

  private PdfOptions pdfOptions(String header, String footer) {
    return new PdfOptions()
        .setFormat("A4")
        .setTagged(true)
        .setPrintBackground(true)
        .setDisplayHeaderFooter(true)
        .setHeaderTemplate(header)
        .setFooterTemplate(footer);
  }

}