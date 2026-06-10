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

import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.ADDITIONAL_INFO_TEXT;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.CERTIFICATE_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.RECIPIENT_NAME;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.SIGNATURE_PAGE_INDEX;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.SIGNATURE_TAG_INDEX_WITH_ADDRESS;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.SIGNED_DATE_FIELD_ID;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.START_MCID;

import se.inera.intyg.certificateprintservice.application.print.custom.dto.CertificateStatusDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPdfMetadataDTO;

public class TestSetupFK7210Metadata {

  public static CustomPdfMetadataDTO fullMetadata() {
    return CustomPdfMetadataDTO.builder()
        .status(CertificateStatusDTO.SIGNED)
        .title("Title")
        .sent(true)
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
}
