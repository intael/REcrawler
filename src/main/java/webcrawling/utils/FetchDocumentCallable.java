package webcrawling.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrawling.site_collectors.SiteCollector;

public class FetchDocumentCallable implements Callable<Optional<Document>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(FetchDocumentCallable.class);
  private final String url;
  private final int retries;
  private SiteCollector siteCollector;
  private Map<String, String> headers;
  private Map<String, String> cookies;

  public FetchDocumentCallable(
      @NotNull String url,
      @NotNull SiteCollector siteCollector,
      int retries,
      Map<String, String> headers,
      Map<String, String> cookies) {
    this.url = url;
    this.siteCollector = siteCollector;
    this.retries = retries;
    this.headers = headers;
    this.cookies = cookies;
  }

  @Override
  public Optional<Document> call() {
    try {
      return this.siteCollector.collect(url, retries, headers, cookies);
    } catch (IOException ioe) {
      LOGGER.error("Failed to fetch document. Exception: " + ioe.toString());
      return Optional.empty();
    }
  }
}
