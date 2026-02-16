package se.inera.intyg.certificateprintservice.playwright.browserpool;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.test.util.ReflectionTestUtils;

class BrowserPoolConfigTest {

  private static final int MIN_IDLE = 1;
  private static final int MAX_IDLE = 2;
  private static final int MAX_TOTAL = 3;
  private final BrowserPoolConfig browserPoolConfig = new BrowserPoolConfig();

  //@Test
  void shouldSetBrowserPoolConfig() throws Exception {
    ReflectionTestUtils.setField(browserPoolConfig, "browserPoolMinIdle", MIN_IDLE);
    ReflectionTestUtils.setField(browserPoolConfig, "browserPoolMaxIdle", MAX_IDLE);
    ReflectionTestUtils.setField(browserPoolConfig, "browserPoolMaxTotal", MAX_TOTAL);

    try (final var browserPool = browserPoolConfig.browserPool()) {

      assertAll(
          () -> assertEquals(MIN_IDLE, browserPool.getMinIdle(), "Should set MIN_IDLE"),
          () -> assertEquals(MAX_IDLE, browserPool.getMaxIdle(), "Should set MAX_IDLE"),
          () -> assertEquals(MAX_TOTAL, browserPool.getMaxTotal(), "Should set MAX_TOTAL"),
          () -> assertEquals(MIN_IDLE, browserPool.getCreatedCount(), "Should create browsers"),
          () -> assertTrue(browserPool.getTestOnCreate(), "Should test on create"),
          () -> assertTrue(browserPool.getTestOnReturn(), "Should test on return")
      );
    }
  }

}
