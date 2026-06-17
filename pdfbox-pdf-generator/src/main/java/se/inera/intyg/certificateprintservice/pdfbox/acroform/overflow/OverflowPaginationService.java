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
package se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.TextFieldAppearance;

@Service
@RequiredArgsConstructor
public class OverflowPaginationService {

  private final OverflowPagePaginator paginator;
  private final OverflowPageRenderer renderer;

  public void writeWithPagination(
      PDDocument document, PDTextField textField, String content, int overflowPageIndex)
      throws IOException {
    final var font = renderer.getDefaultOverflowFont();
    final var fontSize = new TextFieldAppearance(textField).getFontSize();
    final var fieldRect = getFieldRectangle(textField);
    final var lineSpacing = renderer.getLineSpacing();
    final var topMargin = renderer.getTopMargin();
    final var horizontalInsets = renderer.getHorizontalInsets();

    final var effectiveWidth = fieldRect.getWidth() - horizontalInsets;
    final var pages =
        paginator.paginate(
            content, font, fontSize, effectiveWidth, fieldRect.getHeight(), lineSpacing, topMargin);

    if (pages.isEmpty()) {
      throw new IllegalStateException("No pages for overflow was found");
    }

    if (pages.size() == 1) {
      textField.setValue(content);
      return;
    }

    textField.setValue("");
    renderer.renderAllOverflowPages(document, overflowPageIndex, pages, font, fontSize, fieldRect);
  }

  private PDRectangle getFieldRectangle(PDTextField textField) {
    final var widgets = textField.getWidgets();
    if (widgets.isEmpty()) {
      throw new IllegalStateException("Overflow field has no widgets — cannot determine rectangle");
    }
    return widgets.getFirst().getRectangle();
  }
}
