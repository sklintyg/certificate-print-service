package se.inera.intyg.certificateprintservice.playwright.element;

import static se.inera.intyg.certificateprintservice.playwright.element.ElementProvider.element;

import java.util.List;
import javax.swing.text.html.HTML.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Element;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueLabeledList;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.value.ElementValueTable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BasicElementFactory {

  public static Element table(ElementValueTable tableValue) {
    final var headerColumns = tableValue.getHeadings().size();
    final var valueColumns = tableValue.getValues().getFirst().size();
    final var tableElement = element(Tag.TABLE)
        .addClass("text-sm mx-[5mm]");

    tableElement.appendChild(th(tableValue, headerColumns, valueColumns));

    tableValue.getValues().stream()
        .map(values -> tr(values, headerColumns, valueColumns))
        .forEach(tableElement::appendChild);

    return tableElement;
  }

  private static Element th(ElementValueTable tableValue, int headerColumns,
      int valueColumns) {
    final var trHeader = element(Tag.TR)
        .addClass("border-b border-black border-solid");
    for (int i = 0; i < valueColumns; i++) {
      final var th = element(Tag.TH)
          .addClass("font-bold pr-[10mm]");
      if (valueColumns - i > headerColumns) {
        th.appendText("");
      } else {
        th.appendText(tableValue.getHeadings().get(i - (valueColumns - headerColumns)));
      }
      trHeader.appendChild(th);
    }
    return trHeader;
  }

  public static Element labeledList(ElementValueLabeledList list) {
    final var container = element(Tag.DIV)
        .addClass("mb-2");
    list.getList().stream()
        .map(entry -> {
          final var div = element(Tag.DIV).addClass("mb-2");
          final var label = element(Tag.P)
              .addClass("text-sm font-bold pt-[1mm] px-[5mm] text-neutral-600");
          label.appendText(entry.getLabel());
          div.appendChild(label);
          final var value = element(Tag.P)
              .addClass("text-sm italic px-[5mm]")
              .appendText(entry.getText());
          div.appendChild(value);
          return div;
        })
        .forEach(container::appendChild);
    return container;
  }

  public static Element labeledText(String label, String text) {
    final var container = element(Tag.DIV);
    final var labelElement = element(Tag.P)
        .addClass("text-sm font-bold pt-[1mm] px-[5mm] text-neutral-600");
    labelElement.appendText(label);
    container.appendChild(labelElement);
    final var valueElement = element(Tag.P)
        .addClass("text-sm italic px-[5mm]")
        .appendText(text);
    container.appendChild(valueElement);
    return container;
  }

  public static Element p(String text) {
    return new Element(Tag.P.toString())
        .addClass("text-sm italic px-[5mm]")
        .appendText(text);
  }

  private static Element tr(List<String> rowValues, int headerColumns,
      int valueColumns) {
    final var tableRow = element(Tag.TR);
    for (int i = 0; i < valueColumns; i++) {
      final var td = element(Tag.TD);
      if (valueColumns - i > headerColumns) {
        td.addClass("font-bold pr-[10mm]");
      }
      td.appendText(rowValues.get(i));
      tableRow.appendChild(td);
    }
    return tableRow;
  }

}