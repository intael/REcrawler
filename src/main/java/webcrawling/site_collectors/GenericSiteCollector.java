package webcrawling.site_collectors;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.IOException;
import java.net.Proxy;
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
  private final int timeoutInMiliseconds;
  private final int maxRetries;
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

  public Optional<Document> collect(@NotNull String url) {
    Connection conn = Jsoup.connect(url).timeout(timeoutInMiliseconds);
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
}
