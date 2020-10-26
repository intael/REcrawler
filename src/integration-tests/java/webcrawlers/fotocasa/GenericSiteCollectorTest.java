package webcrawlers.fotocasa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrawling.repositories.ProxyRepository;
import webcrawling.repositories.RemoteRestProxyRepository;
import webcrawling.repositories.ShortListUserAgentRepository;
import webcrawling.repositories.UserAgentRepository;
import webcrawling.site_collectors.GenericSiteCollector;

class GenericSiteCollectorTest {

  private final Logger logger = LoggerFactory.getLogger(GenericSiteCollectorTest.class);
  GenericSiteCollector instanceWithProxy;

  private Optional<Document> fetchFuture(Future<Optional<Document>> f) {
    try {
      return f.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  @BeforeEach
  void setUp() {
    UserAgentRepository userAgentRepo = new ShortListUserAgentRepository();
    // TODO: Move this endpoint to properties file
    ProxyRepository proxyRepo =
        new RemoteRestProxyRepository(
            "https://api.proxyscrape.com/?request=getproxies&proxytype=http&timeout=750&country=all&ssl=all&anonymity=all");
    this.instanceWithProxy = new GenericSiteCollector(userAgentRepo, null, null, proxyRepo, 60_000, 6);
  }

  @Test
  @EnabledIfEnvironmentVariable(named = "ENABLE_INTEGRATION_TESTS", matches = "true")
  void siteCollectorWithProxyReturnsSomeHtml() {
    // TODO: Add request time stats
    int searches = 100;
    int index = 0;
    final int SUCCESS_CRITERIA = 30;
    final GenericSiteCollector collector = this.instanceWithProxy;
    List<Callable<Optional<Document>>> callables = new ArrayList<>();
    ExecutorService exec =
        Executors.newFixedThreadPool(
            searches); // It's a IO bound task, so threads are going to be idling most of the time
    while (index < searches) {
      callables.add(
          () -> collector.collect("https://www.fotocasa.es/en/buy/homes/barcelona/all-zones/l"));
      index++;
    }
    try {
      List<Future<Optional<Document>>> futures =
          exec.invokeAll(callables, Long.MAX_VALUE, TimeUnit.NANOSECONDS);
      exec.shutdown();
      long successes =
          futures.stream()
              .filter(
                  (f) -> fetchFuture(f).isPresent() && fetchFuture(f).get().toString().length() > 0)
              .count();
      logger.info("Successes: " + successes + ". Total: " + futures.size());
      Assert.assertTrue(
          successes
              >= SUCCESS_CRITERIA); // we consider a success that at least 30% of the proxies do
      // provide a response
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
