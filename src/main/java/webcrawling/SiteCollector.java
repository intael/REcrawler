package webcrawling;

import java.io.IOException;
import java.util.Optional;
import org.jsoup.nodes.Document;

public interface SiteCollector {

  Optional<Document> collect(String url) throws IOException;
  Optional<Document> collect(String url, int maxRetries) throws IOException;
}
