package webcrawlers.fotocasa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FotocasaSiteCollectorTest {

  private final Logger logger = LoggerFactory.getLogger(FotocasaSiteCollectorTest.class);
  FotocasaSiteCollector instanceWithProxy;
  FotocasaSiteCollector instanceWithoutProxy;
  private static final String USER_AGENTS_DIRECTORY = "user-agents";
  private static final String CHROME_USER_AGENTS_FILE = "Chrome.txt";
  private static final String PROXY_LISTS_DIRECTORY = "proxy-lists";
  private static final String PROXY_SCRAPE_LIST_FILE = "proxy-scrape-list.txt";
  private String exampleUserAgent;
  private List<Proxy> proxyAddresses;

  private Optional<String> pickFirstLineFromFile(File file) {
    Optional<String> firstLine = Optional.empty();
    try (BufferedReader buffer = new BufferedReader(new FileReader(file))) {
      firstLine = Optional.of(buffer.readLine());
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return firstLine;
  }

  private List<String> readAllLinesFromFile(File file) {
    List<String> addresses = new ArrayList<>();
    String line;
    try (BufferedReader buffer = new BufferedReader(new FileReader(file))) {
      while ((line = buffer.readLine()) != null) {
        addresses.add(line);
        if (line.isEmpty()) {
          break;
        }
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return addresses;
  }

  private Proxy createProxyFromString(String proxyAddressString) {
    String[] addressComponents = proxyAddressString.split(":");
    InetSocketAddress proxyAddress =
        new InetSocketAddress(addressComponents[0], Integer.parseInt(addressComponents[1]));
    return new Proxy(Proxy.Type.HTTP, proxyAddress);
  }

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
    Path projectDirectory = FileSystems.getDefault().getPath(".").normalize().toAbsolutePath();
    Path resourcesDirectory = Paths.get(projectDirectory.toString(), "src", "main", "resources");
    File userAgentsFilePath =
        Paths.get(resourcesDirectory.toString(), USER_AGENTS_DIRECTORY, CHROME_USER_AGENTS_FILE)
            .toFile();
    File proxyListFilePath =
        Paths.get(resourcesDirectory.toString(), PROXY_LISTS_DIRECTORY, PROXY_SCRAPE_LIST_FILE)
            .toFile();
    Optional<String> userAgent = pickFirstLineFromFile(userAgentsFilePath);
    userAgent.ifPresent(s -> this.exampleUserAgent = s);
    List<String> proxiAddresses = readAllLinesFromFile(proxyListFilePath);
    this.proxyAddresses =
        proxiAddresses.stream().map(this::createProxyFromString).collect(Collectors.toList());
    this.instanceWithoutProxy = new FotocasaSiteCollector(this.exampleUserAgent);
  }

  @Test
  @EnabledIfEnvironmentVariable(named = "ENABLE_INTEGRATION_TESTS", matches = "true")
  void siteCollectorWithoutProxyReturnsSomeHtml() {
    Optional<Document> doc =
        this.instanceWithoutProxy.collect(
            "https://www.fotocasa.es/en/buy/homes/barcelona/all-zones/l");
    Assert.assertTrue(doc.isPresent());
    Assert.assertTrue(doc.toString().length() > 0);
  }

  @Test
  @EnabledIfEnvironmentVariable(named = "ENABLE_INTEGRATION_TESTS", matches = "true")
  void siteCollectorWithProxyReturnsSomeHtml() {
    // TODO: Add request time stats
    int proxies = 100;
    int index = 0;
    final int SUCCESS_CRITERIA = 30;
    List<Future<Optional<Document>>> futures = new ArrayList<>();
    // TODO: Randomize User Agent used
    final FotocasaSiteCollector proxiedCollector =
        new FotocasaSiteCollector(
            this.exampleUserAgent, null, this.proxyAddresses.get(0), 120_000, 6);
    ExecutorService exec =
        Executors.newFixedThreadPool(
            proxies); // It's a IO bound task, so threads are going to be idling most of the time
    while (index < proxies) {
      proxiedCollector.setProxy(proxyAddresses.get(index));
      futures.add(
          exec.submit(
              () -> {
                logger.info("Using proxy: " + proxiedCollector.getProxy().toString());
                return proxiedCollector.collect(
                    "https://www.fotocasa.es/en/buy/homes/barcelona/all-zones/l");
              }));
      index++;
    }
    exec.shutdown();
    try {
      exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
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
