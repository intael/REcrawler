package webcrawling;

import java.util.Locale;
import java.util.Set;
import realestate.RealEstate;
import webcrawling.specification.CrawlSpecification;

public abstract class WebCrawler {

  protected SiteCollector siteCollector;
  protected HtmlParser htmlParser;
  protected UrlBuilder urlBuilder;
  protected Locale locale;
  protected CrawlSpecification crawlSpecification;

  protected Set<RealEstate> realEstateListings;

  public abstract void crawl();
}
