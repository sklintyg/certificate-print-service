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
package se.inera.intyg.certificateprintservice.integrationtest.fk7210;

public class TestSetupFK7210Constants {

  private TestSetupFK7210Constants() {
    throw new IllegalStateException("Utility class");
  }

  public static final String PATIENT_ID_FIELD_ID = "form1[0].#subform[0].flt_txtPersonNr[0]";
  public static final String BIRTH_DATE_FIELD_ID = "form1[0].#subform[0].flt_dat[0]";
  public static final String SIGNED_DATE_FIELD_ID = "form1[0].#subform[0].flt_datUnderskrift[0]";
  public static final String SIGNED_BY_NAME_FIELD_ID =
      "form1[0].#subform[0].flt_txtNamnfortydligande[0]";
  public static final String PA_TITLE_FIELD_ID = "form1[0].#subform[0].flt_txtBefattning[0]";
  public static final String SPECIALTY_FIELD_ID =
      "form1[0].#subform[0].flt_txtEventuellSpecialistkompetens[0]";
  public static final String HSA_ID_FIELD_ID = "form1[0].#subform[0].flt_txtLakarensHSA-ID[0]";
  public static final String WORKPLACE_CODE_FIELD_ID =
      "form1[0].#subform[0].flt_txtArbetsplatskod[0]";
  public static final String CONTACT_INFORMATION_FIELD_ID =
      "form1[0].#subform[0].flt_txtVardgivarensNamnAdressTelefon[0]";

  public static final String PATIENT_ID = "191212121212";
  public static final String BIRTH_DATE = "2025-10-20";
  public static final String SIGNED_DATE = "2025-03-15";
  public static final String DOCTOR_NAME = "Ajla Doktor";
  public static final String PA_TITLE = "202010, 208014, 208015";
  public static final String SPECIALTY = "";
  public static final String HSA_ID = "TSTNMT2321000156-DRAA";
  public static final String WORKPLACE_CODE = "12345678";
  public static final String CONTACT_INFORMATION =
      "Alfa Medicincentrum\n" + "Alfagatan 3 a, 65591 Karlstad\n" + "Telefon: 054121314";

  public static final String TAGGED_PDF_RESOURCE = "/templates/fk7210.pdf";

  public static final String RIGHT_MARGIN_TEXT =
      "Intygsid: 8996d3d8-cb67-4602-b6a9-81dee33616ce. Intyget är utskrivet från Webcert.";
  public static final int START_MCID = 100;
  public static final int SIGNATURE_PAGE_INDEX = 0;
  public static final int SIGNATURE_TAG_INDEX_WITH_ADDRESS = 15;
}
