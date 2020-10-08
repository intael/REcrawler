package webcrawlers.fotocasa;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import realestate.RealEstate;
import webcrawling.HtmlParser;

public class FotocasaListingsHtmlParser implements HtmlParser<RealEstate> {
  @Override
  public List<RealEstate> parse(@NotNull Document document) {
    return null;
  }
}
