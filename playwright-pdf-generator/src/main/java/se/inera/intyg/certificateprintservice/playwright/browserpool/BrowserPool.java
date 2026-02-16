package se.inera.intyg.certificateprintservice.playwright.browserpool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@Slf4j
public class BrowserPool extends GenericObjectPool<PlaywrightBrowser> {

  public BrowserPool(PooledObjectFactory<PlaywrightBrowser> factory,
      GenericObjectPoolConfig<PlaywrightBrowser> config) {
    super(factory, config);
  }

  @Override
  public PlaywrightBrowser borrowObject() throws Exception {
    var borrowed = super.borrowObject();
    log.info("Available browsers in pool: {}", this.getNumIdle());
    return borrowed;
  }

}
