package webcrawling.site_collectors;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrawling.repositories.ProxyRepository;
import webcrawling.repositories.UserAgentRepository;
import webcrawling.repositories.dependency_injection.ProxyRepositoryQualifier;
import webcrawling.repositories.dependency_injection.UserAgentRepositoryQualifier;

// TODO: Add stateful usage statistics (eg, flush method)
public class GenericSiteCollector implements SiteCollector {

  protected final UserAgentRepository userAgentRepo;
  protected Map<String, String> cookies;
  protected Map<String, String> headers;
  protected final int timeoutInMiliseconds;
  protected final int maxRetries;
  protected final ProxyRepository proxyRepository;
  protected static final Logger LOGGER = LoggerFactory.getLogger(GenericSiteCollector.class);
  protected static final int PROXY_SWAP_ATTEMPTS_MULTIPLE = 3;

  @Inject
  public GenericSiteCollector(
      @UserAgentRepositoryQualifier @NotNull UserAgentRepository userAgentRepo,
      @ProxyRepositoryQualifier @NotNull ProxyRepository proxyRepository,
      @Named("timeoutInMiliseconds") int timeoutInMiliseconds,
      @Named("maxRetries") int maxRetries) {
    this.userAgentRepo = userAgentRepo;
    this.proxyRepository = proxyRepository;
    this.timeoutInMiliseconds = timeoutInMiliseconds;
    this.maxRetries = maxRetries;
    this.proxyRepository.collectProxyList();
  }

  public Optional<Document> collect(
      @NotNull String url,
      int maxRetries,
      @NotNull Map<String, String> headers,
      @NotNull Map<String, String> cookies) {
    Connection conn = Jsoup.connect(url).timeout(timeoutInMiliseconds);
    addHeaders(conn, headers);
    addCookies(conn, cookies);
    int attempts = 0;
    Proxy proxy = fetchProxy(attempts);
    conn = conn.proxy(proxy);
    while (attempts < maxRetries) {
      try {
        Document document = conn.get();
        proxyRepository.registerWorkingProxy(proxy);
        return Optional.of(document);
      } catch (IOException requestError) {
        if (requestError instanceof HttpStatusException
            && ((HttpStatusException) requestError).getStatusCode() == 404) return Optional.empty();
        attempts++;
        proxy = rotateProxy(attempts, proxy);
        // TODO: Make logging clearer and more informative
        LOGGER.error(
            String.format(
                "Failure with proxy: %s. Error: %s. Attempt: %s.",
                proxy.toString(), requestError.toString(), attempts));
      }
    }
    return Optional.empty();
  }

  public Optional<Document> collect(String url, int maxRetries, Map<String, String> headers) {
    return collect(url, maxRetries, headers, cookies);
  }

  public Optional<Document> collect(String url) {
    return collect(url, maxRetries, new HashMap<>());
  }

  @Override
  public Optional<Connection> probingRequest(
      String url, int maxRetries, Map<String, String> headers, Map<String, String> cookies) {
    int attempts = 0;
    Connection conn = Jsoup.connect(url).timeout(timeoutInMiliseconds);
    addHeaders(conn, headers);
    addCookies(conn, cookies);
    Proxy proxy = fetchProxy(attempts);
    conn = conn.proxy(proxy);
    while (attempts < maxRetries) {
      try {
        conn.get(); // discarding the resulting document
        proxyRepository.registerWorkingProxy(proxy);
        return Optional.of(conn);
      } catch (IOException requestError) {
        attempts++;
        proxy = rotateProxy(attempts, proxy);
        // TODO: Make logging clearer and more informative
        LOGGER.error(
            String.format(
                "Failure with proxy: %s. Error: %s. Attempt: %s.",
                proxy.toString(), requestError.toString(), attempts));
      }
    }
    return Optional.empty();
  }

  private Proxy rotateProxy(int attempts, Proxy proxy) {
    if (attempts % PROXY_SWAP_ATTEMPTS_MULTIPLE == 0) {
      proxyRepository.registerFaultyProxy(proxy);
      proxy = fetchProxy(attempts);
    }
    return proxy;
  }

  protected Proxy fetchProxy(int attemptsSoFar) {
    Proxy fetchedProxy;
    if (attemptsSoFar % PROXY_SWAP_ATTEMPTS_MULTIPLE == 0
        && proxyRepository.getNumberOfWorkingProxies() > 0) {
      fetchedProxy = proxyRepository.getRandomWorkingProxy();
    } else if (proxyRepository.getNumberOfUnusedProxies() > 0) {
      fetchedProxy = proxyRepository.getRandomUnusedProxy();
    } else {
      this.proxyRepository.collectProxyList();
      fetchedProxy = proxyRepository.getRandomUnusedProxy();
    }
    return fetchedProxy;
  }

  protected Connection addHeaders(Connection connection, Map<String, String> headers) {
    for (String header : headers.keySet()) {
      connection = connection.header(header, headers.get(header));
    }
    return connection;
  }

  protected Connection addCookies(Connection connection, Map<String, String> cookies) {
    for (String cookie : cookies.keySet()) {
      connection = connection.cookie(cookie, cookies.get(cookie));
    }
    return connection;
  }
}
