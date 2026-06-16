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

import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.AKTIVITETSBEGRANSNING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.FUNKTIONSNEDSATTNING_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.MAX_LENGTH_AKTIVITETSBEGRANSNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.MAX_LENGTH_FUNKTIONSNEDSATTNING;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.OVERFLOW_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.PATIENT_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7804.TestSetupFK7804Constants.PATIENT_ID_FIELD_ID;

import java.util.LinkedHashMap;
import java.util.Map;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfFieldDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.OverflowConfigDTO;

public class TestSetupFK7804Fields {

  private TestSetupFK7804Fields() {
    throw new IllegalStateException("Utility class");
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
