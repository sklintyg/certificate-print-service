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

import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CertificateStatus;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfMetadata;

public class TestDataFK7210CustomPdfMetadata {

  public static final String CERTIFICATE_ID = "certificateId-fk7210-001";
  public static final String RECIPIENT_NAME = "Försäkringskassan";
  public static final String ADDITIONAL_INFO_TEXT = "Webcert";
  public static final String SIGNED_DATE_FIELD_ID = "form1[0].#subform[0].flt_datUnderskrift[0]";
  public static final int START_MCID = 100;
  public static final int SIGNATURE_PAGE_INDEX = 0;
  public static final int SIGNATURE_TAG_INDEX_WITH_ADDRESS = 15;
  private static final String TITLE = "fk7210";

  private TestDataFK7210CustomPdfMetadata() {
    throw new IllegalStateException("Utility class");
  }

  public static CustomPdfMetadata metadataWithStatus(CertificateStatus status) {
    return CustomPdfMetadata.builder()
        .status(status)
        .isSent(false)
        .certificateId(CERTIFICATE_ID)
        .signedDateFieldId(SIGNED_DATE_FIELD_ID)
        .startMcid(START_MCID)
        .build();
  }

  public static CustomPdfMetadata draftMetadata() {
    return metadataWithStatus(CertificateStatus.DRAFT);
  }

  public static CustomPdfMetadata signedMetadata() {
    return CustomPdfMetadata.builder()
        .status(CertificateStatus.SIGNED)
        .isSent(false)
        .certificateId(CERTIFICATE_ID)
        .additionalInfoText(ADDITIONAL_INFO_TEXT)
        .signaturePageIndex(SIGNATURE_PAGE_INDEX)
        .signatureTagIndex(SIGNATURE_TAG_INDEX_WITH_ADDRESS)
        .signedDateFieldId(SIGNED_DATE_FIELD_ID)
        .startMcid(START_MCID)
        .build();
  }

  public static CustomPdfMetadata signedAndSentMetadata() {
    return CustomPdfMetadata.builder()
        .status(CertificateStatus.SIGNED)
        .isSent(true)
        .sentRecipientName(RECIPIENT_NAME)
        .availableForCitizen(true)
        .certificateId(CERTIFICATE_ID)
        .additionalInfoText(ADDITIONAL_INFO_TEXT)
        .signaturePageIndex(SIGNATURE_PAGE_INDEX)
        .signatureTagIndex(SIGNATURE_TAG_INDEX_WITH_ADDRESS)
        .signedDateFieldId(SIGNED_DATE_FIELD_ID)
        .startMcid(START_MCID)
        .build();
  }

  public static CustomPdfMetadata fullMetadata() {
    return CustomPdfMetadata.builder()
        .status(CertificateStatus.SIGNED)
        .isSent(true)
        .sentRecipientName(RECIPIENT_NAME)
        .availableForCitizen(true)
        .certificateId(CERTIFICATE_ID)
        .additionalInfoText(ADDITIONAL_INFO_TEXT)
        .signaturePageIndex(SIGNATURE_PAGE_INDEX)
        .signatureTagIndex(SIGNATURE_TAG_INDEX_WITH_ADDRESS)
        .signedDateFieldId(SIGNED_DATE_FIELD_ID)
        .startMcid(START_MCID)
        .title(TITLE)
        .build();
  }
}
