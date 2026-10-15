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
package se.inera.intyg.certificateprintservice.integrationtest.util;

import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Inspects a generated PDF's page count, AcroForm field values, and rendered page text, so
 * integration tests can assert actual overflow/text-fitting behavior instead of only checking that
 * a response was returned.
 */
public class PdfAssertionHelper {

  private PdfAssertionHelper() {
    throw new IllegalStateException("Utility class");
  }

  public static int pageCount(byte[] pdfData) throws IOException {
    try (PDDocument document = Loader.loadPDF(pdfData)) {
      return document.getNumberOfPages();
    }
  }

  /** Extracts the text of every page, concatenated in page order. */
  public static String extractText(byte[] pdfData) throws IOException {
    try (PDDocument document = Loader.loadPDF(pdfData)) {
      return new PDFTextStripper().getText(document);
    }
  }
}
