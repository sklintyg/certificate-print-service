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

import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;
import org.springframework.stereotype.Component;

/**
 * Derives the usable text area of a form field from the field's own widget geometry.
 *
 * <p>The numbers here mirror how PDFBox lays out a text field appearance in {@code
 * AppearanceGeneratorHelper}, because an estimate that disagrees with the renderer is worse than no
 * estimate at all:
 *
 * <ul>
 *   <li><b>Padding.</b> PDFBox uses {@code max(1, borderWidth)} and applies it twice — once for the
 *       clipping rectangle and once more for the content rectangle — so the text area is inset by
 *       four paddings in each dimension.
 *   <li><b>Line height.</b> Acrobat, and therefore PDFBox, uses the font's bounding box height as
 *       the leading between lines, <em>not</em> a fixed multiple of the font size.
 *   <li><b>Wrapping.</b> Only fields with the multiline flag wrap; every other field renders on a
 *       single line.
 * </ul>
 *
 * <p>Returns {@link Optional#empty()} when the field must not be fitted by us, either because
 * PDFBox already guarantees a fit (auto-sized fonts shrink the text, comb fields cap the character
 * count) or because the geometry is unusable.
 */
@Slf4j
@Component
public class FieldGeometryResolver {

  private static final float MINIMUM_PADDING = 1f;
  private static final int PADDING_APPLICATIONS_PER_DIMENSION = 4;
  private static final float AUTO_SIZED_FONT = 0f;
  private static final float GLYPH_SPACE_SCALE = 1000f;
  private static final int QUARTER_TURN = 90;
  private static final int THREE_QUARTER_TURNS = 270;

  public Optional<FieldGeometry> resolve(PDVariableText field, PDFont font) throws IOException {
    final var fontSize = DefaultAppearanceParser.fontSize(field.getDefaultAppearance());
    if (Float.compare(fontSize, AUTO_SIZED_FONT) == 0) {
      log.debug(
          "Field '{}' is auto-sized, leaving the fit to the renderer",
          field.getFullyQualifiedName());
      return Optional.empty();
    }

    if (isComb(field)) {
      log.debug(
          "Field '{}' is a comb field, leaving the fit to its maximum length",
          field.getFullyQualifiedName());
      return Optional.empty();
    }

    final var widget = firstWidget(field);
    return widget.isEmpty() ? Optional.empty() : resolve(widget.get(), field, font, fontSize);
  }

  private Optional<FieldGeometry> resolve(
      PDAnnotationWidget widget, PDVariableText field, PDFont font, float fontSize)
      throws IOException {
    final var contentArea = contentArea(widget);
    final var lineHeight = lineHeight(font, fontSize);

    if (contentArea.getWidth() <= 0 || lineHeight <= 0) {
      log.warn(
          "Field '{}' has an unusable text area (width {}, line height {}), skipping fitting",
          field.getFullyQualifiedName(),
          contentArea.getWidth(),
          lineHeight);
      return Optional.empty();
    }

    final var multiLine = isMultiLine(field);
    final var maxLines = multiLine ? fittingLines(contentArea.getHeight(), lineHeight) : 1;

    return Optional.of(
        new FieldGeometry(font, fontSize, contentArea.getWidth(), maxLines, multiLine));
  }

  /**
   * The rectangle PDFBox writes text into: the widget rectangle inset by padding twice, and with
   * width and height swapped when the widget is rendered on its side.
   */
  private PDRectangle contentArea(PDAnnotationWidget widget) {
    final var rectangle = widget.getRectangle();
    final var inset = PADDING_APPLICATIONS_PER_DIMENSION * padding(widget);
    final var rotated = isRotatedQuarterTurn(widget);

    final var width = (rotated ? rectangle.getHeight() : rectangle.getWidth()) - inset;
    final var height = (rotated ? rectangle.getWidth() : rectangle.getHeight()) - inset;

    return new PDRectangle(width, height);
  }

  private float padding(PDAnnotationWidget widget) {
    final var borderStyle = widget.getBorderStyle();
    return borderStyle == null
        ? MINIMUM_PADDING
        : Math.max(MINIMUM_PADDING, borderStyle.getWidth());
  }

  private boolean isRotatedQuarterTurn(PDAnnotationWidget widget) {
    final var characteristics = widget.getAppearanceCharacteristics();
    if (characteristics == null) {
      return false;
    }
    final var rotation = Math.floorMod(characteristics.getRotation(), 360);
    return rotation == QUARTER_TURN || rotation == THREE_QUARTER_TURNS;
  }

  /** Acrobat uses the font bounding box height as the leading between lines. */
  private float lineHeight(PDFont font, float fontSize) throws IOException {
    return font.getBoundingBox().getHeight() * fontSize / GLYPH_SPACE_SCALE;
  }

  private int fittingLines(float availableHeight, float lineHeight) {
    return Math.max(1, (int) Math.floor(availableHeight / lineHeight));
  }

  private Optional<PDAnnotationWidget> firstWidget(PDVariableText field) {
    final var widgets = field.getWidgets();
    return widgets == null || widgets.isEmpty() || widgets.getFirst().getRectangle() == null
        ? Optional.empty()
        : Optional.of(widgets.getFirst());
  }

  private boolean isMultiLine(PDVariableText field) {
    return field instanceof PDTextField textField && textField.isMultiline();
  }

  private boolean isComb(PDVariableText field) {
    return field instanceof PDTextField textField && textField.isComb();
  }
}
