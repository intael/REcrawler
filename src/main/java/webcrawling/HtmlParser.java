package webcrawling;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;

public interface ListingsSearchResultsPageHtmlParser {

  List<String> parse(@NotNull Document document);
}
