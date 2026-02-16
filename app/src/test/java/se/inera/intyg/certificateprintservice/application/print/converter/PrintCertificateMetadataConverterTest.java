package se.inera.intyg.certificateprintservice.application.print.converter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.application.print.dto.GeneralPrintTextDTO;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateMetadataDTO;

class PrintCertificateMetadataConverterTest {

  private static final String NAME = "name";
  private static final String FILE_NAME = "fileName";
  private static final String VERSION = "version";
  private static final String TYPE_ID = "typeId";
  private static final String CERTIFICATE_ID = "certificateId";
  private static final String SENT_DATE = "sentDate";
  private static final String SIGNING_DATE = "signingDate";
  private static final byte[] RECIPIENT_LOGO = "logo".getBytes(StandardCharsets.UTF_8);
  private static final String RECIPIENT_NAME = "recipientName";
  private static final String RECIPIENT_ID = "recipientId";
  private static final String APPLICATION_ORIGIN = "applicationOrigin";
  private static final String PERSON_ID = "personId";
  private static final String DESCRIPTION = "description";
  private static final PrintCertificateMetadataDTO METADATA_DTO = PrintCertificateMetadataDTO.builder()
      .name(NAME)
      .fileName(FILE_NAME)
      .version(VERSION)
      .typeId(TYPE_ID)
      .certificateId(CERTIFICATE_ID)
      .sentDate(SENT_DATE)
      .signingDate(SIGNING_DATE)
      .recipientLogo(RECIPIENT_LOGO)
      .recipientName(RECIPIENT_NAME)
      .recipientId(RECIPIENT_ID)
      .applicationOrigin(APPLICATION_ORIGIN)
      .personId(PERSON_ID)
      .description(DESCRIPTION)
      .generalPrintText(GeneralPrintTextDTO.builder()
          .leftMarginInfoText("leftMarginInfoText")
          .draftAlertInfoText("draftAlertInfoText")
          .build())
      .build();
  private static final PrintCertificateMetadataDTO METADATA_DTO_GENERAL_TEXT_MISSING = PrintCertificateMetadataDTO.builder()
      .name(NAME)
      .fileName(FILE_NAME)
      .version(VERSION)
      .typeId(TYPE_ID)
      .certificateId(CERTIFICATE_ID)
      .sentDate(SENT_DATE)
      .signingDate(SIGNING_DATE)
      .recipientLogo(RECIPIENT_LOGO)
      .recipientName(RECIPIENT_NAME)
      .recipientId(RECIPIENT_ID)
      .applicationOrigin(APPLICATION_ORIGIN)
      .personId(PERSON_ID)
      .description(DESCRIPTION)
      .generalPrintText(GeneralPrintTextDTO.builder()
          .build())
      .build();

  PrintCertificateMetadataConverter printCertificateMetadataConverter;

  @BeforeEach
  void setUp() {
    printCertificateMetadataConverter = new PrintCertificateMetadataConverter();
  }

  @Test
  void shallConvertName() {
    assertEquals(NAME, printCertificateMetadataConverter.convert(METADATA_DTO).getName());
  }

  @Test
  void shallConvertFileName() {
    assertEquals(FILE_NAME, printCertificateMetadataConverter.convert(METADATA_DTO).getFileName());
  }

  @Test
  void shallConvertVersion() {
    assertEquals(VERSION, printCertificateMetadataConverter.convert(METADATA_DTO).getVersion());
  }

  @Test
  void shallConvertTypeId() {
    assertEquals(TYPE_ID, printCertificateMetadataConverter.convert(METADATA_DTO).getTypeId());
  }

  @Test
  void shallConvertCertificateId() {
    assertEquals(CERTIFICATE_ID,
        printCertificateMetadataConverter.convert(METADATA_DTO).getCertificateId());
  }

  @Test
  void shallConvertSentDate() {
    assertEquals(SENT_DATE,
        printCertificateMetadataConverter.convert(METADATA_DTO).getSentDate());
  }

  @Test
  void shallConvertSigningDate() {
    assertEquals(SIGNING_DATE,
        printCertificateMetadataConverter.convert(METADATA_DTO).getSigningDate());
  }

  @Test
  void shallConvertRecipientLogo() {
    assertEquals(RECIPIENT_LOGO,
        printCertificateMetadataConverter.convert(METADATA_DTO).getRecipientLogo());
  }

  @Test
  void shallConvertRecipientName() {
    assertEquals(RECIPIENT_NAME,
        printCertificateMetadataConverter.convert(METADATA_DTO).getRecipientName());
  }

  @Test
  void shallConvertRecipientId() {
    assertEquals(RECIPIENT_ID,
        printCertificateMetadataConverter.convert(METADATA_DTO).getRecipientId());
  }

  @Test
  void shallConvertApplicationOrigin() {
    assertEquals(APPLICATION_ORIGIN,
        printCertificateMetadataConverter.convert(METADATA_DTO).getApplicationOrigin());
  }

  @Test
  void shallConvertPersonId() {
    assertEquals(PERSON_ID, printCertificateMetadataConverter.convert(METADATA_DTO).getPersonId());
  }

  @Test
  void shallConvertDescription() {
    assertEquals(DESCRIPTION,
        printCertificateMetadataConverter.convert(METADATA_DTO).getDescription());
  }

  @Test
  void shallConvertGeneralPrintText() {
    assertAll(() -> {
      assertEquals("leftMarginInfoText",
          printCertificateMetadataConverter.convert(METADATA_DTO).getGeneralPrintText()
              .getLeftMarginInfoText());
      assertEquals("draftAlertInfoText",
          printCertificateMetadataConverter.convert(METADATA_DTO).getGeneralPrintText()
              .getDraftAlertInfoText());
    });
  }

  @Test
  void shallConvertGeneralPrintTextWhenMissing() {
    assertNull(printCertificateMetadataConverter.convert(METADATA_DTO_GENERAL_TEXT_MISSING)
        .getGeneralPrintText());
  }
}