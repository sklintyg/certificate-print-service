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
package se.inera.intyg.certificateprintservice.playwright.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.GeneralPrintText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.Metadata;

class LeftMarginInfoConverterTest {

  private final LeftMarginInfoConverter leftMarginInfoConverter = new LeftMarginInfoConverter();

  private static final String TYPE_ID = "typeId";
  private static final String RECIPIENT_NAME = "recipientName";
  private static final Metadata METADATA =
      Metadata.builder()
          .typeId(TYPE_ID)
          .recipientName(RECIPIENT_NAME)
          .recipientId("recipientId")
          .version("version")
          .generalPrintText(
              GeneralPrintText.builder()
                  .leftMarginInfoText("left margin text")
                  .draftAlertInfoText("draft alert text")
                  .build())
          .build();

  @Test
  void shouldSetType() {
    final var response = leftMarginInfoConverter.convert(METADATA);
    assertEquals(TYPE_ID, response.getCertificateType());
  }

  @Test
  void shouldSetRecipientName() {
    final var response = leftMarginInfoConverter.convert(METADATA);
    assertEquals(RECIPIENT_NAME, response.getRecipientName());
  }

  @Test
  void shouldSetVersion() {
    final var response = leftMarginInfoConverter.convert(METADATA);
    assertEquals("version", response.getCertificateVersion());
  }

  @Test
  void shouldSetRecipientId() {
    final var response = leftMarginInfoConverter.convert(METADATA);
    assertEquals("recipientId", response.getRecipientId());
  }

  @Test
  void shouldSetLeftMarginText() {
    final var response = leftMarginInfoConverter.convert(METADATA);
    assertEquals("left margin text", response.getLeftMarginText());
  }
}
