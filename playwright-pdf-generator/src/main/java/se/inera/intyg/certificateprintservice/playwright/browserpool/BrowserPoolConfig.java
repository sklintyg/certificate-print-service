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

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(BrowserPoolProperties.class)
public class BrowserPoolConfig {

  private final BrowserPoolProperties browserPoolProperties;

  private BrowserPool browserPool;

  @Bean
  public BrowserPool browserPool() throws Exception {
    final var config = new GenericObjectPoolConfig<PlaywrightBrowser>();
    config.setMinIdle(browserPoolProperties.min().idle());
    config.setMaxIdle(browserPoolProperties.max().idle());
    config.setMaxTotal(browserPoolProperties.max().total());
    config.setTestOnCreate(true);
    config.setTestOnReturn(true);

    @SuppressWarnings("java:S2095")
    final var pool = new BrowserPool(new BrowserFactory(), config);
    pool.addObjects(browserPoolProperties.min().idle());
    browserPool = pool;
    return browserPool;
  }

  @PreDestroy
  void destroy() {
    browserPool.close();
  }
}
