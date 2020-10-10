package webcrawlers.fotocasa;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import webcrawling.HtmlParser;

public class FotocasaSearchPaginationHtmlParser implements HtmlParser<URL> {

  private static final String LISTINGS_PAGE_HTML_ELEMENTS =
      "a.sui-LinkBasic.sui-PaginationBasic-link";

  @Override
  public List<URL> parse(@NotNull Document document) {
    Elements linkTags = document.select(LISTINGS_PAGE_HTML_ELEMENTS);
    return linkTags.stream()
        .map((element -> element.attr(HREF_ATTRIBUTE)))
        .map(HtmlParser::deserializeUrl)
        .distinct()
        .collect(Collectors.toList());
  }
}
