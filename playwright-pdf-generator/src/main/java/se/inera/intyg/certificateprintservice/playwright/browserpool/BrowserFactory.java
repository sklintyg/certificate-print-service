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
