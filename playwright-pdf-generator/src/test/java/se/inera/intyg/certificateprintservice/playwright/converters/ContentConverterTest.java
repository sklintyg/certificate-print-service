package se.inera.intyg.certificateprintservice.playwright.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Category;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Certificate;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Metadata;

class ContentConverterTest {

  private final ContentConverter contentConverter = new ContentConverter();

  private static final String CATEGORY_ID = "categoryId";
  private static final String CATEGORY_NAME = "categoryName";
  private static final String ISSUER_NAME = "issuerName";
  private static final String ISSUING_UNIT = "issuingUnit";
  private static final String SENT_DATE = "2025-01-22";
  private static final String SIGN_DATE = "2025-01-18";
  private static final String DESCRIPTION = "description";
  private static final String CERTIFICATE_NAME = "certificateName";
  private static final String CERTIFICATE_TYPE = "certificateType";
  private static final String CERTIFICATE_VERSION = "certificateVersion";
  private static final String RECIPIENT_NAME = "recipientName";
  private static final String PERSON_ID = "personId";
  private static final List<String> ISSUING_UNIT_INFO = List.of("address", "zipCeode", "city");

  private final static List<Category> CATEGORIES = List.of(
      Category.builder()
          .id(CATEGORY_ID)
          .name(CATEGORY_NAME)
          .build());


  private static final Metadata METADATA = Metadata.builder()
      .name(CERTIFICATE_NAME)
      .typeId(CERTIFICATE_TYPE)
      .version(CERTIFICATE_VERSION)
      .recipientName(RECIPIENT_NAME)
      .personId(PERSON_ID)
      .issuerName(ISSUER_NAME)
      .issuingUnit(ISSUING_UNIT)
      .issuingUnitInfo(ISSUING_UNIT_INFO)
      .signingDate(SIGN_DATE)
      .sentDate(SENT_DATE)
      .description(DESCRIPTION)
      .build();

  private final static Certificate CERTIFICATE = Certificate.builder()
      .categories(CATEGORIES)
      .metadata(METADATA)
      .build();


  @Test
  void shouldSetCategories() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertEquals(CATEGORIES, response.getCategories());
  }

  @Test
  void shouldSetCertificateName() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertEquals(CERTIFICATE_NAME, response.getCertificateName());
  }

  @Test
  void shouldSetCertificateType() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertEquals(CERTIFICATE_TYPE, response.getCertificateType());
  }

  @Test
  void shouldSetCertificateVersion() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertEquals(CERTIFICATE_VERSION, response.getCertificateVersion());
  }

  @Test
  void shouldSetRecipientName() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertEquals(RECIPIENT_NAME, response.getRecipientName());
  }

  @Test
  void shouldSetPersonId() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertEquals(PERSON_ID, response.getPersonId());
  }

  @Test
  void shouldSetIssuerName() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertEquals(ISSUER_NAME, response.getIssuerName());
  }

  @Test
  void shouldSetIssuingUnit() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertEquals(ISSUING_UNIT, response.getIssuingUnit());
  }

  @Test
  void shouldSetIssuingUnitInfo() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertEquals(ISSUING_UNIT_INFO, response.getIssuingUnitInfo());
  }

  @Test
  void shouldSetSignDate() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertEquals(SIGN_DATE, response.getSignDate());
  }

  @Test
  void shouldSetDescription() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertEquals(DESCRIPTION, response.getDescription());
  }

  @Test
  void shouldSetIsDraft() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertFalse(response.isDraft());
  }

  @Test
  void shouldSetIsSent() {
    final var response = contentConverter.convert(CERTIFICATE);
    assertTrue(response.isSent());
  }

}
