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
package se.inera.intyg.certificateprintservice.pdfbox.acroform.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.TAGGED_PDF_RESOURCE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.FontResolver;

/**
 * Pins {@link FieldTextLayout} to PDFBox's actual rendering.
 *
 * <p>The whole point of {@link FieldTextLayout} is that its line count equals the number of lines
 * PDFBox will draw. If the two ever drift apart, values will be truncated too eagerly or, worse,
 * left to spill outside the field. So rather than asserting hand-computed numbers, these tests fill
 * a real template field, let PDFBox generate the appearance stream, and count the lines it actually
 * produced.
 */
class FieldTextLayoutMatchesPdfBoxTest {

  private static final String MULTILINE_FIELD =
      "form1[0].#subform[0].flt_txtVardgivarensNamnAdressTelefon[0]";
  private static final String SHORT_MULTILINE_FIELD = "form1[0].#subform[0].flt_txtBefattning[0]";

  private final FieldTextLayout textLayout = new FieldTextLayout();
  private final FieldGeometryResolver resolver = new FieldGeometryResolver();

  private PDDocument document;

  @BeforeEach
  void setUp() throws IOException {
    try (final var stream = getClass().getResourceAsStream(TAGGED_PDF_RESOURCE)) {
      document = Loader.loadPDF(stream.readAllBytes());
    }
  }

  @AfterEach
  void tearDown() throws IOException {
    document.close();
  }

  @Test
  @DisplayName("line count matches PDFBox as a value grows word by word")
  void shallMatchPdfBoxForGrowingValues() throws IOException {
    for (var words = 1; words <= 60; words++) {
      assertMatchesPdfBox(MULTILINE_FIELD, "word ".repeat(words).trim());
    }
  }

  @Test
  @DisplayName("line count matches PDFBox in a field only one line tall")
  void shallMatchPdfBoxForShortField() throws IOException {
    for (var words = 1; words <= 30; words++) {
      assertMatchesPdfBox(SHORT_MULTILINE_FIELD, "word ".repeat(words).trim());
    }
  }

  @Test
  @DisplayName("line count matches PDFBox for text that breaks in awkward places")
  void shallMatchPdfBoxForAwkwardText() throws IOException {
    final var values =
        List.of(
            "hyphen-ated-words-everywhere-that-should-break-at-hyphens-and-more-of-them-here",
            "Supercalifragilisticexpialidocious".repeat(6),
            "Line one\nLine two\nLine three",
            "Paragraph one is quite long and wraps around\n\nParagraph two after a blank line",
            "Ett läkarintyg för sjukpenning med svenska tecken åäö ÅÄÖ och mera text här",
            "trailing spaces at end          ",
            "em—dash—separated—text—that—keeps—going—and—going—and—going—for—a—while—yes",
            "tab\tseparated\tvalues\tthat\tgo\ton\tand\ton\tand\ton\tfor\tquite\ta\twhile",
            "a b c d e f g h i j k l m n o p q r s t u v w x y z a b c d e f g h i j k l");

    for (final var value : values) {
      assertMatchesPdfBox(MULTILINE_FIELD, value);
    }
  }

  @Test
  @DisplayName("line count matches PDFBox for randomly generated hyphen-heavy text")
  void shallMatchPdfBoxForRandomText() throws IOException {
    final var random = new Random(42);
    final var alphabet = "abcdefgh-  ijklmnop-qrst".toCharArray();

    for (var n = 0; n < 300; n++) {
      final var value = new StringBuilder();
      final var length = 5 + random.nextInt(160);
      for (var c = 0; c < length; c++) {
        value.append(alphabet[random.nextInt(alphabet.length)]);
      }
      assertMatchesPdfBox(MULTILINE_FIELD, value.toString().trim());
    }
  }

  private void assertMatchesPdfBox(String fieldId, String value) throws IOException {
    final var field = (PDTextField) document.getDocumentCatalog().getAcroForm().getField(fieldId);
    field.setValue(value);

    final var geometry = resolver.resolve(field, FontResolver.getFont(field)).orElseThrow();

    assertEquals(
        renderedLines(field),
        textLayout.countLines(value, geometry),
        () -> "Line count disagreed with PDFBox for value: " + value);
  }

  /**
   * PDFBox emits a {@code Td} per line and another per word gap; only the line advances move
   * vertically, so the number of vertically-moving {@code Td} operations is the number of lines.
   */
  private int renderedLines(PDTextField field) throws IOException {
    final var appearance =
        field.getWidgets().getFirst().getAppearance().getNormalAppearance().getAppearanceStream();

    final var operands = new ArrayList<COSBase>();
    var advances = 0;

    for (final var token : new PDFStreamParser(appearance).parse()) {
      if (token instanceof Operator operator) {
        if (isVerticalTextAdvance(operator, operands)) {
          advances++;
        }
        operands.clear();
      } else {
        operands.add((COSBase) token);
      }
    }

    return Math.max(1, advances);
  }

  private boolean isVerticalTextAdvance(Operator operator, List<COSBase> operands) {
    return "Td".equals(operator.getName())
        && operands.size() == 2
        && operands.get(1) instanceof COSNumber verticalOffset
        && Float.compare(verticalOffset.floatValue(), 0f) != 0;
  }
}
