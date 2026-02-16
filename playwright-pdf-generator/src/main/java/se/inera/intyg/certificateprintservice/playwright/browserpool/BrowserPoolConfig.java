package se.inera.intyg.certificateprintservice.playwright.browserpool;

import jakarta.annotation.PreDestroy;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrowserPoolConfig {

  @Value("${browser.pool.min.idle}")
  private int browserPoolMinIdle;
  @Value("${browser.pool.max.idle}")
  private int browserPoolMaxIdle;
  @Value("${browser.pool.max.total}")
  private int browserPoolMaxTotal;

  private BrowserPool browserPool;

  @Bean
  public BrowserPool browserPool() throws Exception {
    final var config = new GenericObjectPoolConfig<PlaywrightBrowser>();
    config.setMinIdle(browserPoolMinIdle);
    config.setMaxIdle(browserPoolMaxIdle);
    config.setMaxTotal(browserPoolMaxTotal);
    config.setTestOnCreate(true);
    config.setTestOnReturn(true);

    @SuppressWarnings("java:S2095") final var pool = new BrowserPool(new BrowserFactory(), config);
    pool.addObjects(browserPoolMinIdle);
    browserPool = pool;
    return browserPool;
  }

  @PreDestroy
  void destroy() {
    browserPool.close();
  }

}
