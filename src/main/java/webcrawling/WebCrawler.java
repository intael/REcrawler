package webcrawling;

import java.util.Locale;
import java.util.Set;
import realestate.RealEstate;
import webcrawling.specification.CrawlSpecification;

public abstract class WebCrawler {

  protected SiteCollector siteCollector;
  protected RealEstateHtmlParser listingHtmlParser;
  protected ListingsSearchResultsPageHtmlParser listingPageHtmlParser;
  protected UrlBuilder urlBuilder;
  protected CrawlSpecification crawlSpecification;

  protected Set<RealEstate> realEstateListings;

  public abstract void crawl();
}
