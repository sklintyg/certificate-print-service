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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.stereotype.Service;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.OverflowEntry;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.TextFieldAppearance;

@Service
@RequiredArgsConstructor
public class OverflowPaginationService {

  private final OverflowPagePaginator paginator;
  private final OverflowPageRenderer renderer;

  public void writeWithPagination(
      PDDocument document,
      PDTextField textField,
      List<OverflowEntry> entries,
      int overflowPageIndex)
      throws IOException {
    final var acroForm = document.getDocumentCatalog().getAcroForm();
    final var textAppearance = new TextFieldAppearance(textField);
    final var font = textAppearance.getFont(acroForm);
    final var boldFont = new PDType1Font(FontName.HELVETICA_BOLD);
    final var fontSize = textAppearance.getFontSize();
    final var fieldRect = getFieldRectangle(textField);
    final var lineSpacing = renderer.getLineSpacing();
    final var topMargin = renderer.getTopMargin();
    final var horizontalInsets = renderer.getHorizontalInsets();

    final var effectiveWidth = fieldRect.getWidth() - horizontalInsets;
    final var pages =
        paginator.paginate(
            entries,
            font,
            boldFont,
            fontSize,
            effectiveWidth,
            fieldRect.getHeight(),
            lineSpacing,
            topMargin);

    if (pages.isEmpty()) {
      throw new IllegalStateException("No pages for overflow was found");
    }

    textField.setValue("");
    renderer.renderAllOverflowPages(
        document, overflowPageIndex, pages, font, boldFont, fontSize, fieldRect);
  }

  private PDRectangle getFieldRectangle(PDTextField textField) {
    final var widgets = textField.getWidgets();
    if (widgets.isEmpty()) {
      throw new IllegalStateException("Overflow field has no widgets — cannot determine rectangle");
    }
    return widgets.getFirst().getRectangle();
  }
}
