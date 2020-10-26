package webcrawlers.fotocasa;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrawling.SiteCollector;
import webcrawling.SiteCollectorRanOutOfProxies;
import webcrawling.repositories.ProxyRepository;
import webcrawling.repositories.UserAgentRepository;

// TODO: Add stateful usage statistics (eg, flush method)
public class FotocasaSiteCollector implements SiteCollector {

  private final UserAgentRepository userAgentRepo;
  private final Map<String, String> cookies;
  private final Map<String, String> headers;
  private final int timeoutInMiliseconds; // in milliseconds
  private final int maxRetries;
  private final ProxyRepository proxyRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(FotocasaSiteCollector.class);
  private static final int PROXY_SWAP_ATTEMPTS_MULTIPLE = 3;

  public FotocasaSiteCollector(
      @NotNull UserAgentRepository userAgentRepo,
      Map<String, String> cookies,
      Map<String, String> headers,
      ProxyRepository proxyRepository,
      int timeoutInMiliseconds,
      int maxRetries) {
    this.userAgentRepo = userAgentRepo;
    this.cookies = cookies;
    this.headers = headers;
    this.proxyRepository = proxyRepository;
    this.timeoutInMiliseconds = timeoutInMiliseconds;
    this.maxRetries = maxRetries;
    try {
      this.proxyRepository.collectProxyList();
    } catch (IOException ioException) {
      LOGGER.error(
          "Error fetching proxies. Proceeding without proxies: " + ioException.getMessage());
    }
  }

  public FotocasaSiteCollector(
      @NotNull UserAgentRepository userAgentRepo, @NotNull ProxyRepository proxyRepository) {
    this(userAgentRepo, null, null, proxyRepository, 30_000, 6);
  }

  public Optional<Document> collect(String url, int maxRetries)
      throws SiteCollectorRanOutOfProxies {
    Connection conn =
        Jsoup.connect(url).timeout(timeoutInMiliseconds);
    if (headers != null) conn = addHeaders(conn);
    if (cookies != null) conn = addCookies(conn);
    int attempts = 0;
    Proxy proxy = fetchProxy(attempts);
    conn = conn.proxy(proxy);
    while (attempts < maxRetries) {
      conn = conn.userAgent(userAgentRepo.getRandomUserAgent());
      try {
        Document document = conn.get();
        proxyRepository.registerWorkingProxy(proxy);
        return Optional.of(document);
      }
      catch (IOException requestError) {
        if (++attempts % PROXY_SWAP_ATTEMPTS_MULTIPLE == 0) {
          proxyRepository.registerFaultyProxy(proxy);
          proxy = fetchProxy(attempts);
        }
        // TODO: Make logging clearer and more informative
        LOGGER.error(
            String.format(
                "Failure with proxy: %s. Error: %s. Attempt: %s.",
                proxy.toString(), requestError.toString(), attempts));
      }
    }
    return Optional.empty();
  }

  private Proxy fetchProxy(int attemptsSoFar) throws SiteCollectorRanOutOfProxies {
    Proxy fetchedProxy;
    if (attemptsSoFar % PROXY_SWAP_ATTEMPTS_MULTIPLE == 0
        && proxyRepository.getNumberOfWorkingProxies() > 0) {
      fetchedProxy = proxyRepository.getRandomWorkingProxy();
    } else if (proxyRepository.getNumberOfUnusedProxies() > 0) {
      fetchedProxy = proxyRepository.getRandomUnusedProxy();
    } else {
      throw new SiteCollectorRanOutOfProxies("SiteCollector ran out of Proxies!");
    }
    return fetchedProxy;
  }

  public Optional<Document> collect(String url) throws SiteCollectorRanOutOfProxies {
    return collect(url, maxRetries);
  }

  private Connection addHeaders(Connection connection) {
    for (String header : headers.keySet()) {
      connection = connection.header(header, cookies.get(header));
    }
    return connection;
  }

  private Connection addCookies(Connection connection) {
    for (String cookie : cookies.keySet()) {
      connection = connection.header(cookie, cookies.get(cookie));
    }
    return connection;
  }
}
