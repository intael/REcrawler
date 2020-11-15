package webcrawlers.spanishestate.parsing;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import webcrawling.parsing.HtmlParser;

public class SpanishEstateFetchUrlsHtmlParser implements HtmlParser<URL> {
  public static final String LISTING_CONTAINER_CSS_SELECTOR = "div.box1";
  public static final String AD_URL_SUBSTRING = "hop.clickbank.net";

  @Override
  public List<URL> parse(@NotNull Document document) {
    Elements listingSearchResultsContainers = document.select(LISTING_CONTAINER_CSS_SELECTOR);
    return listingSearchResultsContainers.stream()
        .map((element -> element.selectFirst(A_HTML_TAG).absUrl(HREF_ATTRIBUTE)))
        .filter((string) -> !string.contains(AD_URL_SUBSTRING) && !string.contains("/homes-p/"))
        .map(HtmlParser::deserializeUrl)
        .distinct()
        .collect(Collectors.toList());
  }
}
