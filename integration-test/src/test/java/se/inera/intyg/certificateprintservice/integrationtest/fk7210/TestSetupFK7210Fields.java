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

import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.BIRTH_DATE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.BIRTH_DATE_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.CONTACT_INFORMATION;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.CONTACT_INFORMATION_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.DOCTOR_NAME;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.HSA_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.HSA_ID_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.PATIENT_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.PATIENT_ID_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.PA_TITLE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.PA_TITLE_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.SIGNED_BY_NAME_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.SIGNED_DATE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.SIGNED_DATE_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.SPECIALTY;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.SPECIALTY_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.WORKPLACE_CODE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.WORKPLACE_CODE_FIELD_ID;

import java.util.Map;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfFieldDTO;

public class TestSetupFK7210Fields {

  public static Map<String, CustomPdfFieldDTO> allFields() {
    return Map.of(
        PATIENT_ID_FIELD_ID,
        CustomPdfFieldDTO.builder().value(PATIENT_ID).build(),
        BIRTH_DATE_FIELD_ID,
        CustomPdfFieldDTO.builder().value(BIRTH_DATE).build(),
        SIGNED_DATE_FIELD_ID,
        CustomPdfFieldDTO.builder().value(SIGNED_DATE).build(),
        SIGNED_BY_NAME_FIELD_ID,
        CustomPdfFieldDTO.builder().value(DOCTOR_NAME).build(),
        PA_TITLE_FIELD_ID,
        CustomPdfFieldDTO.builder().value(PA_TITLE).build(),
        SPECIALTY_FIELD_ID,
        CustomPdfFieldDTO.builder().value(SPECIALTY).build(),
        HSA_ID_FIELD_ID,
        CustomPdfFieldDTO.builder().value(HSA_ID).build(),
        WORKPLACE_CODE_FIELD_ID,
        CustomPdfFieldDTO.builder().value(WORKPLACE_CODE).build(),
        CONTACT_INFORMATION_FIELD_ID,
        CustomPdfFieldDTO.builder().value(CONTACT_INFORMATION).build());
  }
}
