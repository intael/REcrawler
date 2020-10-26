package webcrawling.site_collectors;

import java.io.IOException;
import java.util.Optional;
import org.jsoup.nodes.Document;
import webcrawling.site_collectors.SiteCollectorRanOutOfProxies;

public interface SiteCollector {

  Optional<Document> collect(String url) throws IOException, SiteCollectorRanOutOfProxies;

  Optional<Document> collect(String url, int maxRetries)
      throws IOException, SiteCollectorRanOutOfProxies;
}
