/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.certificateprintservice.application.print.general.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.certificateprintservice.application.print.general.dto.PrintCertificateQuestionDTO;
import se.inera.intyg.certificateprintservice.application.print.general.dto.value.ElementSimplifiedValueLabeledList;
import se.inera.intyg.certificateprintservice.application.print.general.dto.value.ElementSimplifiedValueLabeledText;
import se.inera.intyg.certificateprintservice.application.print.general.dto.value.ElementSimplifiedValueList;
import se.inera.intyg.certificateprintservice.application.print.general.dto.value.ElementSimplifiedValueTable;
import se.inera.intyg.certificateprintservice.application.print.general.dto.value.ElementSimplifiedValueText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.Question;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.value.ElementValue;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.value.ElementValueLabeledList;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.value.ElementValueLabeledText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.value.ElementValueList;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.value.ElementValueTable;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.general.model.value.ElementValueText;

@Component
public class PrintCertificateQuestionConverter {

  public Question convert(PrintCertificateQuestionDTO question) {
    return Question.builder()
        .id(question.getId())
        .name(question.getName())
        .value(getElementValue(question))
        .subQuestions(question.getSubquestions().stream().map(this::convert).toList())
        .build();
  }

  private static ElementValue getElementValue(PrintCertificateQuestionDTO question) {
    if (question.getValue() instanceof ElementSimplifiedValueText text) {
      return ElementValueText.builder().text(text.getText()).build();
    } else if (question.getValue() instanceof ElementSimplifiedValueList list) {
      return ElementValueList.builder().list(list.getList()).build();
    } else if (question.getValue() instanceof ElementSimplifiedValueTable table) {
      return ElementValueTable.builder()
          .headings(table.getHeadings())
          .values(table.getValues())
          .build();
    } else if (question.getValue() instanceof ElementSimplifiedValueLabeledList labeledList) {
      return ElementValueLabeledList.builder()
          .list(
              labeledList.getList().stream()
                  .map(
                      labeledText ->
                          ElementValueLabeledText.builder()
                              .label(labeledText.getLabel())
                              .text(labeledText.getText())
                              .build())
                  .toList())
          .build();
    } else if (question.getValue() instanceof ElementSimplifiedValueLabeledText text) {
      return ElementValueLabeledText.builder().label(text.getLabel()).text(text.getText()).build();
    }

    throw new IllegalArgumentException("Illegal value type");
  }
}
