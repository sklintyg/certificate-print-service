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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class PdfTestResultHelper {

  private static final Logger log = Logger.getLogger(PdfTestResultHelper.class.getName());

  private static final String OUTPUT_DIR = "build/integration-test-output";
  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

  private PdfTestResultHelper() {
    throw new IllegalStateException("Utility class");
  }

  public static Path savePdf(byte[] pdfData, String testName) {
    try {
      final var outputDir = Paths.get(OUTPUT_DIR);
      Files.createDirectories(outputDir);

      final var timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
      final var fileName = testName + "_" + timestamp + ".pdf";
      final var outputPath = outputDir.resolve(fileName);

      Files.write(outputPath, pdfData);
      log.info("Integration test PDF saved to: " + outputPath.toAbsolutePath());
      return outputPath;
    } catch (IOException e) {
      log.warning("Failed to save integration test PDF for '" + testName + "': " + e.getMessage());
      return null;
    }
  }
}
