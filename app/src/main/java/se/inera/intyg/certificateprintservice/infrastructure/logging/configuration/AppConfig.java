package se.inera.intyg.certificateprintservice.infrastructure.logging.configuration;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.inera.intyg.certificateprintservice.pdfgenerator.event.CertificatePrintEventService;
import se.inera.intyg.certificateprintservice.pdfgenerator.event.CertificatePrintEventSubscriber;

@Configuration
public class AppConfig {

  @Bean
  public CertificatePrintEventService certificatePrintEventService(
      List<CertificatePrintEventSubscriber> subscribers) {
    return new CertificatePrintEventService(subscribers);
  }
}
