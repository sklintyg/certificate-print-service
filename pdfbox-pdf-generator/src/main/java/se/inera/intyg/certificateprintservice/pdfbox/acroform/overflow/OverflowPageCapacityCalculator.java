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
package se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow;

import org.springframework.stereotype.Component;

@Component
public class OverflowPageCapacityCalculator {

  public int calculateMaxLines(float fieldHeight, float fontSize, float lineSpacing) {
    return calculateMaxLines(fieldHeight, fontSize, lineSpacing, 0f);
  }

  public int calculateMaxLines(
      float fieldHeight, float fontSize, float lineSpacing, float topMargin) {
    final var availableHeight = fieldHeight - topMargin;
    if (availableHeight <= 0) {
      return 0;
    }
    final var lineHeight = fontSize * lineSpacing;
    return (int) Math.floor(availableHeight / lineHeight);
  }
}
