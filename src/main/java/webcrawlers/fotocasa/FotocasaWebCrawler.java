package webcrawlers.fotocasa;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import webcrawling.HtmlParser;
import webcrawling.SiteCollector;
import webcrawling.SiteListingsPage;
import webcrawling.UrlBuilder;
import webcrawling.WebCrawler;
import webcrawling.specification.CrawlSpecification;

public class FotocasaWebCrawler extends WebCrawler {
  private final Set<SiteListingsPage> siteListingPages = new HashSet<>();

  public FotocasaWebCrawler(
      SiteCollector siteCollector,
      HtmlParser htmlParser,
      UrlBuilder urlBuilder,
      Locale locale,
      CrawlSpecification crawlSpecification) {
    this.siteCollector = siteCollector;
    this.htmlParser = htmlParser;
    this.urlBuilder = urlBuilder;
    this.locale = locale;
    this.crawlSpecification = crawlSpecification;
  }

  @Override
  public void crawl() {}

  private void collectListingPages() {}
}
