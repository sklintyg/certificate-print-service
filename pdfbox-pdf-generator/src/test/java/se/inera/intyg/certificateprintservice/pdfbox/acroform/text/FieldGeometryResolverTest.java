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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FieldGeometryResolverTest {

  private static final PDFont FONT = new PDType1Font(FontName.HELVETICA);
  private static final float FONT_SIZE = 10f;

  /** Helvetica's font bounding box is 1156 glyph units high, so a 10pt line is 11.56pt. */
  private static final float LINE_HEIGHT = 11.56f;

  private final FieldGeometryResolver resolver = new FieldGeometryResolver();

  private PDDocument document;
  private PDAcroForm acroForm;

  @BeforeEach
  void setUp() {
    document = new PDDocument();
    document.addPage(new PDPage(PDRectangle.A4));
    acroForm = new PDAcroForm(document);
  }

  @AfterEach
  void tearDown() throws IOException {
    document.close();
  }

  @Test
  void shouldSubtractPaddingFourTimesFromWidth() throws IOException {
    final var field = textField(100f, 50f, true, "/Helv 10 Tf 0 g");

    final var geometry = resolver.resolve(field, FONT).orElseThrow();

    assertEquals(96f, geometry.availableWidth());
  }

  @Test
  void shouldDeriveMaxLinesFromFontBoundingBoxHeight() throws IOException {
    final var field = textField(100f, 50f, true, "/Helv 10 Tf 0 g");

    final var geometry = resolver.resolve(field, FONT).orElseThrow();

    assertEquals((int) Math.floor(46f / LINE_HEIGHT), geometry.maxLines());
  }

  @Test
  void shouldReportAtLeastOneLineWhenFieldIsShorterThanALine() throws IOException {
    final var field = textField(100f, 6f, true, "/Helv 10 Tf 0 g");

    final var geometry = resolver.resolve(field, FONT).orElseThrow();

    assertEquals(1, geometry.maxLines());
  }

  @Test
  void shouldReportSingleLineForFieldWithoutMultilineFlag() throws IOException {
    final var field = textField(100f, 50f, false, "/Helv 10 Tf 0 g");

    final var geometry = resolver.resolve(field, FONT).orElseThrow();

    assertEquals(1, geometry.maxLines());
  }

  @Test
  void shouldReportNotMultiLineForFieldWithoutMultilineFlag() throws IOException {
    final var field = textField(100f, 50f, false, "/Helv 10 Tf 0 g");

    final var geometry = resolver.resolve(field, FONT).orElseThrow();

    assertFalse(geometry.multiLine());
  }

  @Test
  void shouldUseBorderWidthAsPaddingWhenWiderThanMinimum() throws IOException {
    final var field = textField(100f, 50f, true, "/Helv 10 Tf 0 g");
    final var borderStyle = new PDBorderStyleDictionary();
    borderStyle.setWidth(3f);
    field.getWidgets().getFirst().setBorderStyle(borderStyle);

    final var geometry = resolver.resolve(field, FONT).orElseThrow();

    assertEquals(88f, geometry.availableWidth());
  }

  @Test
  void shouldSwapWidthAndHeightForQuarterTurnedWidget() throws IOException {
    final var field = textField(100f, 50f, true, "/Helv 10 Tf 0 g");
    final var characteristics = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
    characteristics.setRotation(90);
    field.getWidgets().getFirst().setAppearanceCharacteristics(characteristics);

    final var geometry = resolver.resolve(field, FONT).orElseThrow();

    assertEquals(46f, geometry.availableWidth());
  }

  @Test
  void shouldReturnEmptyForAutoSizedFont() throws IOException {
    final var field = textField(100f, 50f, true, "/Helv 0 Tf 0 g");

    assertTrue(resolver.resolve(field, FONT).isEmpty());
  }

  @Test
  void shouldReturnEmptyForCombField() throws IOException {
    final var field = textField(100f, 50f, false, "/Helv 10 Tf 0 g");
    field.setMaxLen(10);
    field.setComb(true);

    assertTrue(resolver.resolve(field, FONT).isEmpty());
  }

  @Test
  void shouldReturnEmptyWhenWidgetHasNoRectangle() throws IOException {
    final var field = new PDTextField(acroForm);
    field.setPartialName("noRectangle");
    field.setDefaultAppearance("/Helv 10 Tf 0 g");
    field.getCOSObject().removeItem(COSName.RECT);

    assertTrue(resolver.resolve(field, FONT).isEmpty());
  }

  @Test
  void shouldReturnEmptyWhenFieldHasNoWidgetsAtAll() throws IOException {
    final var field = new PDTextField(acroForm);
    field.setPartialName("noWidgets");
    field.setDefaultAppearance("/Helv 10 Tf 0 g");
    field.setWidgets(List.of());

    assertTrue(resolver.resolve(field, FONT).isEmpty());
  }

  /**
   * A line height of zero or less would make the number of fitting lines meaningless, so the field
   * is left to the renderer rather than divided by.
   */
  @Test
  void shouldReturnEmptyWhenLineHeightIsNotPositive() throws IOException {
    final var field = textField(100f, 50f, true, "/Helv -10 Tf 0 g");

    assertTrue(resolver.resolve(field, FONT).isEmpty());
  }

  @Test
  void shouldSwapWidthAndHeightForThreeQuarterTurnedWidget() throws IOException {
    final var field = rotatedField(270);

    final var geometry = resolver.resolve(field, FONT).orElseThrow();

    assertEquals(46f, geometry.availableWidth());
  }

  @Test
  void shouldSwapWidthAndHeightForNegativeQuarterTurnedWidget() throws IOException {
    final var field = rotatedField(-90);

    final var geometry = resolver.resolve(field, FONT).orElseThrow();

    assertEquals(46f, geometry.availableWidth());
  }

  @Test
  void shouldNotSwapWidthAndHeightForHalfTurnedWidget() throws IOException {
    final var field = rotatedField(180);

    final var geometry = resolver.resolve(field, FONT).orElseThrow();

    assertEquals(96f, geometry.availableWidth());
  }

  @Test
  void shouldUseMinimumPaddingWhenBorderIsNarrowerThanMinimum() throws IOException {
    final var field = textField(100f, 50f, true, "/Helv 10 Tf 0 g");
    final var borderStyle = new PDBorderStyleDictionary();
    borderStyle.setWidth(0.25f);
    field.getWidgets().getFirst().setBorderStyle(borderStyle);

    final var geometry = resolver.resolve(field, FONT).orElseThrow();

    assertEquals(96f, geometry.availableWidth());
  }

  @Test
  void shouldReturnEmptyWhenFieldIsNarrowerThanItsPadding() throws IOException {
    final var field = textField(3f, 50f, true, "/Helv 10 Tf 0 g");

    assertTrue(resolver.resolve(field, FONT).isEmpty());
  }

  @Test
  void shouldReturnFontSizeFromDefaultAppearance() throws IOException {
    final var field = textField(100f, 50f, true, "/Helv 10 Tf 0 g");

    final var geometry = resolver.resolve(field, FONT).orElseThrow();

    assertEquals(FONT_SIZE, geometry.fontSize());
  }

  private PDTextField rotatedField(int rotation) {
    final var field = textField(100f, 50f, true, "/Helv 10 Tf 0 g");
    final var characteristics = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
    characteristics.setRotation(rotation);
    field.getWidgets().getFirst().setAppearanceCharacteristics(characteristics);
    return field;
  }

  private PDTextField textField(
      float width, float height, boolean multiline, String defaultAppearance) {
    final var field = new PDTextField(acroForm);
    field.setPartialName("field");
    field.setDefaultAppearance(defaultAppearance);
    field.setMultiline(multiline);

    final var widget = new PDAnnotationWidget();
    widget.setRectangle(new PDRectangle(10f, 10f, width, height));
    widget.getCOSObject().setItem(COSName.PARENT, field);
    field.setWidgets(List.of(widget));

    return field;
  }
}
