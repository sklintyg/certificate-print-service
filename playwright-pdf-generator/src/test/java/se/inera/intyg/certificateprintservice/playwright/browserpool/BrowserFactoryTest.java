package se.inera.intyg.certificateprintservice.playwright.browserpool;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BrowserFactoryTest {

  private static final String CHROMIUM = "chromium";
  private static final int ONE = 1;
  private final BrowserFactory browserFactory = new BrowserFactory();

  //@Test
  void shouldReturnChromiumBrowser() {
    final var browser = browserFactory.create();
    final var context = browser.getBrowserContext();

    assertAll(
        () -> assertEquals(CHROMIUM, browser.getBrowser().browserType().name(),
            "Should be chromium browser"),
        () -> assertEquals(ONE, context.browser().contexts().size(),
            "Should create browser context")
    );
  }

}
