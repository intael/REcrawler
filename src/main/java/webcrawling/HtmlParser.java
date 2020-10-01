package webcrawling;

import org.jsoup.nodes.Document;
import realestate.RealEstate;

public interface HtmlParser {

  RealEstate parse(Document document);
}
