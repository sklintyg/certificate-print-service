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
package se.inera.intyg.certificateprintservice.pdfgenerator.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class MetadataTest {

  public static final String LEFT_MARGIN_INFO_TEXT = "leftMarginInfoText";
  public static final String DRAFT_ALERT_INFO_TEXT = "draftAlertInfoText";

  @Test
  void shouldReturnIsNotDraftIfSigned() {
    assertFalse(Metadata.builder().signingDate("2025-01-01").build().isDraft());
  }

  @Test
  void shouldReturnGeneralPrintText() {
    var generalPrintText =
        Metadata.builder()
            .generalPrintText(
                GeneralPrintText.builder()
                    .leftMarginInfoText(LEFT_MARGIN_INFO_TEXT)
                    .draftAlertInfoText(DRAFT_ALERT_INFO_TEXT)
                    .build())
            .build()
            .getGeneralPrintText();

    assertEquals(LEFT_MARGIN_INFO_TEXT, generalPrintText.getLeftMarginInfoText());
    assertEquals(DRAFT_ALERT_INFO_TEXT, generalPrintText.getDraftAlertInfoText());
  }
}
