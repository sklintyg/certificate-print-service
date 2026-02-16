package se.inera.intyg.certificateprintservice.pdfgenerator.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class MetadataTest {

  public static final String LEFT_MARGIN_INFO_TEXT = "leftMarginInfoText";
  public static final String DRAFT_ALERT_INFO_TEXT = "draftAlertInfoText";

  @Test
  void shouldReturnIsNotDraftIfSigned() {
    assertFalse(
        Metadata.builder()
            .signingDate("2025-01-01")
            .build().isDraft()
    );
  }

  @Test
  void shouldReturnGeneralPrintText() {
    var generalPrintText = Metadata.builder()
        .generalPrintText(
            GeneralPrintText.builder()
                .leftMarginInfoText(LEFT_MARGIN_INFO_TEXT)
                .draftAlertInfoText(DRAFT_ALERT_INFO_TEXT)
                .build()
        )
        .build().getGeneralPrintText();

    assertEquals(LEFT_MARGIN_INFO_TEXT, generalPrintText.getLeftMarginInfoText());
    assertEquals(DRAFT_ALERT_INFO_TEXT, generalPrintText.getDraftAlertInfoText());
  }
}