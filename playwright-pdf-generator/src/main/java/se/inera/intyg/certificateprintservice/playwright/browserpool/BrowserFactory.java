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

import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Playwright;
import java.util.Map;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class BrowserFactory extends BasePooledObjectFactory<PlaywrightBrowser> {

  @Override
  public PlaywrightBrowser create() {
    final var env = Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1");
    final var playwright = Playwright.create(new Playwright.CreateOptions().setEnv(env));
    final var launchOptions = new LaunchOptions().setChannel("chrome").setHeadless(true);
    final var browser = playwright.chromium().launch(launchOptions);
    return new PlaywrightBrowser(playwright, browser);
  }

  @Override
  public PooledObject<PlaywrightBrowser> wrap(PlaywrightBrowser browser) {
    return new DefaultPooledObject<>(browser);
  }
}
