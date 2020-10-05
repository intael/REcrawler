package webcrawling;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import realestate.RealEstate;

public interface RealEstateHtmlParser {

  List<RealEstate> parse(@NotNull Document document);
}
