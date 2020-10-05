package webcrawlers.fotocasa;

import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import webcrawling.ListingsSearchResultsPageHtmlParser;
import webcrawling.RealEstateHtmlParser;
import webcrawling.SiteCollector;
import webcrawling.SiteListingsPage;
import webcrawling.UrlBuilder;
import webcrawling.WebCrawler;
import webcrawling.specification.CrawlSpecification;

public class FotocasaWebCrawler extends WebCrawler {
  private final Set<SiteListingsPage> siteListingPages = new HashSet<>();

  public FotocasaWebCrawler(
      @NotNull SiteCollector siteCollector,
      @NotNull RealEstateHtmlParser listingHtmlParser,
      @NotNull ListingsSearchResultsPageHtmlParser listingPageHtmlParser,
      @NotNull UrlBuilder urlBuilder,
      @NotNull CrawlSpecification crawlSpecification) {
    this.siteCollector = siteCollector;
    this.listingHtmlParser = listingHtmlParser;
    this.listingPageHtmlParser = listingPageHtmlParser;
    this.urlBuilder = urlBuilder;
    this.crawlSpecification = crawlSpecification;
  }

  @Override
  public void crawl() {}

  private void collectListingPages() {}
}
