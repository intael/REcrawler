package webcrawlers.fotocasa;

import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import webcrawling.HtmlParser;

public class FotocasaPaginationHtmlParser implements HtmlParser<String> {

  private static final String LISTING_PAGE_HTML_ATTRIBUTE =
      "a.sui-LinkBasic.sui-PaginationBasic-link";
  private static final String HREF_ATTRIBUTE = "href";

  @Override
  public List<String> parse(@NotNull Document document) {
    Elements linkTags = document.select(LISTING_PAGE_HTML_ATTRIBUTE);
    return linkTags.stream()
        .map((element -> element.attr(HREF_ATTRIBUTE)))
        .distinct()
        .collect(Collectors.toList());
  }
}
