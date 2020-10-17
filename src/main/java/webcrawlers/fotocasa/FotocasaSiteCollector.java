package webcrawlers.fotocasa;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrawling.SiteCollector;

// TODO: Add stateful usage statistics (eg, flush method)
public class FotocasaSiteCollector implements SiteCollector {

  private final String userAgent;
  private final Map<String, String> cookies;
  private Proxy proxy;
  private final int timeout; // in milliseconds
  private final int maxRetries;
  private final Logger logger = LoggerFactory.getLogger(FotocasaSiteCollector.class);

  public FotocasaSiteCollector(
      @NotNull String userAgent,
      Map<String, String> cookies,
      Proxy proxy,
      int timeout,
      int maxRetries) {
    this.userAgent = userAgent;
    this.cookies = cookies;
    this.proxy = proxy;
    this.timeout = timeout;
    this.maxRetries = maxRetries;
  }

  public FotocasaSiteCollector(@NotNull String userAgent) {
    this(userAgent, null, null, 30_000, 3);
  }

  public Proxy getProxy() {
    return proxy;
  }

  public void setProxy(@NotNull Proxy proxy) {
    this.proxy = proxy;
  }

  public Optional<Document> collect(String url, int maxRetries) {
    Connection conn = Jsoup.connect(url).userAgent(userAgent).timeout(timeout);
    if (proxy != null) conn = conn.proxy(proxy);
    if (cookies != null) conn = addCookies(conn);
    int attempts = 0;
    while (attempts < maxRetries) {
      try {
        return Optional.of(conn.get());
      } catch (IOException requestError) {
        attempts++;
        // TODO: Make logging clearer and more informative
        logger.error(
            String.format(
                "Failure with proxy: %s. Attempt: %s. Trace: %s",
                this.proxy == null ? "Not using a proxy." : this.proxy.toString(),
                attempts,
                requestError.toString()));
      }
    }
    return Optional.empty();
  }

  public Optional<Document> collect(String url) {
    return collect(url, maxRetries);
  }

  private Connection addCookies(Connection connection) {
    for (String cookie : cookies.keySet()) {
      connection = connection.cookie(cookie, cookies.get(cookie));
    }
    return connection;
  }
}
