package se.inera.intyg.certificateprintservice.playwright.certificate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Element;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValue;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueLabeledList;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueLabeledText;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueList;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueTable;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueText;
import se.inera.intyg.certificateprintservice.playwright.element.BasicElementFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElementValueConverter {

  public static Element html(ElementValue elementValue) {
    if (elementValue instanceof ElementValueText textValue) {
      return BasicElementFactory.p(textValue.getText());
    }

    if (elementValue instanceof ElementValueList listValue) {
      return BasicElementFactory.p(String.join(", ", listValue.getList()));
    }

    if (elementValue instanceof ElementValueTable tableValue) {
      return BasicElementFactory.table(tableValue);
    }

    if (elementValue instanceof ElementValueLabeledList labeledList) {
      return BasicElementFactory.labeledList(labeledList);
    }

    if (elementValue instanceof ElementValueLabeledText labeledText) {
      return BasicElementFactory.labeledText(labeledText.getLabel(), labeledText.getText());
    }

    throw new IllegalStateException("No value converter for value type");
  }
}
