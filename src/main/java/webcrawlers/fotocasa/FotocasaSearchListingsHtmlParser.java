package webcrawlers.fotocasa;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import webcrawling.HtmlParser;

public class FotocasaSearchListingsHtmlParser implements HtmlParser<URL> {
  private static final String CARD_LINKS_HTML_ELEMENTS =
      "a.re-Card-link";

  @Override
  public List<URL> parse(@NotNull Document document) {
    Elements linkTags = document.select(CARD_LINKS_HTML_ELEMENTS);
    return linkTags.stream()
        .map((element -> element.attr(HREF_ATTRIBUTE)))
        .map(HtmlParser::deserializeUrl)
        .distinct()
        .collect(Collectors.toList());
  }
}
