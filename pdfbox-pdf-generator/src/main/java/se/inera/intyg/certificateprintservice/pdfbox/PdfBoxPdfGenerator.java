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
package se.inera.intyg.certificateprintservice.pdfbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.AcroFormFiller;
import se.inera.intyg.certificateprintservice.pdfbox.overlay.OverlayTextService;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.CustomPdfGenerator;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdf;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfBoxPdfGenerator implements CustomPdfGenerator {

  private final AcroFormFiller acroFormFiller;
  private final OverlayTextService overlayTextService;

  @Override
  public byte[] get(CustomPdf customPdf) {
    final var template = customPdf.template();
    if (template == null || template.length == 0) {
      throw new IllegalArgumentException("PDF template bytes must not be null or empty");
    }

    try (final var document = Loader.loadPDF(template)) {
      document.setAllSecurityToBeRemoved(true);
      acroFormFiller.fill(
          document,
          customPdf.fields(),
          customPdf.metadata().overflowPageIndex(),
          customPdf.metadata().personIdConfig());
      overlayTextService.drawOverlays(document, customPdf.metadata());
      document
          .getDocumentInformation()
          .setTitle(customPdf.metadata().accessibilityMetadata().title());
      flattenAcroForm(document);
      return toBytes(document);
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to load or process PDF template", e);
    }
  }

  private void flattenAcroForm(PDDocument document) throws IOException {
    final var acroForm = document.getDocumentCatalog().getAcroForm();
    if (acroForm != null) {
      acroForm.flatten();
    }
  }

  private byte[] toBytes(PDDocument document) throws IOException {
    try (final var out = new ByteArrayOutputStream()) {
      document.save(out);
      return Base64.getEncoder().encode(out.toByteArray());
    }
  }
}
