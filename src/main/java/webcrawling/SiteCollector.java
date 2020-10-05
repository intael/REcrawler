package webcrawling;

import java.io.IOException;
import org.jsoup.nodes.Document;

public interface SiteCollector {

  Document collect(String url) throws IOException;
}
