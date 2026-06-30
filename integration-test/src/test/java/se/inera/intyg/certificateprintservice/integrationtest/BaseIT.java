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
package se.inera.intyg.certificateprintservice.integrationtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintRequestDTO;
import se.inera.intyg.certificateprintservice.application.print.custom.dto.CustomPrintResponseDTO;

@SpringBootTest(
    classes = se.inera.intyg.certificateprintservice.CertificatePrintServiceApplication.class,
    webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("it")
public abstract class BaseIT {

  private static final String API_PATH = "/api/print/custom";

  @Autowired protected RestTestClient restTestClient;

  protected ResponseEntity<CustomPrintResponseDTO> postCustom(CustomPrintRequestDTO request) {
    final var result =
        restTestClient
            .post()
            .uri(API_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .returnResult(CustomPrintResponseDTO.class);

    return ResponseEntity.status(HttpStatus.valueOf(result.getStatus().value()))
        .body(result.getResponseBody());
  }

  protected ResponseEntity<String> postCustomError(CustomPrintRequestDTO request) {
    final var result =
        restTestClient
            .post()
            .uri(API_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .returnResult(String.class);

    return ResponseEntity.status(HttpStatus.valueOf(result.getStatus().value()))
        .body(result.getResponseBody());
  }
}
