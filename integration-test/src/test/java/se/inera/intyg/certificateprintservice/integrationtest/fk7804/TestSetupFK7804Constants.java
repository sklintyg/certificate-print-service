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

  public static final String TAGGED_PDF_OVERFLOW_RESOURCE = "/templates/fk7804-overflow-signed.pdf";

  // --- Patient ID fields (5 pages) ---

  public static final String PATIENT_ID_FIELD_ID = "form1[0].#subform[0].flt_txtPersonNr[0]";
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

  // --- Grund för medicinskt underlag ---

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

  // --- Sysselsättning ---

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

  // --- Prognos ---

  public static final String PROGNOS_RADIO_GROUP_FIELD_ID = "form1[0].Sida3[0].RadioButtonList4[0]";

  // --- Kontakt ---

  public static final String CHECKBOX_KONTAKT_FIELD_ID =
      "form1[0].Sida4[0].ksr_ForsakringskassanKontakar[0]";

  // --- MaxLength constants ---

  public static final int PDF_TEXT_FIELD_ROW_LENGTH = 53;
  public static final int MAX_LENGTH_FUNKTIONSNEDSATTNING = 11 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_AKTIVITETSBEGRANSNING = 12 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_MEDICINSK_BEHANDLING = 8 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_OVRIGT = 8 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_ATGARDER = 7 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_YRKE = 3 * PDF_TEXT_FIELD_ROW_LENGTH;
  public static final int MAX_LENGTH_GRUND_FOR_BEDOMNING = 2 * PDF_TEXT_FIELD_ROW_LENGTH;

  // --- Realistic value constants ---

  public static final String PATIENT_ID = "191212121212";
  public static final String SIGNED_DATE = "2025-06-15";
  public static final String DOCTOR_NAME = "Ajla Doktor";
  public static final String PA_TITLE = "202010, 208014, 208015";
  public static final String SPECIALTY = "";
  public static final String HSA_ID = "TSTNMT2321000156-DRAA";
  public static final String WORKPLACE_CODE = "7654321";
  public static final String CONTACT_INFORMATION =
      "Alfa Medicincentrum\n" + "Alfagatan 3 a, 65591 Karlstad\n" + "Telefon: 054121314";

  public static final String DATE_FYSISK_UNDERSOKNING = "2025-06-10";
  public static final String DATE_DIGITAL_UNDERSOKNING = "2025-06-09";
  public static final String DATE_TELEFONKONTAKT = "2025-06-08";
  public static final String DATE_JOURNALUPPGIFTER = "2025-06-05";
  public static final String DATE_ANNAT = "2025-06-03";

  public static final String ANNAN_GRUND = "Rehabiliteringsplan från fysioterapeut";

  public static final String YRKE_ARBETSUPPGIFTER =
      """
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
    Lagerarbetare med tunga lyft och repetitiva arbetsmoment\n
  """;

  public static final String DIAGNOS_1_NAME = "Lumbal diskbråck";
  public static final String DIAGNOS_1_CODE = "M5116";
  public static final String DIAGNOS_2_NAME = "Ischias";
  public static final String DIAGNOS_2_CODE = "M541";
  public static final String DIAGNOS_3_NAME = "Lumbago";
  public static final String DIAGNOS_3_CODE = "M545";

  public static final String FUNKTIONSNEDSATTNING =
      "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
          + "Nedsatt rörlighet i lumbalryggen, kan inte böja sig framåt mer än 30 grader. "
          + "Positiv Lasègues test vänster sida vid 40 grader. Nedsatt känsel i L5-dermatomet. "
          + "Svårigheter att sitta mer än 20 minuter utan smärta."
          + "Patienten har kraftig smärta i ländryggen med utstrålning till vänster ben. "
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

  // --- Overflow labels ---

  public static final String OVERFLOW_LABEL_FUNKTIONSNEDSATTNING = "Funktionsnedsättning";
  public static final String OVERFLOW_LABEL_AKTIVITETSBEGRANSNING = "Aktivitetsbegränsning";
  public static final String OVERFLOW_LABEL_MEDICINSK_BEHANDLING = "Medicinsk behandling";
  public static final String OVERFLOW_LABEL_OVRIGT = "Övriga upplysningar";
  public static final String OVERFLOW_LABEL_ATGARDER = "Åtgärder för att främja återgång";
  public static final String OVERFLOW_LABEL_YRKE = "Yrke och arbetsuppgifter";
  public static final String OVERFLOW_LABEL_GRUND_FOR_BEDOMNING = "Grund för bedömning";

  // --- Date range values ---

  public static final String DATE_100_FROM = "2025-06-15";
  public static final String DATE_100_TO = "2025-07-15";
  public static final String DATE_75_FROM = "2025-07-16";
  public static final String DATE_75_TO = "2025-08-15";
  public static final String DATE_50_FROM = "2025-08-16";
  public static final String DATE_50_TO = "2025-09-15";
  public static final String DATE_25_FROM = "2025-09-16";
  public static final String DATE_25_TO = "2025-10-15";
}
