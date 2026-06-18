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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.OverflowConfig;

/**
 * Realistic FK7804 (Läkarintyg för sjukpenning) PDF field test data.
 *
 * <p>Field IDs correspond to the AcroForm fields in the FK7804 PDF template as defined by {@code
 * FK7804PdfSpecification} in the certificate-service. Values are realistic examples for a fully
 * filled-in certificate ready for printing.
 */
public class TestDataFK7804Fields {

  // --- Patient ID fields (5 pages) ---

  public static final String PATIENT_ID_FIELD_ID_1 = "form1[0].#subform[0].flt_txtPersonNr[0]";
  public static final String PATIENT_ID_FIELD_ID_2 = "form1[0].Sida2[0].flt_txtPersonNr[0]";
  public static final String PATIENT_ID_FIELD_ID_3 = "form1[0].Sida3[0].flt_txtPersonNr[0]";
  public static final String PATIENT_ID_FIELD_ID_4 = "form1[0].Sida4[0].flt_txtPersonNr[0]";
  public static final String PATIENT_ID_FIELD_ID_5 = "form1[0].#subform[4].flt_txtPersonNr[1]";

  // --- Signature fields ---

  public static final String SIGNED_DATE_FIELD_ID = "form1[0].Sida4[0].flt_datUnderskrift[0]";
  public static final String SIGNED_BY_NAME_FIELD_ID =
      "form1[0].Sida4[0].flt_txtNamnfortydligande[0]";
  public static final String PA_TITLE_FIELD_ID = "form1[0].Sida4[0].flt_txtBefattning[0]";
  public static final String SPECIALTY_FIELD_ID =
      "form1[0].Sida4[0].flt_txtEventuellSpecialistkompetens[0]";
  public static final String HSA_ID_FIELD_ID = "form1[0].Sida4[0].flt_txtLakarensHSA-ID[0]";
  public static final String WORKPLACE_CODE_FIELD_ID = "form1[0].Sida4[0].flt_txtArbetsplatskod[0]";
  public static final String CONTACT_INFORMATION_FIELD_ID =
      "form1[0].Sida4[0].flt_txtVardgivarensNamnAdressTelefon[0]";

  // --- Grund för medicinskt underlag (date checkbox fields) ---

  public static final String CHECKBOX_FYSISK_UNDERSOKNING_FIELD_ID =
      "form1[0].#subform[0].ksr_FysisktVardmote[0]";
  public static final String DATE_FYSISK_UNDERSOKNING_FIELD_ID =
      "form1[0].#subform[0].flt_datumPatient[0]";
  public static final String CHECKBOX_DIGITAL_UNDERSOKNING_FIELD_ID =
      "form1[0].#subform[0].ksr_DigitalVardmote[0]";
  public static final String DATE_DIGITAL_UNDERSOKNING_FIELD_ID =
      "form1[0].#subform[0].flt_datumDigitaltVardmöte[0]";
  public static final String CHECKBOX_TELEFONKONTAKT_FIELD_ID =
      "form1[0].#subform[0].ksr_TelefonkontaktPatienten[0]";
  public static final String DATE_TELEFONKONTAKT_FIELD_ID =
      "form1[0].#subform[0].flt_datumTelefonkontakt[0]";
  public static final String CHECKBOX_JOURNALUPPGIFTER_FIELD_ID =
      "form1[0].#subform[0].ksr_Journaluppgifter[0]";
  public static final String DATE_JOURNALUPPGIFTER_FIELD_ID =
      "form1[0].#subform[0].flt_datumJournaluppgifter[0]";
  public static final String CHECKBOX_ANNAT_FIELD_ID = "form1[0].#subform[0].ksr_Annat[0]";
  public static final String DATE_ANNAT_FIELD_ID = "form1[0].#subform[0].flt_datumAnnat[0]";

  // --- Annan grund ---

  public static final String ANNAN_GRUND_FIELD_ID = "form1[0].#subform[0].flt_txtAnnatAngeVad[0]";

  // --- Sysselsättning (checkbox fields) ---

  public static final String CHECKBOX_NUVARANDE_ARBETE_FIELD_ID =
      "form1[0].#subform[0].ksr_NuvarandeArbete[0]";
  public static final String CHECKBOX_ARBETSSOKANDE_FIELD_ID =
      "form1[0].#subform[0].ksr_Arbetssokande[0]";
  public static final String CHECKBOX_FORALDRALEDIGHET_FIELD_ID =
      "form1[0].#subform[0].ksr_Foraldraledighet[0]";
  public static final String CHECKBOX_STUDIER_FIELD_ID = "form1[0].#subform[0].ksr_Studier[0]";

  // --- Yrke och arbetsuppgifter ---

  public static final String YRKE_ARBETSUPPGIFTER_FIELD_ID =
      "form1[0].#subform[0].flt_txtYrkeArbetsuppgifter[0]";

  // --- Smittbärarpenning ---

  public static final String CHECKBOX_SMITTBARARPENNING_FIELD_ID =
      "form1[0].#subform[0].ksr_AvstangningSmittskyddslagen[0]";

  // --- Diagnos fields ---

  public static final String DIAGNOS_1_NAME_FIELD_ID = "form1[0].#subform[0].flt_txtDiagnoser[0]";
  public static final String DIAGNOS_1_CODE_1_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod1[0]";
  public static final String DIAGNOS_1_CODE_2_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod2[0]";
  public static final String DIAGNOS_1_CODE_3_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod3[0]";
  public static final String DIAGNOS_1_CODE_4_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod4[0]";
  public static final String DIAGNOS_1_CODE_5_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod5[0]";
  public static final String DIAGNOS_2_NAME_FIELD_ID = "form1[0].#subform[0].flt_txtDiagnoser2[0]";
  public static final String DIAGNOS_2_CODE_1_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod6[0]";
  public static final String DIAGNOS_2_CODE_2_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod7[0]";
  public static final String DIAGNOS_2_CODE_3_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod8[0]";
  public static final String DIAGNOS_2_CODE_4_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod9[0]";
  public static final String DIAGNOS_2_CODE_5_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod10[0]";
  public static final String DIAGNOS_3_NAME_FIELD_ID = "form1[0].#subform[0].flt_txtDiagnoser3[0]";
  public static final String DIAGNOS_3_CODE_1_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod11[0]";
  public static final String DIAGNOS_3_CODE_2_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod12[0]";
  public static final String DIAGNOS_3_CODE_3_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod13[0]";
  public static final String DIAGNOS_3_CODE_4_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod14[0]";
  public static final String DIAGNOS_3_CODE_5_FIELD_ID = "form1[0].#subform[0].flt_txtDiaKod15[0]";

  // --- Text fields with overflow ---

  public static final String FUNKTIONSNEDSATTNING_FIELD_ID =
      "form1[0].Sida2[0].flt_txtBeskrivUndersokningsfynd[0]";
  public static final String AKTIVITETSBEGRANSNING_FIELD_ID =
      "form1[0].Sida2[0].flt_txtBeskrivAktivitetsbegransning[0]";
  public static final String MEDICINSK_BEHANDLING_FIELD_ID =
      "form1[0].Sida2[0].flt_txtPagandeMedicinskBehandling[0]";
  public static final String OVRIGT_FIELD_ID = "form1[0].Sida3[0].flt_txtOvrigaUpplysningarl[0]";
  public static final String ATGARDER_FIELD_ID =
      "form1[0].Sida3[0].flt_txtArbetslivsinriktadAtgarderUnderlatta[0]";
  public static final String GRUND_FOR_BEDOMNING_FIELD_ID =
      "form1[0].Sida3[0].flt_txtAlternativFyra[0]";

  // --- Overflow sheet field ---

  public static final String OVERFLOW_FIELD_ID = "form1[0].#subform[4].flt_txtFortsattningsblad[0]";

  // --- MaxLength constants (rows × PDF_TEXT_FIELD_ROW_LENGTH where row length = 53) ---

  public static final int PDF_TEXT_FIELD_ROW_LENGTH = 53;
  public static final int MAX_LENGTH_FUNKTIONSNEDSATTNING = 11 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_AKTIVITETSBEGRANSNING = 12 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_MEDICINSK_BEHANDLING = 8 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_OVRIGT = 8 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_ATGARDER = 7 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_YRKE = 3 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_GRUND_FOR_BEDOMNING = 2 * PDF_TEXT_FIELD_ROW_LENGTH;

  // --- Date range fields (Nedsatt arbetsförmåga) ---

  public static final String CHECKBOX_100_PROCENT_FIELD_ID = "form1[0].Sida2[0].ksr_100procent[0]";
  public static final String DATE_100_FROM_FIELD_ID = "form1[0].Sida2[0].flt_datumFranMed[0]";
  public static final String DATE_100_TO_FIELD_ID = "form1[0].Sida2[0].flt_datumtTillMed[0]";
  public static final String CHECKBOX_75_PROCENT_FIELD_ID = "form1[0].Sida2[0].ksr_75procent[0]";
  public static final String DATE_75_FROM_FIELD_ID =
      "form1[0].Sida2[0].flt_datumFranMed75Procent[0]";
  public static final String DATE_75_TO_FIELD_ID = "form1[0].Sida2[0].flt_datumTillMed75Procent[0]";
  public static final String CHECKBOX_50_PROCENT_FIELD_ID = "form1[0].Sida2[0].ksr_50procent[0]";
  public static final String DATE_50_FROM_FIELD_ID =
      "form1[0].Sida2[0].flt_datumFranMed50Procent[0]";
  public static final String DATE_50_TO_FIELD_ID = "form1[0].Sida2[0].flt_datumTillMed50Procent[0]";
  public static final String CHECKBOX_25_PROCENT_FIELD_ID = "form1[0].Sida2[0].ksr_25procent[0]";
  public static final String DATE_25_FROM_FIELD_ID =
      "form1[0].Sida2[0].flt_datumFranMed25Procent[0]";
  public static final String DATE_25_TO_FIELD_ID = "form1[0].Sida2[0].flt_datumTillMed25Procent[0]";

  // --- Radio button field (Prognos) ---

  public static final String PROGNOS_RADIO_GROUP_FIELD_ID = "form1[0].Sida3[0].RadioButtonList4[0]";

  // --- Kontakt ---

  public static final String CHECKBOX_KONTAKT_FIELD_ID =
      "form1[0].Sida4[0].ksr_ForsakringskassanKontakar[0]";

  // --- Realistic value constants ---

  public static final String PATIENT_ID = "194011306125";
  public static final String SIGNED_DATE = "2025-06-15";
  public static final String DOCTOR_NAME = "Erik Johansson";
  public static final String PA_TITLE = "Läkare";
  public static final String SPECIALTY = "Ortopedi";
  public static final String HSA_ID = "SE2321000016-A1B2";
  public static final String WORKPLACE_CODE = "7654321";
  public static final String CONTACT_INFORMATION =
      "Ortopedkliniken\nSjukhusvägen 10\n123 45 Göteborg\nTel: 031-987 654 32";

  public static final String DATE_FYSISK_UNDERSOKNING = "2025-06-10";
  public static final String DATE_TELEFONKONTAKT = "2025-06-08";

  public static final String ANNAN_GRUND = "Rehabiliteringsplan från fysioterapeut";

  public static final String YRKE_ARBETSUPPGIFTER =
      "Lagerarbetare med tunga lyft och repetitiva arbetsmoment";

  public static final String DIAGNOS_1_NAME = "Lumbal diskbråck";
  public static final String DIAGNOS_1_CODE = "M5116";
  public static final String DIAGNOS_2_NAME = "Ischias";
  public static final String DIAGNOS_2_CODE = "M541";

  public static final String FUNKTIONSNEDSATTNING =
      "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta.";

  public static final String AKTIVITETSBEGRANSNING =
      "Patienten kan inte utföra tunga lyft (>5 kg). Kan inte sitta stillasittande längre "
          + "perioder. Svårigheter att böja sig och vrida överkroppen. Kan inte köra bil längre "
          + "sträckor på grund av smärta. Begränsad förmåga att gå längre sträckor.";

  public static final String MEDICINSK_BEHANDLING =
      "Smärtlindring med paracetamol och NSAID. Remiss till fysioterapi för "
          + "ryggstabiliserande träning. Uppföljning hos ortoped om 6 veckor. "
          + "Vid utebliven förbättring övervägs MR-undersökning.";

  public static final String OVRIGT =
      "Patienten är motiverad till rehabilitering och har god insikt i sin situation.";

  public static final String ATGARDER =
      "Arbetsanpassning med ergonomisk arbetsplats. Möjlighet till stående arbetsposition. "
          + "Undvikande av tunga lyft under rehabiliteringsperioden.";

  public static final String GRUND_FOR_BEDOMNING =
      "Baserat på klinisk undersökning och patientens beskrivning av symtom.";

  // --- Overflow labels (question names displayed in continuation page) ---

  public static final String OVERFLOW_LABEL_FUNKTIONSNEDSATTNING = "Funktionsnedsättning";
  public static final String OVERFLOW_LABEL_AKTIVITETSBEGRANSNING = "Aktivitetsbegränsning";
  public static final String OVERFLOW_LABEL_MEDICINSK_BEHANDLING = "Medicinsk behandling";
  public static final String OVERFLOW_LABEL_OVRIGT = "Övriga upplysningar";
  public static final String OVERFLOW_LABEL_ATGARDER = "Åtgärder för att främja återgång";
  public static final String OVERFLOW_LABEL_YRKE = "Yrke och arbetsuppgifter";
  public static final String OVERFLOW_LABEL_GRUND_FOR_BEDOMNING = "Grund för bedömning";

  public static final String DATE_100_FROM = "2025-06-15";
  public static final String DATE_100_TO = "2025-07-15";
  public static final String DATE_50_FROM = "2025-07-16";
  public static final String DATE_50_TO = "2025-08-15";

  private TestDataFK7804Fields() {
    throw new IllegalStateException("Utility class");
  }

  public static final String TAGGED_PDF_OVERFLOW_RESOURCE = "/tagged-test-template-overflow.pdf";
  public static PDDocument pdDocument;

  static {
    try (InputStream stream =
        TestDataFK7804Fields.class.getResourceAsStream(TAGGED_PDF_OVERFLOW_RESOURCE)) {
      if (stream == null) {
        throw new IllegalStateException(
            "Could not load PDF resource: " + TAGGED_PDF_OVERFLOW_RESOURCE);
      }
      pdDocument = Loader.loadPDF(stream.readAllBytes());
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load PDF document", e);
    }
  }

  /**
   * Returns a LinkedHashMap of all FK7804 AcroForm field IDs to their realistic test values, as
   * they would appear in a fully filled-in certificate ready for printing. The map preserves
   * insertion order as required by the custom PDF processing specification.
   */
  public static Map<String, CustomPdfField> fk7804Fields() {
    final var fields = new LinkedHashMap<String, CustomPdfField>();

    // Patient ID on all pages
    fields.put(PATIENT_ID_FIELD_ID_1, CustomPdfField.builder().value(PATIENT_ID).build());
    fields.put(PATIENT_ID_FIELD_ID_2, CustomPdfField.builder().value(PATIENT_ID).build());
    fields.put(PATIENT_ID_FIELD_ID_3, CustomPdfField.builder().value(PATIENT_ID).build());
    fields.put(PATIENT_ID_FIELD_ID_4, CustomPdfField.builder().value(PATIENT_ID).build());
    fields.put(PATIENT_ID_FIELD_ID_5, CustomPdfField.builder().value(PATIENT_ID).build());

    // Grund för medicinskt underlag
    fields.put(CHECKBOX_FYSISK_UNDERSOKNING_FIELD_ID, CustomPdfField.builder().value("1").build());
    fields.put(
        DATE_FYSISK_UNDERSOKNING_FIELD_ID,
        CustomPdfField.builder().value(DATE_FYSISK_UNDERSOKNING).build());
    fields.put(CHECKBOX_TELEFONKONTAKT_FIELD_ID, CustomPdfField.builder().value("1").build());
    fields.put(
        DATE_TELEFONKONTAKT_FIELD_ID, CustomPdfField.builder().value(DATE_TELEFONKONTAKT).build());

    // Sysselsättning
    fields.put(CHECKBOX_NUVARANDE_ARBETE_FIELD_ID, CustomPdfField.builder().value("1").build());

    // Yrke
    fields.put(
        YRKE_ARBETSUPPGIFTER_FIELD_ID,
        CustomPdfField.builder()
            .value(YRKE_ARBETSUPPGIFTER)
            .maxLength(MAX_LENGTH_YRKE)
            .overflow(
                OverflowConfig.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_YRKE)
                    .build())
            .build());

    // Diagnos
    fields.put(
        DIAGNOS_1_NAME_FIELD_ID,
        CustomPdfField.builder().value(DIAGNOS_1_NAME).shouldRemoveLineBreaks(true).build());
    fields.put(
        DIAGNOS_1_CODE_1_FIELD_ID,
        CustomPdfField.builder().value(String.valueOf(DIAGNOS_1_CODE.charAt(0))).build());
    fields.put(
        DIAGNOS_1_CODE_2_FIELD_ID,
        CustomPdfField.builder().value(String.valueOf(DIAGNOS_1_CODE.charAt(1))).build());
    fields.put(
        DIAGNOS_1_CODE_3_FIELD_ID,
        CustomPdfField.builder().value(String.valueOf(DIAGNOS_1_CODE.charAt(2))).build());
    fields.put(
        DIAGNOS_1_CODE_4_FIELD_ID,
        CustomPdfField.builder().value(String.valueOf(DIAGNOS_1_CODE.charAt(3))).build());
    fields.put(
        DIAGNOS_1_CODE_5_FIELD_ID,
        CustomPdfField.builder().value(String.valueOf(DIAGNOS_1_CODE.charAt(4))).build());
    fields.put(
        DIAGNOS_2_NAME_FIELD_ID,
        CustomPdfField.builder().value(DIAGNOS_2_NAME).shouldRemoveLineBreaks(true).build());
    fields.put(
        DIAGNOS_2_CODE_1_FIELD_ID,
        CustomPdfField.builder().value(String.valueOf(DIAGNOS_2_CODE.charAt(0))).build());
    fields.put(
        DIAGNOS_2_CODE_2_FIELD_ID,
        CustomPdfField.builder().value(String.valueOf(DIAGNOS_2_CODE.charAt(1))).build());
    fields.put(
        DIAGNOS_2_CODE_3_FIELD_ID,
        CustomPdfField.builder().value(String.valueOf(DIAGNOS_2_CODE.charAt(2))).build());
    fields.put(
        DIAGNOS_2_CODE_4_FIELD_ID,
        CustomPdfField.builder().value(String.valueOf(DIAGNOS_2_CODE.charAt(3))).build());

    // Funktionsnedsättning
    fields.put(
        FUNKTIONSNEDSATTNING_FIELD_ID,
        CustomPdfField.builder()
            .value(FUNKTIONSNEDSATTNING)
            .maxLength(MAX_LENGTH_FUNKTIONSNEDSATTNING)
            .overflow(
                OverflowConfig.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_FUNKTIONSNEDSATTNING)
                    .build())
            .build());

    // Aktivitetsbegränsning
    fields.put(
        AKTIVITETSBEGRANSNING_FIELD_ID,
        CustomPdfField.builder()
            .value(AKTIVITETSBEGRANSNING)
            .maxLength(MAX_LENGTH_AKTIVITETSBEGRANSNING)
            .overflow(
                OverflowConfig.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_AKTIVITETSBEGRANSNING)
                    .build())
            .build());

    // Medicinsk behandling
    fields.put(
        MEDICINSK_BEHANDLING_FIELD_ID,
        CustomPdfField.builder()
            .value(MEDICINSK_BEHANDLING)
            .maxLength(MAX_LENGTH_MEDICINSK_BEHANDLING)
            .overflow(
                OverflowConfig.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_MEDICINSK_BEHANDLING)
                    .build())
            .build());

    // Nedsatt arbetsförmåga
    fields.put(CHECKBOX_100_PROCENT_FIELD_ID, CustomPdfField.builder().value("1").build());
    fields.put(DATE_100_FROM_FIELD_ID, CustomPdfField.builder().value(DATE_100_FROM).build());
    fields.put(DATE_100_TO_FIELD_ID, CustomPdfField.builder().value(DATE_100_TO).build());
    fields.put(CHECKBOX_50_PROCENT_FIELD_ID, CustomPdfField.builder().value("1").build());
    fields.put(DATE_50_FROM_FIELD_ID, CustomPdfField.builder().value(DATE_50_FROM).build());
    fields.put(DATE_50_TO_FIELD_ID, CustomPdfField.builder().value(DATE_50_TO).build());

    // Prognos
    fields.put(PROGNOS_RADIO_GROUP_FIELD_ID, CustomPdfField.builder().value("1").build());

    // Åtgärder
    fields.put(
        ATGARDER_FIELD_ID,
        CustomPdfField.builder()
            .value(ATGARDER)
            .maxLength(MAX_LENGTH_ATGARDER)
            .overflow(
                OverflowConfig.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_ATGARDER)
                    .build())
            .build());

    // Övrigt
    fields.put(
        OVRIGT_FIELD_ID,
        CustomPdfField.builder()
            .value(OVRIGT)
            .maxLength(MAX_LENGTH_OVRIGT)
            .overflow(
                OverflowConfig.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_OVRIGT)
                    .build())
            .build());

    // Grund för bedömning
    fields.put(
        GRUND_FOR_BEDOMNING_FIELD_ID,
        CustomPdfField.builder()
            .value(GRUND_FOR_BEDOMNING)
            .maxLength(MAX_LENGTH_GRUND_FOR_BEDOMNING)
            .overflow(
                OverflowConfig.builder()
                    .overflowFieldId(OVERFLOW_FIELD_ID)
                    .overflowLabel(OVERFLOW_LABEL_GRUND_FOR_BEDOMNING)
                    .build())
            .build());

    // Kontakt
    fields.put(CHECKBOX_KONTAKT_FIELD_ID, CustomPdfField.builder().value("1").build());

    // Signature fields
    fields.put(SIGNED_DATE_FIELD_ID, CustomPdfField.builder().value(SIGNED_DATE).build());
    fields.put(SIGNED_BY_NAME_FIELD_ID, CustomPdfField.builder().value(DOCTOR_NAME).build());
    fields.put(PA_TITLE_FIELD_ID, CustomPdfField.builder().value(PA_TITLE).build());
    fields.put(SPECIALTY_FIELD_ID, CustomPdfField.builder().value(SPECIALTY).build());
    fields.put(HSA_ID_FIELD_ID, CustomPdfField.builder().value(HSA_ID).build());
    fields.put(WORKPLACE_CODE_FIELD_ID, CustomPdfField.builder().value(WORKPLACE_CODE).build());
    fields.put(
        CONTACT_INFORMATION_FIELD_ID, CustomPdfField.builder().value(CONTACT_INFORMATION).build());

    return fields;
  }
}
