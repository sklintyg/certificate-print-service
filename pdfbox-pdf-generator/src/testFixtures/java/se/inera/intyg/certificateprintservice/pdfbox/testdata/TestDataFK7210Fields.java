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
package se.inera.intyg.certificateprintservice.pdfbox.testdata;

import java.util.Map;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.CustomPdfFieldF;

/**
 * Realistic FK7210 (Intyg om graviditet) PDF field test data.
 *
 * <p>Field IDs correspond to the AcroForm fields in the FK7210 PDF template as defined by {@code
 * FK7210PdfSpecification} in the certificate-service.
 */
public class TestDataFK7210Fields {

  // --- Field ID constants ---

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

  // --- Realistic value constants ---

  public static final String PATIENT_ID = "191212121212";
  public static final String BIRTH_DATE = "2025-10-20";
  public static final String SIGNED_DATE = "2025-03-15";
  public static final String DOCTOR_NAME = "Anna Lindqvist";
  public static final String PA_TITLE = "Läkare";
  public static final String SPECIALTY = "Allmänmedicin";
  public static final String HSA_ID = "SE2321000016-1C4B";
  public static final String WORKPLACE_CODE = "1234567";
  public static final String CONTACT_INFORMATION =
      "Vårdcentralen Centrum\nStorgatan 1\n111 11 Stockholm\nTel: 08-123 456 78";

  private TestDataFK7210Fields() {
    throw new IllegalStateException("Utility class");
  }

  public static final String TAGGED_PDF_RESOURCE = "/tagged-test-template.pdf";

  /**
   * Returns a map of all FK7210 AcroForm field IDs to their realistic test values, as they would
   * appear in a fully filled-in certificate ready for printing.
   */
  public static Map<String, CustomPdfFieldF> fk7210Fields() {
    return Map.of(
        PATIENT_ID_FIELD_ID, CustomPdfFieldF.builder().value(PATIENT_ID).build(),
        BIRTH_DATE_FIELD_ID, CustomPdfFieldF.builder().value(BIRTH_DATE).build(),
        SIGNED_DATE_FIELD_ID, CustomPdfFieldF.builder().value(SIGNED_DATE).build(),
        SIGNED_BY_NAME_FIELD_ID, CustomPdfFieldF.builder().value(DOCTOR_NAME).build(),
        PA_TITLE_FIELD_ID, CustomPdfFieldF.builder().value(PA_TITLE).build(),
        SPECIALTY_FIELD_ID, CustomPdfFieldF.builder().value(SPECIALTY).build(),
        HSA_ID_FIELD_ID, CustomPdfFieldF.builder().value(HSA_ID).build(),
        WORKPLACE_CODE_FIELD_ID, CustomPdfFieldF.builder().value(WORKPLACE_CODE).build(),
        CONTACT_INFORMATION_FIELD_ID, CustomPdfFieldF.builder().value(CONTACT_INFORMATION).build());
  }
}
