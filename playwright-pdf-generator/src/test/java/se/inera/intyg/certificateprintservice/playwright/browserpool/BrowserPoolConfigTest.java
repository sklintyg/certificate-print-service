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
package se.inera.intyg.certificateprintservice.playwright.browserpool;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrowserPoolConfigTest {

  private static final int MIN_IDLE = 1;
  private static final int MAX_IDLE = 2;
  private static final int MAX_TOTAL = 3;
  private final BrowserPoolConfig browserPoolConfig =
      new BrowserPoolConfig(
          new BrowserPoolProperties(
              new BrowserPoolProperties.Max(MAX_IDLE, MAX_TOTAL),
              new BrowserPoolProperties.Min(MIN_IDLE)));

  // @Test
  void shouldSetBrowserPoolConfig() throws Exception {

    try (final var browserPool = browserPoolConfig.browserPool()) {

      assertAll(
          () -> assertEquals(MIN_IDLE, browserPool.getMinIdle(), "Should set MIN_IDLE"),
          () -> assertEquals(MAX_IDLE, browserPool.getMaxIdle(), "Should set MAX_IDLE"),
          () -> assertEquals(MAX_TOTAL, browserPool.getMaxTotal(), "Should set MAX_TOTAL"),
          () -> assertEquals(MIN_IDLE, browserPool.getCreatedCount(), "Should create browsers"),
          () -> assertTrue(browserPool.getTestOnCreate(), "Should test on create"),
          () -> assertTrue(browserPool.getTestOnReturn(), "Should test on return"));
    }
  }
}
