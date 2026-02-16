package se.inera.intyg.certificateprintservice.pdfgenerator.api;


import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Metadata {

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
  List<String> issuingUnitInfo;
  GeneralPrintText generalPrintText;

  public boolean isDraft() {
    return signingDate == null;
  }

  public boolean isSent() {
    return sentDate != null;
  }

  public boolean isSigned() {
    return signingDate != null;
  }

  public String getSigningDateAsString() {
    return LocalDate.parse(this.signingDate.split("T")[0]).toString();
  }
}