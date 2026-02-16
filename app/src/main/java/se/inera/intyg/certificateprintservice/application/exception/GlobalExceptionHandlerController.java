package se.inera.intyg.certificateprintservice.application.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandlerController {
  
  @ExceptionHandler({Exception.class})
  public ResponseEntity<String> handleRuntimeExceptions(Exception exception) {
    log.error("Internal server error. Reason: %s.".formatted(
        exception.getMessage()), exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON).body("internal server error");
  }

}