package webcrawling.site_collectors;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

public interface SiteCollector {

  Optional<Document> collect(String url) throws IOException;

  Optional<Document> collect(String url, int maxRetries, Map<String, String> headers)
      throws IOException;

  Optional<Document> collect(
      String url, int maxRetries, Map<String, String> headers, Map<String, String> cookies)
      throws IOException;

  Optional<Connection> probingRequest(
      String url, int maxRetries, Map<String, String> headers, Map<String, String> cookies)
      throws IOException;
}
