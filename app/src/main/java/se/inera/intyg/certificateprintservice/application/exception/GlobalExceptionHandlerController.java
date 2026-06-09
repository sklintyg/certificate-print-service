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
package se.inera.intyg.certificateprintservice.application.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandlerController {

  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<String> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    final var message =
        exception.getBindingResult().getFieldErrors().stream()
            .map(error -> "Invalid request - '%s' %s".formatted(error.getField(), error.getDefaultMessage()))
            .findFirst()
            .orElse("Invalid request");
    log.warn("Bad request. Reason: %s.".formatted(message));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(message);
  }

  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<String> handleIllegalArgumentException(
      IllegalArgumentException exception) {
    log.warn("Bad request. Reason: %s.".formatted(exception.getMessage()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(exception.getMessage());
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<String> handleRuntimeExceptions(Exception exception) {
    log.error("Internal server error. Reason: %s.".formatted(exception.getMessage()), exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body("internal server error");
  }
}
