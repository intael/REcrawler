package webcrawling;

import java.net.URL;
import java.util.Set;
import realestate.RealEstate;
import webcrawling.specification.CrawlSpecification;

public abstract class WebCrawler {

  protected SiteCollector siteCollector;
  protected HtmlParser<RealEstate> listingHtmlParser;
  protected HtmlParser<URL> listingPageHtmlParser;
  protected HtmlParser<URL> searchResultsPagesHtmlParser;
  protected UrlBuilder urlBuilder;
  protected CrawlSpecification crawlSpecification;

  protected Set<RealEstate> realEstateListings;

  public abstract void crawl();
}
