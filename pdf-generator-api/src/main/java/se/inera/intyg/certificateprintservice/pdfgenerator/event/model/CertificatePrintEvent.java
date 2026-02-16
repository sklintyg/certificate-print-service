package se.inera.intyg.certificateprintservice.pdfgenerator.event.model;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CertificatePrintEvent {

  CertificatePrintEventType type;
  String certificateId;
  LocalDateTime start;
  LocalDateTime end;

  public long duration() {
    if (start == null || end == null) {
      throw new IllegalArgumentException("Start and end time cannot be null");
    }
    return Duration.between(start, end).toMillis();
  }
}