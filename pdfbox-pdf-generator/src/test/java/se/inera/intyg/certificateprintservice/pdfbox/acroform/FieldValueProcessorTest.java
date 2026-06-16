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
package se.inera.intyg.certificateprintservice.pdfbox.acroform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.OverflowConfig;

class FieldValueProcessorTest {

  private final FieldValueProcessor processor = new FieldValueProcessor();

  @Nested
  class WhenNoMaxLength {

    @Test
    void shallReturnValueAsIs() {
      final var field = CustomPdfField.builder().value("Hello World").build();

      final var result = processor.process(field);

      assertEquals("Hello World", result.primaryValue());
      assertNull(result.overflowRemainder());
    }

    @Test
    void shallRemoveLineBreaksWhenFlagIsSet() {
      final var field =
          CustomPdfField.builder()
              .value("Line one\nLine two\nLine three")
              .shouldRemoveLineBreaks(true)
              .build();

      final var result = processor.process(field);

      assertEquals("Line oneLine twoLine three", result.primaryValue());
      assertNull(result.overflowRemainder());
    }

    @Test
    void shallPreserveLineBreaksWhenFlagIsFalse() {
      final var field =
          CustomPdfField.builder()
              .value("Line one\nLine two")
              .shouldRemoveLineBreaks(false)
              .build();

      final var result = processor.process(field);

      assertEquals("Line one\nLine two", result.primaryValue());
    }
  }

  @Nested
  class WhenValueFitsWithinMaxLength {

    @Test
    void shallReturnValueWithoutModification() {
      final var field = CustomPdfField.builder().value("Short text").maxLength(100).build();

      final var result = processor.process(field);

      assertEquals("Short text", result.primaryValue());
      assertNull(result.overflowRemainder());
    }
  }

  @Nested
  class WhenTruncationRequired {

    @Test
    void shallTruncateWithEllipsisWhenNoOverflowConfig() {
      final var field =
          CustomPdfField.builder()
              .value("This is a long text that exceeds the max length limit set for field")
              .maxLength(30)
              .build();

      final var result = processor.process(field);

      assertEquals("This is a long text that...", result.primaryValue());
      assertNull(result.overflowRemainder());
    }

    @Test
    void shallTruncateWhenOverflowFieldIdIsNull() {
      final var field =
          CustomPdfField.builder()
              .value("This is a long text that exceeds the max length limit set for field")
              .maxLength(30)
              .overflow(OverflowConfig.builder().overflowFieldId(null).build())
              .build();

      final var result = processor.process(field);

      assertEquals("This is a long text that...", result.primaryValue());
      assertNull(result.overflowRemainder());
    }

    @Test
    void shallRemoveLineBreaksBeforeTruncation() {
      final var field =
          CustomPdfField.builder()
              .value("This is\na long text\nthat exceeds the max length limit")
              .maxLength(30)
              .shouldRemoveLineBreaks(true)
              .build();

      final var result = processor.process(field);

      assertEquals("This isa long textthat...", result.primaryValue());
    }
  }

  @Nested
  class WhenOverflowSplitRequired {

    @Test
    void shallSplitWithLongSuffixWhenMaxLengthAbove22() {
      final var longValue = "A".repeat(50) + " " + "B".repeat(50) + " " + "C".repeat(50);
      final var field =
          CustomPdfField.builder()
              .value(longValue)
              .maxLength(60)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId("overflow")
                      .overflowLabel("Label")
                      .build())
              .build();

      final var result = processor.process(field);

      assertTrue(result.primaryValue().endsWith("... Se fortsättningsblad!"));
      assertTrue(result.primaryValue().length() <= 60);
      assertTrue(result.overflowRemainder().startsWith("... "));
    }

    @Test
    void shallSplitWithShortSuffixWhenMaxLengthIs22OrLess() {
      final var field =
          CustomPdfField.builder()
              .value("This text is too long for the tiny field")
              .maxLength(20)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId("overflow")
                      .overflowLabel("Test")
                      .build())
              .build();

      final var result = processor.process(field);

      assertTrue(result.primaryValue().endsWith("..."));
      assertTrue(!result.primaryValue().contains("Se fortsättningsblad"));
    }

    @Test
    void shallPreserveLineBreaksInOverflowRemainder() {
      final var value = "First part of the text\nSecond line\nThird line with more content here";
      final var field =
          CustomPdfField.builder()
              .value(value)
              .maxLength(30)
              .shouldRemoveLineBreaks(true)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId("overflow")
                      .overflowLabel("Label")
                      .build())
              .build();

      final var result = processor.process(field);

      assertTrue(
          !result.primaryValue().contains("\n"), "Primary value should have line breaks removed");
      assertTrue(
          result.overflowRemainder().contains("\n"),
          "Overflow remainder should preserve line breaks from original value");
    }

    @Test
    void shallRemoveLineBreaksOnlyFromPrimaryValue() {
      final var value =
          "Short\ntext that will overflow when line breaks are removed and this is long";
      final var field =
          CustomPdfField.builder()
              .value(value)
              .maxLength(40)
              .shouldRemoveLineBreaks(true)
              .overflow(
                  OverflowConfig.builder()
                      .overflowFieldId("overflow")
                      .overflowLabel("Label")
                      .build())
              .build();

      final var result = processor.process(field);

      assertTrue(!result.primaryValue().contains("\n"), "Primary should not contain line breaks");
    }
  }
}
