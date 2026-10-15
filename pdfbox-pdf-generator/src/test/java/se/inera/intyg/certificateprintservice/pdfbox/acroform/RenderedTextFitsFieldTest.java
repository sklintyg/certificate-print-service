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
package se.inera.intyg.certificateprintservice.pdfbox.acroform;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.TAGGED_PDF_RESOURCE;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7210Fields.fk7210Fields;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7804Fields.TAGGED_PDF_OVERFLOW_RESOURCE;
import static se.inera.intyg.certificateprintservice.pdfbox.testdata.TestDataFK7804Fields.fk7804Fields;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPagePaginator;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPageRenderer;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPageStructureCloner;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow.OverflowPaginationService;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.FieldGeometry;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.FieldGeometryResolver;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.FieldTextLayout;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.TextCapacityCalculator;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.TextLineWrapper;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;

/**
 * End-to-end check that what PDFBox actually draws stays inside the box it is drawn into.
 *
 * <p>Every other test in this area asserts against our own model of the field. This one asserts
 * against the rendered result: it fills a real template, lets PDFBox generate the appearance
 * streams, then measures the glyphs in those streams and compares them with the widget rectangle.
 * If the fitting logic were wrong in the dangerous direction — reporting that a value fits when it
 * does not — this is the test that notices.
 *
 * <p>Verification happens at the filler rather than at {@code PdfBoxPdfGenerator}, because the
 * generator flattens the form and the widget rectangles we need to compare against are gone by
 * then.
 */
class RenderedTextFitsFieldTest {

  /** Rounding in glyph widths; a fraction of a point cannot be seen and is not worth failing on. */
  private static final float TOLERANCE = 0.5f;

  private static final String ELLIPSIS = "...";

  private static final String LONG_VALUE =
      "Patienten har en nedsatt funktion i höger knä som påverkar rörelseförmågan och "
          + "ger betydande smärta vid belastning under längre perioder, vilket begränsar "
          + "möjligheten att utföra arbetsuppgifter som kräver att man står eller går. "
          + "Ytterligare bedömning visar svårigheter med vardagliga aktiviteter.";

  private final TextFieldAppearance textFieldAppearance = new TextFieldAppearance();

  private final AcroFormFiller filler =
      new AcroFormFiller(
          new FieldValueProcessor(new FieldTextLayout()),
          new FieldGeometryResolver(),
          new OverflowFieldWriter(
              new OverflowPaginationService(
                  new OverflowPagePaginator(new TextLineWrapper(), new TextCapacityCalculator()),
                  new OverflowPageRenderer(new OverflowPageStructureCloner()),
                  textFieldAppearance)),
          textFieldAppearance);

  private final FieldGeometryResolver resolver = new FieldGeometryResolver();
  private final FieldTextLayout textLayout = new FieldTextLayout();

  private PDDocument document;

  @AfterEach
  void tearDown() throws IOException {
    if (document != null) {
      document.close();
    }
  }

  @Test
  @DisplayName("every field of the FK7210 template renders inside its rectangle")
  void shallRenderWithinRectangleForFk7210() throws IOException {
    document = load(TAGGED_PDF_RESOURCE);

    filler.fill(document, fk7210Fields(), null, null);

    assertEveryFieldRendersWithinItsRectangle();
  }

  @Test
  @DisplayName("every field of the FK7804 template renders inside its rectangle")
  void shallRenderWithinRectangleForFk7804() throws IOException {
    document = load(TAGGED_PDF_OVERFLOW_RESOURCE);

    filler.fill(document, fk7804Fields(), 1, null);

    assertEveryFieldRendersWithinItsRectangle();
  }

  /**
   * The real blast radius test: every text field in the template gets a value far longer than it
   * can hold. Previously a field without a {@code maxLength} was written whole and left to spill;
   * now every field must be fitted.
   */
  @Test
  @DisplayName("every field of the FK7210 template renders inside its rectangle when overfilled")
  void shallRenderWithinRectangleWhenEveryFieldIsOverfilled() throws IOException {
    document = load(TAGGED_PDF_RESOURCE);

    filler.fill(document, everyTextFieldWith(), null, null);

    assertEveryFieldRendersWithinItsRectangle();
  }

  @Test
  @DisplayName("every field of the FK7804 template renders inside its rectangle when overfilled")
  void shallRenderWithinRectangleWhenEveryFk7804FieldIsOverfilled() throws IOException {
    document = load(TAGGED_PDF_OVERFLOW_RESOURCE);

    filler.fill(document, everyTextFieldWith(), 1, null);

    assertEveryFieldRendersWithinItsRectangle();
  }

  private Map<String, CustomPdfField> everyTextFieldWith() {
    final var fields = new LinkedHashMap<String, CustomPdfField>();
    for (final var field : textFields()) {
      fields.put(
          field.getFullyQualifiedName(),
          CustomPdfField.builder().value(RenderedTextFitsFieldTest.LONG_VALUE).build());
    }
    return fields;
  }

  private void assertEveryFieldRendersWithinItsRectangle() throws IOException {
    var fieldsChecked = 0;

    for (final var field : textFields()) {
      final var value = field.getValue();
      if (value == null || value.isEmpty()) {
        continue;
      }

      final var font = FontResolver.getFont(field);
      final var geometry = resolver.resolve(field, font);
      if (geometry.isEmpty()
          || !(font instanceof PDSimpleFont)
          || tooSmallForAnyTextBesideAnEllipsis(value, geometry.get())) {
        continue;
      }

      final var lines = renderedLines(field, font, geometry.get().fontSize());
      if (lines.isEmpty()) {
        continue;
      }
      fieldsChecked++;

      assertTrue(
          lines.size() <= geometry.get().maxLines(),
          () ->
              "Field '%s' rendered %s lines but only %s fit. Value: %s"
                  .formatted(
                      field.getFullyQualifiedName(),
                      lines.size(),
                      geometry.get().maxLines(),
                      value));

      for (final var line : lines) {
        assertTrue(
            line.width() <= geometry.get().availableWidth() + TOLERANCE,
            () ->
                "Field '%s' rendered a line %spt wide into %spt of space. Value: %s"
                    .formatted(
                        field.getFullyQualifiedName(),
                        line.width(),
                        geometry.get().availableWidth(),
                        value));
      }
    }

    assertNotEquals(0, fieldsChecked, "No fields were actually verified, the test proves nothing");
  }

  /**
   * Such a field is deliberately left to the renderer to clip, see {@link FieldValueProcessor}, so
   * it is outside what this test guards.
   */
  private boolean tooSmallForAnyTextBesideAnEllipsis(String value, FieldGeometry geometry)
      throws IOException {
    return !textLayout.fits(value.charAt(0) + ELLIPSIS, geometry);
  }

  private List<PDTextField> textFields() {
    final var acroForm = document.getDocumentCatalog().getAcroForm();
    final var fields = new ArrayList<PDTextField>();
    for (final var field : acroForm.getFieldTree()) {
      if (field instanceof PDTextField textField && hasRectangle(textField)) {
        fields.add(textField);
      }
    }
    return fields;
  }

  private boolean hasRectangle(PDField field) {
    final var widgets = field.getWidgets();
    return widgets != null && !widgets.isEmpty() && widgets.getFirst().getRectangle() != null;
  }

  /**
   * Reconstructs the lines PDFBox drew.
   *
   * <p>{@code PlainTextFormatter} starts each line with a vertically moving {@code Td}, then emits
   * one {@code Tj} per word with a horizontal {@code Td} in between. So a vertical move opens a new
   * line and the width of a line is the total advance of the words on it.
   */
  private List<RenderedLine> renderedLines(PDTextField field, PDFont font, float fontSize)
      throws IOException {
    final var appearanceStream =
        field.getWidgets().getFirst().getAppearance().getNormalAppearance().getAppearanceStream();
    if (appearanceStream == null) {
      return List.of();
    }

    final var lines = new ArrayList<RenderedLine>();
    final var operands = new ArrayList<COSBase>();
    var currentWidth = 0f;
    var started = false;

    for (final var token : new PDFStreamParser(appearanceStream).parse()) {
      if (!(token instanceof Operator operator)) {
        operands.add((COSBase) token);
        continue;
      }

      if (isVerticalTextAdvance(operator, operands)) {
        if (started) {
          lines.add(new RenderedLine(currentWidth));
        }
        started = true;
        currentWidth = 0f;
      } else if ("Tj".equals(operator.getName()) && !operands.isEmpty()) {
        currentWidth += width(operands.getLast(), font, fontSize);
      }
      operands.clear();
    }

    if (started) {
      lines.add(new RenderedLine(currentWidth));
    }
    return lines;
  }

  /**
   * Measures the shown string by its character codes, so that no assumption is made about the
   * font's encoding.
   */
  private float width(COSBase operand, PDFont font, float fontSize) throws IOException {
    if (!(operand instanceof COSString shownText)) {
      return 0f;
    }
    var glyphSpaceWidth = 0f;
    for (final var code : shownText.getBytes()) {
      glyphSpaceWidth += font.getWidth(code & 0xFF);
    }
    return glyphSpaceWidth / 1000f * fontSize;
  }

  private boolean isVerticalTextAdvance(Operator operator, List<COSBase> operands) {
    return "Td".equals(operator.getName())
        && operands.size() == 2
        && operands.get(1) instanceof COSNumber verticalOffset
        && Float.compare(verticalOffset.floatValue(), 0f) != 0;
  }

  private PDDocument load(String resource) throws IOException {
    try (final var stream = getClass().getResourceAsStream(resource)) {
      return Loader.loadPDF(stream.readAllBytes());
    }
  }

  private record RenderedLine(float width) {}
}
