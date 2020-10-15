package webcrawlers.fotocasa;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import webcrawling.HtmlParser;

public class FotocasaFetchUrlsHtmlParser implements HtmlParser<URL> {
  private final String cssClassName;

  public FotocasaFetchUrlsHtmlParser(@NotNull String cssClassName) {
    this.cssClassName = cssClassName;
  }

  @Override
  public List<URL> parse(@NotNull Document document) {
    Elements linkTags = document.select(cssClassName);
    return linkTags.stream()
        .map((element -> element.attr(HREF_ATTRIBUTE)))
        .map(HtmlParser::deserializeUrl)
        .distinct()
        .collect(Collectors.toList());
  }
}
