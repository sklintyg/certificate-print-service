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

import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.AKTIVITETSBEGRANSNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.AKTIVITETSBEGRANSNING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.ANNAN_GRUND;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.ANNAN_GRUND_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.ATGARDER;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.ATGARDER_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_100_PROCENT_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_25_PROCENT_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_50_PROCENT_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_75_PROCENT_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_ANNAT_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_ARBETSSOKANDE_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_DIGITAL_UNDERSOKNING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_FORALDRALEDIGHET_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_FYSISK_UNDERSOKNING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_JOURNALUPPGIFTER_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_KONTAKT_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_NUVARANDE_ARBETE_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_SMITTBARARPENNING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_STUDIER_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CHECKBOX_TELEFONKONTAKT_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CONTACT_INFORMATION;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.CONTACT_INFORMATION_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_100_FROM;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_100_FROM_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_100_TO;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_100_TO_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_25_FROM;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_25_FROM_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_25_TO;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_25_TO_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_50_FROM;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_50_FROM_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_50_TO;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_50_TO_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_75_FROM;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_75_FROM_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_75_TO;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_75_TO_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_ANNAT;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_ANNAT_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_DIGITAL_UNDERSOKNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_DIGITAL_UNDERSOKNING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_FYSISK_UNDERSOKNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_FYSISK_UNDERSOKNING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_JOURNALUPPGIFTER;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_JOURNALUPPGIFTER_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_TELEFONKONTAKT;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DATE_TELEFONKONTAKT_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_1_CODE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_1_CODE_1_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_1_CODE_2_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_1_CODE_3_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_1_CODE_4_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_1_CODE_5_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_1_NAME;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_1_NAME_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_2_CODE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_2_CODE_1_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_2_CODE_2_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_2_CODE_3_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_2_CODE_4_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_2_NAME;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_2_NAME_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_3_CODE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_3_CODE_1_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_3_CODE_2_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_3_CODE_3_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_3_CODE_4_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_3_NAME;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DIAGNOS_3_NAME_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.DOCTOR_NAME;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.FUNKTIONSNEDSATTNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.FUNKTIONSNEDSATTNING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.GRUND_FOR_BEDOMNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.GRUND_FOR_BEDOMNING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.HSA_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.HSA_ID_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.MAX_LENGTH_AKTIVITETSBEGRANSNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.MAX_LENGTH_ATGARDER;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.MAX_LENGTH_FUNKTIONSNEDSATTNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.MAX_LENGTH_GRUND_FOR_BEDOMNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.MAX_LENGTH_MEDICINSK_BEHANDLING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.MAX_LENGTH_OVRIGT;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.MAX_LENGTH_YRKE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.MEDICINSK_BEHANDLING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.MEDICINSK_BEHANDLING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.OVERFLOW_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.OVERFLOW_LABEL_AKTIVITETSBEGRANSNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.OVERFLOW_LABEL_ATGARDER;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.OVERFLOW_LABEL_FUNKTIONSNEDSATTNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.OVERFLOW_LABEL_GRUND_FOR_BEDOMNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.OVERFLOW_LABEL_MEDICINSK_BEHANDLING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.OVERFLOW_LABEL_OVRIGT;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.OVERFLOW_LABEL_YRKE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.OVRIGT;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.OVRIGT_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.PATIENT_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.PATIENT_ID_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.PATIENT_ID_FIELD_ID_2;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.PATIENT_ID_FIELD_ID_3;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.PATIENT_ID_FIELD_ID_4;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.PATIENT_ID_FIELD_ID_5;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.PA_TITLE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.PA_TITLE_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.PROGNOS_RADIO_GROUP_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.SIGNED_BY_NAME_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.SIGNED_DATE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.SIGNED_DATE_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.SPECIALTY;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.SPECIALTY_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.WORKPLACE_CODE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.WORKPLACE_CODE_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.YRKE_ARBETSUPPGIFTER;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.YRKE_ARBETSUPPGIFTER_FIELD_ID;

import java.util.LinkedHashMap;
import java.util.Map;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfFieldDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.OverflowConfigDTO;

public class TestSetupFK7804Fields {

  private TestSetupFK7804Fields() {
    throw new IllegalStateException("Utility class");
  }

  public static Map<String, CustomPdfFieldDTO> allFields() {
    final var fields = new LinkedHashMap<String, CustomPdfFieldDTO>();

    fields.put(PATIENT_ID_FIELD_ID, CustomPdfFieldDTO.builder().value(PATIENT_ID).build());
    fields.put(PATIENT_ID_FIELD_ID_2, CustomPdfFieldDTO.builder().value(PATIENT_ID).build());
    fields.put(PATIENT_ID_FIELD_ID_3, CustomPdfFieldDTO.builder().value(PATIENT_ID).build());
    fields.put(PATIENT_ID_FIELD_ID_4, CustomPdfFieldDTO.builder().value(PATIENT_ID).build());
    fields.put(PATIENT_ID_FIELD_ID_5, CustomPdfFieldDTO.builder().value(PATIENT_ID).build());

    // Grund för medicinskt underlag
    fields.put(
        CHECKBOX_FYSISK_UNDERSOKNING_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());
    fields.put(
        DATE_FYSISK_UNDERSOKNING_FIELD_ID,
        CustomPdfFieldDTO.builder().value(DATE_FYSISK_UNDERSOKNING).build());
    fields.put(
        CHECKBOX_DIGITAL_UNDERSOKNING_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());
    fields.put(
        DATE_DIGITAL_UNDERSOKNING_FIELD_ID,
        CustomPdfFieldDTO.builder().value(DATE_DIGITAL_UNDERSOKNING).build());
    fields.put(CHECKBOX_TELEFONKONTAKT_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());
    fields.put(
        DATE_TELEFONKONTAKT_FIELD_ID,
        CustomPdfFieldDTO.builder().value(DATE_TELEFONKONTAKT).build());
    fields.put(CHECKBOX_JOURNALUPPGIFTER_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());
    fields.put(
        DATE_JOURNALUPPGIFTER_FIELD_ID,
        CustomPdfFieldDTO.builder().value(DATE_JOURNALUPPGIFTER).build());
    fields.put(CHECKBOX_ANNAT_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());
    fields.put(DATE_ANNAT_FIELD_ID, CustomPdfFieldDTO.builder().value(DATE_ANNAT).build());

    // Annan grund
    fields.put(ANNAN_GRUND_FIELD_ID, CustomPdfFieldDTO.builder().value(ANNAN_GRUND).build());

    // Sysselsättning
    fields.put(CHECKBOX_NUVARANDE_ARBETE_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());
    fields.put(CHECKBOX_ARBETSSOKANDE_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());
    fields.put(CHECKBOX_FORALDRALEDIGHET_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());
    fields.put(CHECKBOX_STUDIER_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());

    // Smittbärarpenning
    fields.put(CHECKBOX_SMITTBARARPENNING_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());

    fields.put(
        YRKE_ARBETSUPPGIFTER_FIELD_ID,
        CustomPdfFieldDTO.builder()
            .value(YRKE_ARBETSUPPGIFTER)
            .maxLength(MAX_LENGTH_YRKE)
            .shouldRemoveLineBreaks(true)
            .overflow(
                OverflowConfigDTO.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_YRKE)
                    .build())
            .build());

    fields.put(
        DIAGNOS_1_NAME_FIELD_ID,
        CustomPdfFieldDTO.builder().value(DIAGNOS_1_NAME).shouldRemoveLineBreaks(true).build());
    fields.put(
        DIAGNOS_1_CODE_1_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_1_CODE.charAt(0))).build());
    fields.put(
        DIAGNOS_1_CODE_2_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_1_CODE.charAt(1))).build());
    fields.put(
        DIAGNOS_1_CODE_3_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_1_CODE.charAt(2))).build());
    fields.put(
        DIAGNOS_1_CODE_4_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_1_CODE.charAt(3))).build());
    fields.put(
        DIAGNOS_1_CODE_5_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_1_CODE.charAt(4))).build());
    fields.put(
        DIAGNOS_2_NAME_FIELD_ID,
        CustomPdfFieldDTO.builder().value(DIAGNOS_2_NAME).shouldRemoveLineBreaks(true).build());
    fields.put(
        DIAGNOS_2_CODE_1_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_2_CODE.charAt(0))).build());
    fields.put(
        DIAGNOS_2_CODE_2_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_2_CODE.charAt(1))).build());
    fields.put(
        DIAGNOS_2_CODE_3_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_2_CODE.charAt(2))).build());
    fields.put(
        DIAGNOS_2_CODE_4_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_2_CODE.charAt(3))).build());
    fields.put(
        DIAGNOS_3_NAME_FIELD_ID,
        CustomPdfFieldDTO.builder().value(DIAGNOS_3_NAME).shouldRemoveLineBreaks(true).build());
    fields.put(
        DIAGNOS_3_CODE_1_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_3_CODE.charAt(0))).build());
    fields.put(
        DIAGNOS_3_CODE_2_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_3_CODE.charAt(1))).build());
    fields.put(
        DIAGNOS_3_CODE_3_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_3_CODE.charAt(2))).build());
    fields.put(
        DIAGNOS_3_CODE_4_FIELD_ID,
        CustomPdfFieldDTO.builder().value(String.valueOf(DIAGNOS_3_CODE.charAt(3))).build());

    // Funktionsnedsättning
    fields.put(
        FUNKTIONSNEDSATTNING_FIELD_ID,
        CustomPdfFieldDTO.builder()
            .value(FUNKTIONSNEDSATTNING)
            .maxLength(MAX_LENGTH_FUNKTIONSNEDSATTNING)
            .shouldRemoveLineBreaks(true)
            .overflow(
                OverflowConfigDTO.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_FUNKTIONSNEDSATTNING)
                    .build())
            .build());

    // Aktivitetsbegränsning
    fields.put(
        AKTIVITETSBEGRANSNING_FIELD_ID,
        CustomPdfFieldDTO.builder()
            .value(AKTIVITETSBEGRANSNING)
            .maxLength(MAX_LENGTH_AKTIVITETSBEGRANSNING)
            .overflow(
                OverflowConfigDTO.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_AKTIVITETSBEGRANSNING)
                    .build())
            .build());

    // Medicinsk behandling
    fields.put(
        MEDICINSK_BEHANDLING_FIELD_ID,
        CustomPdfFieldDTO.builder()
            .value(MEDICINSK_BEHANDLING)
            .maxLength(MAX_LENGTH_MEDICINSK_BEHANDLING)
            .overflow(
                OverflowConfigDTO.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_MEDICINSK_BEHANDLING)
                    .build())
            .build());

    // Nedsatt arbetsförmåga
    fields.put(CHECKBOX_100_PROCENT_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());
    fields.put(DATE_100_FROM_FIELD_ID, CustomPdfFieldDTO.builder().value(DATE_100_FROM).build());
    fields.put(DATE_100_TO_FIELD_ID, CustomPdfFieldDTO.builder().value(DATE_100_TO).build());
    fields.put(CHECKBOX_75_PROCENT_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());
    fields.put(DATE_75_FROM_FIELD_ID, CustomPdfFieldDTO.builder().value(DATE_75_FROM).build());
    fields.put(DATE_75_TO_FIELD_ID, CustomPdfFieldDTO.builder().value(DATE_75_TO).build());
    fields.put(CHECKBOX_50_PROCENT_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());
    fields.put(DATE_50_FROM_FIELD_ID, CustomPdfFieldDTO.builder().value(DATE_50_FROM).build());
    fields.put(DATE_50_TO_FIELD_ID, CustomPdfFieldDTO.builder().value(DATE_50_TO).build());
    fields.put(CHECKBOX_25_PROCENT_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());
    fields.put(DATE_25_FROM_FIELD_ID, CustomPdfFieldDTO.builder().value(DATE_25_FROM).build());
    fields.put(DATE_25_TO_FIELD_ID, CustomPdfFieldDTO.builder().value(DATE_25_TO).build());

    // Prognos
    fields.put(PROGNOS_RADIO_GROUP_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());

    // Åtgärder
    fields.put(
        ATGARDER_FIELD_ID,
        CustomPdfFieldDTO.builder()
            .value(ATGARDER)
            .maxLength(MAX_LENGTH_ATGARDER)
            .overflow(
                OverflowConfigDTO.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_ATGARDER)
                    .build())
            .build());

    // Övrigt
    fields.put(
        OVRIGT_FIELD_ID,
        CustomPdfFieldDTO.builder()
            .value(OVRIGT)
            .maxLength(MAX_LENGTH_OVRIGT)
            .overflow(
                OverflowConfigDTO.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_OVRIGT)
                    .build())
            .build());

    // Grund för bedömning
    fields.put(
        GRUND_FOR_BEDOMNING_FIELD_ID,
        CustomPdfFieldDTO.builder()
            .value(GRUND_FOR_BEDOMNING)
            .maxLength(MAX_LENGTH_GRUND_FOR_BEDOMNING)
            .overflow(
                OverflowConfigDTO.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_GRUND_FOR_BEDOMNING)
                    .build())
            .build());

    // Kontakt
    fields.put(CHECKBOX_KONTAKT_FIELD_ID, CustomPdfFieldDTO.builder().value("1").build());

    // Signature fields
    fields.put(SIGNED_DATE_FIELD_ID, CustomPdfFieldDTO.builder().value(SIGNED_DATE).build());
    fields.put(SIGNED_BY_NAME_FIELD_ID, CustomPdfFieldDTO.builder().value(DOCTOR_NAME).build());
    fields.put(PA_TITLE_FIELD_ID, CustomPdfFieldDTO.builder().value(PA_TITLE).build());
    fields.put(SPECIALTY_FIELD_ID, CustomPdfFieldDTO.builder().value(SPECIALTY).build());
    fields.put(HSA_ID_FIELD_ID, CustomPdfFieldDTO.builder().value(HSA_ID).build());
    fields.put(WORKPLACE_CODE_FIELD_ID, CustomPdfFieldDTO.builder().value(WORKPLACE_CODE).build());
    fields.put(
        CONTACT_INFORMATION_FIELD_ID,
        CustomPdfFieldDTO.builder().value(CONTACT_INFORMATION).build());

    return fields;
  }

  public static Map<String, CustomPdfFieldDTO> fieldsWithSingleOverflow() {
    final var fields = new LinkedHashMap<String, CustomPdfFieldDTO>();
    fields.put(PATIENT_ID_FIELD_ID, CustomPdfFieldDTO.builder().value(PATIENT_ID).build());
    fields.put(
        FUNKTIONSNEDSATTNING_FIELD_ID,
        CustomPdfFieldDTO.builder()
            .value(
                generateLongText(
                    "Patienten har en nedsatt funktion i höger knä som påverkar "
                        + "rörelseförmågan. ",
                    MAX_LENGTH_FUNKTIONSNEDSATTNING + 200))
            .maxLength(MAX_LENGTH_FUNKTIONSNEDSATTNING)
            .overflow(
                OverflowConfigDTO.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel("Funktionsnedsättning")
                    .build())
            .build());
    return fields;
  }

  public static Map<String, CustomPdfFieldDTO> fieldsWithMultipleOverflows() {
    final var fields = new LinkedHashMap<String, CustomPdfFieldDTO>();
    fields.put(PATIENT_ID_FIELD_ID, CustomPdfFieldDTO.builder().value(PATIENT_ID).build());
    fields.put(
        FUNKTIONSNEDSATTNING_FIELD_ID,
        CustomPdfFieldDTO.builder()
            .value(
                generateLongText(
                    "Patienten har en nedsatt funktion i höger knä som påverkar "
                        + "rörelseförmågan väsentligt. ",
                    MAX_LENGTH_FUNKTIONSNEDSATTNING + 300))
            .maxLength(MAX_LENGTH_FUNKTIONSNEDSATTNING)
            .overflow(
                OverflowConfigDTO.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel("Funktionsnedsättning")
                    .build())
            .build());
    fields.put(
        AKTIVITETSBEGRANSNING_FIELD_ID,
        CustomPdfFieldDTO.builder()
            .value(
                generateLongText(
                    "Patienten kan inte stå eller gå längre sträckor utan smärta "
                        + "vilket begränsar dagliga aktiviteter. ",
                    MAX_LENGTH_AKTIVITETSBEGRANSNING + 250))
            .maxLength(MAX_LENGTH_AKTIVITETSBEGRANSNING)
            .overflow(
                OverflowConfigDTO.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel("Aktivitetsbegränsning")
                    .build())
            .build());
    return fields;
  }

  public static Map<String, CustomPdfFieldDTO> fieldsWithOverflowAndLineBreaks() {
    final var fields = new LinkedHashMap<String, CustomPdfFieldDTO>();
    fields.put(PATIENT_ID_FIELD_ID, CustomPdfFieldDTO.builder().value(PATIENT_ID).build());

    final var valueWithLineBreaks =
        "Patienten har en nedsatt funktion i höger knä.\n"
            + "Rörelseförmågan är begränsad.\n"
            + "Smärta vid belastning.\n"
            + "Rörelseförmågan är begränsad.\n"
            + "Smärta vid belastning.\n"
            + "Rörelseförmågan är begränsad.\n"
            + "Smärta vid belastning.\n"
            + "Rörelseförmågan är begränsad.\n"
            + "Smärta vid belastning.\n"
            + "Rörelseförmågan är begränsad.\n"
            + "Smärta vid belastning.\n"
            + "Rörelseförmågan är begränsad.\n"
            + "Smärta vid belastning.\n"
            + "Rörelseförmågan är begränsad.\n"
            + "Smärta vid belastning.\n"
            + "Rörelseförmågan är begränsad.\n"
            + "Smärta vid belastning.\n"
            + "Rörelseförmågan är begränsad.\n"
            + "Smärta vid belastning.\n"
            + "Rörelseförmågan är begränsad.\n"
            + "Smärta vid belastning.\n"
            + "Rörelseförmågan är begränsad.\n"
            + "Smärta vid belastning.\n"
            + generateLongText(
                "Ytterligare bedömning visar att patienten har svårigheter med "
                    + "vardagliga aktiviteter. ",
                MAX_LENGTH_FUNKTIONSNEDSATTNING + 100);

    fields.put(
        FUNKTIONSNEDSATTNING_FIELD_ID,
        CustomPdfFieldDTO.builder()
            .value(valueWithLineBreaks)
            .maxLength(MAX_LENGTH_FUNKTIONSNEDSATTNING)
            .shouldRemoveLineBreaks(true)
            .overflow(
                OverflowConfigDTO.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel("Funktionsnedsättning")
                    .build())
            .build());
    return fields;
  }

  public static Map<String, CustomPdfFieldDTO> fieldsWithOverflowNoLineBreakRemoval() {
    final var fields = new LinkedHashMap<String, CustomPdfFieldDTO>();
    fields.put(PATIENT_ID_FIELD_ID, CustomPdfFieldDTO.builder().value(PATIENT_ID).build());

    final var valueWithLineBreaks =
        "Patienten har en nedsatt funktion i höger knä.\n"
            + "Rörelseförmågan är begränsad.\n"
            + "Smärta vid belastning.\n"
            + generateLongText(
                "Ytterligare bedömning visar svårigheter. ", MAX_LENGTH_FUNKTIONSNEDSATTNING + 50);

    fields.put(
        FUNKTIONSNEDSATTNING_FIELD_ID,
        CustomPdfFieldDTO.builder()
            .value(valueWithLineBreaks)
            .maxLength(MAX_LENGTH_FUNKTIONSNEDSATTNING)
            .shouldRemoveLineBreaks(false)
            .overflow(
                OverflowConfigDTO.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel("Funktionsnedsättning")
                    .build())
            .build());
    return fields;
  }

  private static String generateLongText(String sentence, int minLength) {
    final var builder = new StringBuilder();
    while (builder.length() < minLength) {
      builder.append(sentence);
    }
    return builder.toString();
  }
}
