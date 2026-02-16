package se.inera.intyg.certificateprintservice.application.print.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.application.print.dto.PrintCertificateCategoryDTO;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.Category;

@Component
@RequiredArgsConstructor
public class PrintCertificateCategoryConverter {

  private final PrintCertificateQuestionConverter printCertificateQuestionConverter;

  public Category convert(PrintCertificateCategoryDTO category) {
    return Category.builder()
        .id(category.getId())
        .name(category.getName())
        .questions(category.getQuestions().stream().map(printCertificateQuestionConverter::convert)
            .toList())
        .build();
  }
}