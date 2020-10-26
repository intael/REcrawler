package webcrawling.repositories;

import java.io.IOException;
import java.net.Proxy;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoteRestProxyRepositoryTest {
  private final String PROXY_SCRAPE_ENDPOINT =
      "https://api.proxyscrape.com/?request=getproxies&proxytype=http&timeout=750&country=all&ssl=all&anonymity=all";
  private RemoteRestProxyRepository instance;

  @BeforeEach
  void setUp() {
    this.instance = new RemoteRestProxyRepository(PROXY_SCRAPE_ENDPOINT);
  }

  @Test
  void testcollectProxyListGetsDataFromRemote() throws IOException {
    this.instance.collectProxyList();
    Assert.assertTrue(this.instance.getAllUnusedProxies().size() > 0);
  }

  @Test
  void testgetRandomProxyReturnsAProxy() throws IOException {
    this.instance.collectProxyList();
    Assert.assertNotNull(this.instance.getRandomUnusedProxy());
  }

  @Test
  void testWorkingProxiesGetProperlyRegistered() throws IOException {
    this.instance.collectProxyList();
    Proxy proxy = this.instance.getRandomUnusedProxy();
    this.instance.registerWorkingProxy(proxy);
    Assert.assertEquals(1, this.instance.getNumberOfWorkingProxies());
  }

  @Test
  void testFaultyProxiesGetProperlyRegistered() throws IOException {
    this.instance.collectProxyList();
    Proxy proxy = this.instance.getRandomUnusedProxy();
    this.instance.registerWorkingProxy(proxy);
    this.instance.deregisterWorkingProxy(proxy);
    Assert.assertEquals(1, this.instance.getNumberOfFaultyProxies());
  }
}
