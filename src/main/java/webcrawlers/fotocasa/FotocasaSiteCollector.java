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

public class FotocasaSiteCollector implements SiteCollector {

  private final String userAgent;
  private final Map<String, String> cookies;

  public FotocasaSiteCollector(
      @NotNull String userAgent,
      Map<String, String> cookies,
      Proxy proxy,
      int timeout,
      int max_retries) {
    this.userAgent = userAgent;
    this.cookies = cookies;
    this.proxy = proxy;
    this.timeout = timeout;
    this.max_retries = max_retries;
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

  private Proxy proxy;
  private final int timeout; // in milliseconds
  private final int max_retries;
  private final Logger logger = LoggerFactory.getLogger(FotocasaSiteCollector.class);

  // TODO: Add stateful usage statistics (eg, flush method)
  public Optional<Document> collect(String url) {
    Connection conn = Jsoup.connect(url).userAgent(userAgent).timeout(timeout);
    if (proxy != null) conn = conn.proxy(proxy);
    if (cookies != null) conn = addCookies(conn);
    int attempts = 0;
    while (attempts < max_retries) {
      try {
        return Optional.of(conn.get());
      } catch (IOException requestError) {
        attempts++;
        // TODO: Make logging clearer and more informative
        logger.error("Attempt: " + attempts + ".\nTrace: " + requestError.toString());
      }
    }
    return Optional.empty();
  }

  private Connection addCookies(Connection connection) {
    for (String cookie : cookies.keySet()) {
      connection = connection.cookie(cookie, cookies.get(cookie));
    }
    return connection;
  }
}
