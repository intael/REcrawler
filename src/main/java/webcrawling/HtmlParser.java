package webcrawling;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface HtmlParser<T> {

  String HREF_ATTRIBUTE = "href";
  String A_HTML_TAG = "a";
  String SPAN_HTML_TAG = "span";
  String TABLE_HTML_TAG = "table";
  String TBODY_HTML_TAG = "tbody";
  String TD_HTML_TAG = "td";
  String TR_HTML_TAG = "tr";
  Logger LOGGER = LoggerFactory.getLogger(HtmlParser.class);

  List<T> parse(@NotNull Document document);
  static URL deserializeUrl(String urlString) {
    try {
      return new URL(urlString);
    } catch (MalformedURLException e) {
      LOGGER.error("Could not deserialize the URL: " + urlString);
      e.printStackTrace();
    }
    return null;
  }
}
