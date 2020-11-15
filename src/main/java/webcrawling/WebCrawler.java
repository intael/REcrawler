package webcrawling;

import java.util.Set;
import realestate.RealEstate;

public interface WebCrawler {

  void crawl();

  Set<RealEstate> getCollectedRealEstates();
}
