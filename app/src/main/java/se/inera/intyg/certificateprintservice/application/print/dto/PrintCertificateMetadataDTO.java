package se.inera.intyg.certificateprintservice.application.print.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateMetadataDTO.PrintCertificateMetadataDTOBuilder;

@Value
@Builder
@JsonDeserialize(builder = PrintCertificateMetadataDTOBuilder.class)
public class PrintCertificateMetadataDTO {

  String name;
  String fileName;
  String version;
  String typeId;
  String certificateId;
  String signingDate;
  String sentDate;
  byte[] recipientLogo;
  String recipientName;
  String recipientId;
  boolean canSendElectronically;
  String applicationOrigin;
  String personId;
  String description;
  String issuerName;
  String issuingUnit;
  List<String> unitInformation;
  GeneralPrintTextDTO generalPrintText;

  @JsonPOJOBuilder(withPrefix = "")
  public static class PrintCertificateMetadataDTOBuilder {

  }
}