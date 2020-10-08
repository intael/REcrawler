package webcrawling;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;

public interface HtmlParser<T> {

  List<T> parse(@NotNull Document document);
}
