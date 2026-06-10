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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Constants.TAGGED_PDF_RESOURCE;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Fields.allFields;
import static se.inera.intyg.certificateprintservice.integrationtest.fk7210.TestSetupFK7210Metadata.fullMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintRequestDTO;

public class TestSetupFK7210 {

  public CustomPrintRequestDTO.CustomPrintRequestDTOBuilder fk7210SignedAndSent() {
    return CustomPrintRequestDTO.builder()
        .fields(allFields())
        .metadata(fullMetadata())
        .template(TAGGED_PDF_RESOURCE);
  }

  public CustomPrintRequestDTO loadTemplateAsBase64(CustomPrintRequestDTO requestDTO)
      throws IOException {
    try (InputStream stream = getClass().getResourceAsStream(requestDTO.getTemplate())) {
      assertNotNull(stream, "Test PDF template not found at: " + requestDTO.getTemplate());
      return requestDTO.withTemplate(Base64.getEncoder().encodeToString(stream.readAllBytes()));
    }
  }
}
