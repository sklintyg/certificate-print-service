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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Optional;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.FieldGeometry;
import se.inera.intyg.certificateprintservice.pdfbox.acroform.text.FieldTextLayout;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomPdfField;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.OverflowConfig;

class FieldValueProcessorTest {

  private static final PDType1Font HELVETICA = new PDType1Font(FontName.HELVETICA);
  private static final float FONT_SIZE = 10f;
  private static final String LONG_SUFFIX = "... Se fortsättningsblad!";

  private final FieldTextLayout textLayout = new FieldTextLayout();
  private final FieldValueProcessor processor = new FieldValueProcessor(textLayout);

  private static FieldGeometry multiLine(float width, int maxLines) {
    return new FieldGeometry(HELVETICA, FONT_SIZE, width, maxLines, true);
  }

  private static FieldGeometry singleLine(float width) {
    return new FieldGeometry(HELVETICA, FONT_SIZE, width, 1, false);
  }

  private static OverflowConfig overflow() {
    return OverflowConfig.builder().overflowFieldId("overflow").overflowLabel("Label").build();
  }

  @Nested
  class WhenGeometryIsUnavailable {

    @Test
    void shallReturnValueAsIs() throws IOException {
      final var field = CustomPdfField.builder().value("Hello World").build();

      final var result = processor.process(field, HELVETICA, Optional.empty());

      assertEquals("Hello World", result.primaryValue());
    }

    @Test
    void shallNotProduceOverflow() throws IOException {
      final var field =
          CustomPdfField.builder().value("A".repeat(500)).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.empty());

      assertNull(result.overflowRemainder());
    }

    @Test
    void shallPreserveLineBreaks() throws IOException {
      final var field = CustomPdfField.builder().value("Line one\nLine two\nLine three").build();

      final var result = processor.process(field, HELVETICA, Optional.empty());

      assertEquals("Line one\nLine two\nLine three", result.primaryValue());
    }
  }

  @Nested
  class WhenValueFits {

    @Test
    void shallReturnValueWithoutModification() throws IOException {
      final var field = CustomPdfField.builder().value("Short text").build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(400f, 5)));

      assertEquals("Short text", result.primaryValue());
    }

    @Test
    void shallNotProduceOverflowRemainder() throws IOException {
      final var field = CustomPdfField.builder().value("Short text").overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(400f, 5)));

      assertNull(result.overflowRemainder());
    }

    @Test
    void shallKeepValueFillingExactlyTheAvailableLines() throws IOException {
      final var value = "word ".repeat(12).trim();
      final var requiredLines = textLayout.countLines(value, multiLine(120f, 99));
      final var field = CustomPdfField.builder().value(value).build();

      final var result =
          processor.process(field, HELVETICA, Optional.of(multiLine(120f, requiredLines)));

      assertEquals(value, result.primaryValue());
    }
  }

  @Nested
  class WhenTruncationRequired {

    private static final String TOO_LONG =
        "This is a long text that will not fit in the field at all, by some margin";

    @Test
    void shallEndWithEllipsisWhenNoOverflowConfig() throws IOException {
      final var field = CustomPdfField.builder().value(TOO_LONG).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(120f, 2)));

      assertTrue(result.primaryValue().endsWith("..."));
    }

    @Test
    void shallProduceValueThatFitsTheField() throws IOException {
      final var geometry = multiLine(120f, 2);
      final var field = CustomPdfField.builder().value(TOO_LONG).build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      assertTrue(textLayout.fits(result.primaryValue(), geometry));
    }

    @Test
    void shallNotProduceOverflowRemainder() throws IOException {
      final var field = CustomPdfField.builder().value(TOO_LONG).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(120f, 1)));

      assertNull(result.overflowRemainder());
    }

    @Test
    void shallTruncateWhenOverflowFieldIdIsNull() throws IOException {
      final var field =
          CustomPdfField.builder()
              .value(TOO_LONG)
              .overflow(OverflowConfig.builder().overflowFieldId(null).build())
              .build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(120f, 1)));

      assertNull(result.overflowRemainder());
    }

    @Test
    void shallTruncateSingleLineFieldOnWidth() throws IOException {
      final var geometry = singleLine(100f);
      final var field =
          CustomPdfField.builder().value("This value is far too wide for the field").build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      assertTrue(textLayout.fits(result.primaryValue(), geometry));
    }
  }

  /**
   * Without an extension page, whatever is cut is lost. Line breaks cost whole lines, so they are
   * given up before any text is.
   */
  @Nested
  class WhenTruncatingTextWithLineBreaks {

    private static final String SHORT_LINES =
        "Hosta\nFeber\nHuvudvärk\nYrsel\nTrötthet\nIllamående";

    @Test
    void shallKeepEveryWordWhenTheValueFitsWithoutLineBreaks() throws IOException {
      final var field = CustomPdfField.builder().value(SHORT_LINES).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(300f, 2)));

      assertEquals("Hosta Feber Huvudvärk Yrsel Trötthet Illamående", result.primaryValue());
    }

    @Test
    void shallJoinLinesInASingleLineField() throws IOException {
      final var field = CustomPdfField.builder().value("Hosta\nFeber").build();

      final var result = processor.process(field, HELVETICA, Optional.of(singleLine(200f)));

      assertEquals("Hosta Feber", result.primaryValue());
    }

    @Test
    void shallCollapseBlankLinesAndIndentationToASingleSpace() throws IOException {
      final var field = CustomPdfField.builder().value("Hosta\n\n  Feber").build();

      final var result = processor.process(field, HELVETICA, Optional.of(singleLine(200f)));

      assertEquals("Hosta Feber", result.primaryValue());
    }

    @Test
    void shallTruncateWithoutLineBreaksWhenTheValueStillDoesNotFit() throws IOException {
      final var geometry = multiLine(120f, 2);
      final var field = CustomPdfField.builder().value("word\n".repeat(40).trim()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      assertTrue(result.primaryValue().endsWith("..."));
      assertFalse(result.primaryValue().contains("\n"));
      assertTrue(textLayout.fits(result.primaryValue(), geometry));
    }

    @Test
    void shallKeepLineBreaksWhenTheValueFitsWithThem() throws IOException {
      final var field = CustomPdfField.builder().value("Hosta\nFeber").build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(300f, 3)));

      assertEquals("Hosta\nFeber", result.primaryValue());
    }

    @Test
    void shallKeepLineBreaksInTheRemainderWhenAnExtensionPageIsConfigured() throws IOException {
      final var value = "word ".repeat(30).trim() + "\n\nHosta\nFeber";
      final var field = CustomPdfField.builder().value(value).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(300f, 2)));

      assertTrue(result.overflowRemainder().endsWith("\n\nHosta\nFeber"));
    }
  }

  @Nested
  class WhenOverflowSplitRequired {

    private static final String MANY_WORDS = "word ".repeat(200).trim();
    private static final String TINY_FIELD_VALUE = "This text is too long for the tiny field";

    @Test
    void shallEndPrimaryValueWithLongSuffixWhenItFits() throws IOException {
      final var field = CustomPdfField.builder().value(MANY_WORDS).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(300f, 4)));

      assertTrue(result.primaryValue().endsWith(LONG_SUFFIX));
    }

    @Test
    void shallProducePrimaryValueThatFitsTheField() throws IOException {
      final var geometry = multiLine(300f, 4);
      final var field = CustomPdfField.builder().value(MANY_WORDS).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      assertTrue(textLayout.fits(result.primaryValue(), geometry));
    }

    @Test
    void shallPrefixOverflowRemainder() throws IOException {
      final var field = CustomPdfField.builder().value(MANY_WORDS).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(300f, 4)));

      assertTrue(result.overflowRemainder().startsWith("... "));
    }

    @Test
    void shallFallBackToShortSuffixWhenLongSuffixDoesNotFit() throws IOException {
      final var field =
          CustomPdfField.builder().value(TINY_FIELD_VALUE).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(singleLine(60f)));

      assertFalse(result.primaryValue().contains("Se fortsättningsblad"));
    }

    @Test
    void shallStillEndWithEllipsisWhenFallingBackToShortSuffix() throws IOException {
      final var field =
          CustomPdfField.builder().value(TINY_FIELD_VALUE).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(singleLine(60f)));

      assertTrue(result.primaryValue().endsWith("..."));
    }

    @Test
    void shallCarryTheUnsplitRemainderToOverflow() throws IOException {
      final var field =
          CustomPdfField.builder()
              .value("First second third fourth fifth sixth seventh eighth ninth tenth")
              .overflow(overflow())
              .build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(120f, 1)));

      assertTrue(result.overflowRemainder().endsWith("tenth"));
    }
  }

  /**
   * A split must lose nothing and duplicate nothing. Every other assertion in this class checks the
   * shape of the two halves; these check that the halves still add up to what the clinician wrote.
   * An off-by-one in the index mapping or the word-boundary snap would drop or repeat a sentence
   * while leaving every shape assertion green.
   */
  @Nested
  class WhenSplitTheTwoHalvesReconstructTheOriginal {

    private static final String PROSE =
        "Patienten har nedsatt funktion i höger knä vilket påverkar rörelseförmågan "
            + "och ger smärta vid belastning under längre perioder";

    /**
     * Compares ignoring whitespace: the seam between the halves is allowed to gain or lose a space.
     */
    private static void assertReconstructs(FieldValueResult result) {
      final var prefix =
          result
              .primaryValue()
              .substring(
                  0, result.primaryValue().length() - FieldValueProcessorTest.LONG_SUFFIX.length());
      final var rest = result.overflowRemainder().substring("... ".length());

      assertEquals(
          withoutWhitespace(WhenSplitTheTwoHalvesReconstructTheOriginal.PROSE),
          withoutWhitespace(prefix + rest));
    }

    private static String withoutWhitespace(String text) {
      return text.replaceAll("\\s", "");
    }

    @Test
    void shallReconstructTheOriginalValue() throws IOException {
      final var field = CustomPdfField.builder().value(PROSE).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(200f, 2)));

      assertReconstructs(result);
    }

    @Test
    void shallReconstructTheOriginalValueAcrossSeveralFieldWidths() throws IOException {
      final var field = CustomPdfField.builder().value(PROSE).overflow(overflow()).build();
      var widthsThatSplit = 0;

      for (var width = 150f; width <= 400f; width += 10f) {
        final var result = processor.process(field, HELVETICA, Optional.of(multiLine(width, 2)));

        if (result.overflowRemainder() == null) {
          assertEquals(PROSE, result.primaryValue());
          continue;
        }
        widthsThatSplit++;
        assertReconstructs(result);
      }

      assertTrue(widthsThatSplit > 0, "no width actually produced a split");
    }

    @Test
    void shallResumeTheRemainderAtTheWordThePrimaryValueStoppedAt() throws IOException {
      final var field = CustomPdfField.builder().value(PROSE).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(200f, 2)));

      final var prefix =
          result.primaryValue().substring(0, result.primaryValue().length() - LONG_SUFFIX.length());
      final var nextWord = PROSE.substring(prefix.stripTrailing().length()).trim().split("\\s")[0];

      assertTrue(
          result.overflowRemainder().startsWith("... " + nextWord),
          "remainder should continue at '%s' but was '%s'"
              .formatted(nextWord, result.overflowRemainder()));
    }

    @Test
    void shallNotStartTheRemainderMidWord() throws IOException {
      final var field = CustomPdfField.builder().value(PROSE).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(200f, 2)));

      final var firstWord = result.overflowRemainder().substring("... ".length()).split("\\s")[0];

      assertTrue(
          PROSE.contains(" " + firstWord) || PROSE.startsWith(firstWord),
          "remainder started mid-word with '%s'".formatted(firstWord));
    }
  }

  /**
   * A line break is a word boundary too. Snapping the split back to the nearest space alone steps
   * over an intervening break, moving the last word of the preceding paragraph into the overflow
   * and leaving the overflow page opening on a blank line.
   */
  @Nested
  class WhenSplitLandsOnALineBreak {

    private static final float WIDTH = 200f;

    private static final String FIRST_PARAGRAPH =
        "Patienten har nedsatt funktion i höger knä vilket påverkar rörelseförmågan lectus.";

    private static final String SECOND_PARAGRAPH =
        "Aenean luctus porta aliquam och ger smärta vid belastning under längre perioder.";

    private static final String THIRD_PARAGRAPH =
        "Cras volutpat massa eros och patienten bör följas upp inom tre månader.";

    /** A field that has room for the first paragraph and its suffix, but nothing more. */
    private FieldGeometry geometryFittingFirstParagraphOnly() throws IOException {
      final var withSuffix = FIRST_PARAGRAPH + " " + LONG_SUFFIX;
      return multiLine(WIDTH, textLayout.countLines(withSuffix, multiLine(WIDTH, 99)));
    }

    private FieldValueResult split(String value) throws IOException {
      final var field = CustomPdfField.builder().value(value).overflow(overflow()).build();
      return processor.process(field, HELVETICA, Optional.of(geometryFittingFirstParagraphOnly()));
    }

    private static void assertReconstructs(String original, FieldValueResult result) {
      final var prefix =
          result.primaryValue().substring(0, result.primaryValue().length() - LONG_SUFFIX.length());
      final var rest = result.overflowRemainder().substring("... ".length());

      assertEquals(original.replaceAll("\\s", ""), (prefix + rest).replaceAll("\\s", ""));
    }

    @Test
    void shallKeepTheLastWordBeforeABlankLineInTheField() throws IOException {
      final var result = split(FIRST_PARAGRAPH + "\n\n" + SECOND_PARAGRAPH);

      assertEquals(FIRST_PARAGRAPH + " " + LONG_SUFFIX, result.primaryValue());
    }

    @Test
    void shallStartTheRemainderAtTheNextParagraph() throws IOException {
      final var result = split(FIRST_PARAGRAPH + "\n\n" + SECOND_PARAGRAPH);

      assertEquals("... " + SECOND_PARAGRAPH, result.overflowRemainder());
    }

    /**
     * A single break costs one line rather than two, so the split legitimately continues into the
     * next paragraph. What must not happen is a word being dropped at the seam.
     */
    @Test
    void shallNotLoseTheWordBeforeASingleLineBreak() throws IOException {
      final var value = FIRST_PARAGRAPH + "\n" + SECOND_PARAGRAPH;

      final var result = split(value);

      assertTrue(
          result.primaryValue().contains("lectus."),
          "field lost the last word before the line break: '%s'".formatted(result.primaryValue()));
      assertReconstructs(value, result);
    }

    @Test
    void shallNotStartTheRemainderWithABlankLineForASingleLineBreak() throws IOException {
      final var result = split(FIRST_PARAGRAPH + "\n" + SECOND_PARAGRAPH);

      assertEquals(
          result.overflowRemainder().stripLeading(),
          result.overflowRemainder(),
          "remainder must not open on whitespace");
    }

    @Test
    void shallPreserveBlankLinesBetweenParagraphsInsideTheRemainder() throws IOException {
      final var result =
          split(FIRST_PARAGRAPH + "\n\n" + SECOND_PARAGRAPH + "\n\n" + THIRD_PARAGRAPH);

      assertEquals(
          "... " + SECOND_PARAGRAPH + "\n\n" + THIRD_PARAGRAPH, result.overflowRemainder());
    }

    @Test
    void shallReconstructTheOriginalValueAcrossTheLineBreak() throws IOException {
      final var value = FIRST_PARAGRAPH + "\n\n" + SECOND_PARAGRAPH;

      final var result = split(value);

      assertReconstructs(value, result);
    }
  }

  @Nested
  class WhenValueIsOnlyJustTooLong {

    @Test
    void shallSplitAValueThatExceedsTheFieldByASingleCharacter() throws IOException {
      final var geometry = singleLine(100f);
      final var value = longestValueFitting(geometry) + "X";
      final var field = CustomPdfField.builder().value(value).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      assertTrue(textLayout.fits(result.primaryValue(), geometry));
      assertNotEquals(result.primaryValue(), value);
    }

    private String longestValueFitting(FieldGeometry geometry) throws IOException {
      final var value = new StringBuilder();
      while (textLayout.fits(value + "a", geometry)) {
        value.append('a');
      }
      return value.toString();
    }
  }

  @Nested
  class WhenNoWordBoundaryIsAvailable {

    @Test
    void shallSplitASingleWordLongerThanTheWholeField() throws IOException {
      final var geometry = multiLine(120f, 2);
      final var value = "Supercalifragilisticexpialidocious".repeat(10);
      final var field = CustomPdfField.builder().value(value).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      assertTrue(textLayout.fits(result.primaryValue(), geometry));
      assertFalse(result.overflowRemainder().isBlank());
    }

    @Test
    void shallNotLoseCharactersWhenSplittingASingleLongWord() throws IOException {
      final var geometry = multiLine(120f, 2);
      final var value = "Supercalifragilisticexpialidocious".repeat(10);
      final var field = CustomPdfField.builder().value(value).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      final var prefix =
          result.primaryValue().substring(0, result.primaryValue().length() - LONG_SUFFIX.length());
      final var rest = result.overflowRemainder().substring("... ".length());

      assertEquals(value, (prefix + rest).replaceAll("\\s", ""));
    }

    /** A leading space must not snap the split back to index zero and empty the field. */
    @Test
    void shallNotProduceAnEmptyPrimaryValueWhenTheValueStartsWithASpace() throws IOException {
      final var geometry = multiLine(120f, 2);
      final var value = " " + "Supercalifragilisticexpialidocious".repeat(10);
      final var field = CustomPdfField.builder().value(value).overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      assertNotEquals(LONG_SUFFIX, result.primaryValue());
      assertTrue(result.primaryValue().length() > LONG_SUFFIX.length());
    }
  }

  @Nested
  class WhenTheFieldIsTooSmallForAnySuffix {

    @Test
    void shallFallBackToTruncationWhenNeitherSuffixLeavesRoom() throws IOException {
      final var geometry = singleLine(12f);
      final var field =
          CustomPdfField.builder().value("Some value that cannot fit").overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      assertNull(result.overflowRemainder());
    }

    @Test
    void shallKeepValueUnchangedWhenOverflowFieldHasNoRoomForAnySuffix() throws IOException {
      final var geometry = singleLine(12f);
      final var field =
          CustomPdfField.builder().value("Some value that cannot fit").overflow(overflow()).build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      assertEquals("Some value that cannot fit", result.primaryValue());
    }

    @Test
    void shallKeepValueUnchangedWhenNoCharacterFitsBesideTheEllipsis() throws IOException {
      final var geometry = singleLine(12f);
      final var field = CustomPdfField.builder().value("Some value that cannot fit").build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      assertEquals("Some value that cannot fit", result.primaryValue());
    }

    /** Regression: FK7804 months box, where "10" is wider than the field but "..." is not. */
    @Test
    void shallKeepTwoDigitNumberInAFieldOnlyWideEnoughForTheEllipsis() throws IOException {
      final var geometry = singleLine(widthOf("..."));
      final var field = CustomPdfField.builder().value("10").build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      assertEquals("10", result.primaryValue());
      assertNull(result.overflowRemainder());
    }

    @Test
    void shallStillTruncateWhenOneCharacterFitsBesideTheEllipsis() throws IOException {
      final var geometry = singleLine(widthOf("S..."));
      final var field = CustomPdfField.builder().value("Some value that cannot fit").build();

      final var result = processor.process(field, HELVETICA, Optional.of(geometry));

      assertEquals("S...", result.primaryValue());
    }
  }

  @Nested
  class WhenTextFittingIsSkipped {

    private static final String CONTACT_BLOCK =
        """
        Vårdcentralen Johannelund i Linköping med ett mycket långt namn
        Johannelundsgatan 42, lägenhet 1203, 582 46 Linköping
        Telefon: 010-123 45 67""";

    @Test
    void shallKeepValueUnchangedWhenItDoesNotFit() throws IOException {
      final var field = CustomPdfField.builder().value(CONTACT_BLOCK).skipTextFitting(true).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(120f, 3)));

      assertEquals(CONTACT_BLOCK, result.primaryValue());
    }

    @Test
    void shallPreserveLineBreaksWhenValueDoesNotFit() throws IOException {
      final var field = CustomPdfField.builder().value(CONTACT_BLOCK).skipTextFitting(true).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(120f, 3)));

      assertEquals(3, result.primaryValue().lines().count());
    }

    @Test
    void shallNotAppendEllipsisWhenValueDoesNotFitASingleLine() throws IOException {
      final var value = "Vårdcentralen Johannelund, Johannelundsgatan 42, 582 46 Linköping";
      final var field = CustomPdfField.builder().value(value).skipTextFitting(true).build();

      final var result = processor.process(field, HELVETICA, Optional.of(singleLine(60f)));

      assertEquals(value, result.primaryValue());
    }

    @Test
    void shallNotProduceOverflowRemainder() throws IOException {
      final var field = CustomPdfField.builder().value(CONTACT_BLOCK).skipTextFitting(true).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(120f, 3)));

      assertNull(result.overflowRemainder());
    }

    @Test
    void shallStillSanitizeCharactersTheFontCannotEncode() throws IOException {
      final var field =
          CustomPdfField.builder()
              .value("Telefon: 010\u2013123 45 67")
              .skipTextFitting(true)
              .build();

      final var result = processor.process(field, HELVETICA, Optional.of(singleLine(20f)));

      assertEquals("Telefon: 010-123 45 67", result.primaryValue());
    }

    @Test
    void shallStillSplitToOverflowSheetWhenOneIsConfigured() throws IOException {
      final var field =
          CustomPdfField.builder()
              .value(CONTACT_BLOCK)
              .overflow(overflow())
              .skipTextFitting(true)
              .build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(200f, 3)));

      assertTrue(result.primaryValue().endsWith(LONG_SUFFIX));
      assertNotNull(result.overflowRemainder());
    }

    @Test
    void shallKeepValueUnchangedWhenOverflowSheetHasNoRoomForAnySuffix() throws IOException {
      final var field =
          CustomPdfField.builder()
              .value(CONTACT_BLOCK)
              .overflow(overflow())
              .skipTextFitting(true)
              .build();

      final var result = processor.process(field, HELVETICA, Optional.of(singleLine(12f)));

      assertEquals(CONTACT_BLOCK, result.primaryValue());
    }

    @Test
    void shallTruncateWhenTextFittingIsNotSkipped() throws IOException {
      final var field =
          CustomPdfField.builder().value(CONTACT_BLOCK).skipTextFitting(false).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(120f, 3)));

      assertNotEquals(CONTACT_BLOCK, result.primaryValue());
    }

    @Test
    void shallDefaultToFittingWhenFlagIsNotSet() throws IOException {
      final var field = CustomPdfField.builder().value(CONTACT_BLOCK).build();

      final var result = processor.process(field, HELVETICA, Optional.of(multiLine(120f, 3)));

      assertFalse(result.primaryValue().contains("\n"));
    }
  }

  private static float widthOf(String text) throws IOException {
    return HELVETICA.getStringWidth(text) / 1000f * FONT_SIZE;
  }
}
