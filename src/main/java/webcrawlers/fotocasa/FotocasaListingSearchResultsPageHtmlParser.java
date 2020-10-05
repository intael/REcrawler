package webcrawlers.fotocasa;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import webcrawling.ListingsSearchResultsPageHtmlParser;

public class FotocasaListingSearchResultsPageHtmlParser implements ListingsSearchResultsPageHtmlParser {
  public List<String> parse(@NotNull Document document){
    return List.of("");
  }
}
