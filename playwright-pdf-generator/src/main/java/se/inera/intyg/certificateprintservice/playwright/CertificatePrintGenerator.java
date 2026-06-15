/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import se.inera.intyg.certificateprintservice.pdfgenerator.api.event.CertificatePrintEventPublisher;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.event.model.CertificatePrintEvent;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.event.model.CertificatePrintEventType;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.PrintCertificateGenerator;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.Certificate;
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
  private final CertificatePrintEventPublisher certificatePrintEventPublisher;
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
      certificatePrintEventPublisher.publish(
          CertificatePrintEvent.builder()
              .start(start)
              .end(LocalDateTime.now(ZoneId.systemDefault()))
              .type(CertificatePrintEventType.CREATED)
              .certificateId(certificate.getMetadata().getCertificateId())
              .build());
    }
  }

  private byte[] createPdf(PlaywrightBrowser playwrightBrowser, Certificate certificate)
      throws IOException {
    try (final var context = playwrightBrowser.getBrowserContext();
        final var page = context.newPage(); ) {
      final var metadata = certificate.getMetadata();
      final var header = headerConverter.convert(metadata).create().html();
      final var footer = footerConverter.convert(metadata).create().html();
      final var headerHeight = headerConverter.headerHeight(page, header);

      final var document =
          Document.builder()
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
