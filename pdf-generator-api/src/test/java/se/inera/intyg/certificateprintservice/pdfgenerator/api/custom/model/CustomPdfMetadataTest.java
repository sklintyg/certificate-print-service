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
package se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CustomPdfMetadataTest {

  @Test
  void shouldReturnTrueWhenRightMarginTextIsSet() {
    assertTrue(
        CustomPdfMetadata.builder().rightMarginText("some text").build().hasRightMarginText());
  }

  @Test
  void shouldReturnFalseWhenRightMarginTextIsNull() {
    assertFalse(CustomPdfMetadata.builder().rightMarginText(null).build().hasRightMarginText());
  }

  @Test
  void shouldReturnFalseWhenRightMarginTextIsEmpty() {
    assertFalse(CustomPdfMetadata.builder().rightMarginText("").build().hasRightMarginText());
  }

  @Test
  void shouldReturnFalseWhenRightMarginTextIsBlank() {
    assertFalse(CustomPdfMetadata.builder().rightMarginText("   ").build().hasRightMarginText());
  }

  @Test
  void shouldReturnTrueForHasPersonIdWhenPersonIdIsSet() {
    assertTrue(
        CustomPdfMetadata.builder()
            .personIdConfig(PersonIdConfig.builder().fieldId("field").value("value").build())
            .build()
            .hasPersonId());
  }

  @Test
  void shouldReturnFalseForHasPersonIdWhenPersonIdIsNull() {
    assertFalse(CustomPdfMetadata.builder().build().hasPersonId());
  }
}
