package webcrawlers.fotocasa;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrawling.SiteCollector;
import webcrawling.SiteCollectorRanOutOfProxies;

public class FetchDocumentCallable implements Callable<Optional<Document>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(FetchDocumentCallable.class);
  private final String url;
  private final int retries;
  private SiteCollector siteCollector;

  public FetchDocumentCallable(
      @NotNull String url, @NotNull SiteCollector siteCollector, int retries) {
    this.url = url;
    this.siteCollector = siteCollector;
    this.retries = retries;
  }

  @Override
  public Optional<Document> call() {
    try {
      return this.siteCollector.collect(url, retries);
    } catch (IOException ioe) {
      LOGGER.error("Failed to fetch document. Exception: " + ioe.toString());
    } catch (SiteCollectorRanOutOfProxies outOfProxies) {
      LOGGER.error("The program ran out of proxies to be used, returning empty Optional.");
    }
    return Optional.empty();
  }
}
