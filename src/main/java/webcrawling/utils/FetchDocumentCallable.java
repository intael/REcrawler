package webcrawling.utils;

import java.io.IOException;
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
  private final SiteCollector siteCollector;

  public FetchDocumentCallable(@NotNull String url, @NotNull SiteCollector siteCollector) {
    this.url = url;
    this.siteCollector = siteCollector;
  }

  @Override
  public Optional<Document> call() {
    try {
      LOGGER.info("Collecting: " + url);
      return this.siteCollector.collect(url);
    } catch (IOException ioe) {
      LOGGER.error("Failed to fetch document. Exception: " + ioe.toString());
      return Optional.empty();
    }
  }
}
