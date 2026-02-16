package se.inera.intyg.certificateprintservice.pdfgenerator.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GeneralPrintText {

  String leftMarginInfoText;
  String draftAlertInfoText;

}
