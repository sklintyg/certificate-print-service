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
package se.inera.intyg.certificateprintservice.integrationtest.fk7804;

public class TestSetupFK7804Constants {

  private TestSetupFK7804Constants() {
    throw new IllegalStateException("Utility class");
  }

  public static final String TAGGED_PDF_OVERFLOW_RESOURCE = "/templates/fk7804-overflow.pdf";

  public static final String FUNKTIONSNEDSATTNING_FIELD_ID =
      "form1[0].Sida2[0].flt_txtBeskrivUndersokningsfynd[0]";
  public static final String AKTIVITETSBEGRANSNING_FIELD_ID =
      "form1[0].Sida2[0].flt_txtBeskrivAktivitetsbegransning[0]";
  public static final String OVERFLOW_FIELD_ID = "form1[0].#subform[4].flt_txtFortsattningsblad[0]";
  public static final String PATIENT_ID_FIELD_ID = "form1[0].#subform[0].flt_txtPersonNr[0]";

  public static final int PDF_TEXT_FIELD_ROW_LENGTH = 53;
  public static final int MAX_LENGTH_FUNKTIONSNEDSATTNING = 11 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_AKTIVITETSBEGRANSNING = 12 * PDF_TEXT_FIELD_ROW_LENGTH;

  public static final String PATIENT_ID = "191212121212";
}
