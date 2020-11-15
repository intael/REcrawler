package webcrawling.repositories;

import java.io.IOException;
import java.net.Proxy;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

class RemoteRestProxyRepositoryTest {
  private final String PROXY_SCRAPE_ENDPOINT =
      "https://api.proxyscrape.com/?request=getproxies&proxytype=http&timeout=750&country=all&ssl=all&anonymity=all";
  private RemoteRestProxyRepository instance;

  @BeforeEach
  void setUp() throws IOException{
    this.instance = new RemoteRestProxyRepository(PROXY_SCRAPE_ENDPOINT);
    this.instance.collectProxyList();
  }

  @Test
  @EnabledIfEnvironmentVariable(named = "ENABLE_INTEGRATION_TESTS", matches = "true")
  void testcollectProxyListGetsDataFromRemote() throws IOException {
    Assert.assertTrue(this.instance.getAllUnusedProxies().size() > 0);
  }

  @Test
  @EnabledIfEnvironmentVariable(named = "ENABLE_INTEGRATION_TESTS", matches = "true")
  void testgetRandomProxyReturnsAProxy() throws IOException {
    Assert.assertNotNull(this.instance.getRandomUnusedProxy());
  }

  @Test
  @EnabledIfEnvironmentVariable(named = "ENABLE_INTEGRATION_TESTS", matches = "true")
  void testWorkingProxiesGetProperlyRegistered() throws IOException {
    Proxy proxy = this.instance.getRandomUnusedProxy();
    this.instance.registerWorkingProxy(proxy);
    Assert.assertEquals(1, this.instance.getNumberOfWorkingProxies());
  }

  @Test
  @EnabledIfEnvironmentVariable(named = "ENABLE_INTEGRATION_TESTS", matches = "true")
  void testFaultyProxiesGetProperlyRegistered() throws IOException {
    Proxy proxy = this.instance.getRandomUnusedProxy();
    this.instance.registerWorkingProxy(proxy);
    this.instance.deregisterWorkingProxy(proxy);
    Assert.assertEquals(1, this.instance.getNumberOfFaultyProxies());
  }
}
