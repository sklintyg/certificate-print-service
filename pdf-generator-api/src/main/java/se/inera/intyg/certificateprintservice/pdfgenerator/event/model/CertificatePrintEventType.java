package se.inera.intyg.certificateprintservice.pdfgenerator.event.model;

public enum CertificatePrintEventType {
  CREATED("created-certificate-print", Constants.ACTION_TYPE_CREATION);

  private final String action;
  private final String actionType;

  CertificatePrintEventType(String action, String actionType) {
    this.action = action;
    this.actionType = actionType;
  }

  public String action() {
    return action;
  }

  public String actionType() {
    return actionType;
  }

  private static class Constants {

    private static final String ACTION_TYPE_CREATION = "creation";
  }
}