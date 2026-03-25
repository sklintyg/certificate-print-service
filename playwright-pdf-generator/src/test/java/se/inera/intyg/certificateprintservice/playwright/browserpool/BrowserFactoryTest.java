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

class BrowserFactoryTest {

  private static final String CHROMIUM = "chromium";
  private static final int ONE = 1;
  private final BrowserFactory browserFactory = new BrowserFactory();

  // @Test
  void shouldReturnChromiumBrowser() {
    final var browser = browserFactory.create();
    final var context = browser.getBrowserContext();

    assertAll(
        () ->
            assertEquals(
                CHROMIUM, browser.getBrowser().browserType().name(), "Should be chromium browser"),
        () ->
            assertEquals(
                ONE, context.browser().contexts().size(), "Should create browser context"));
  }
}
