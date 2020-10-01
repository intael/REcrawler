package webcrawling;

import org.jsoup.nodes.Document;

public interface SiteCollector {

  Document collect(String url);
}
