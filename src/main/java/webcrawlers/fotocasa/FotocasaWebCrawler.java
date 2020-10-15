package webcrawlers.fotocasa;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import realestate.RealEstate;
import webcrawling.HtmlParser;
import webcrawling.SiteCollector;
import webcrawling.SiteListingsPage;
import webcrawling.UrlBuilder;
import webcrawling.WebCrawler;
import webcrawling.specification.CrawlSpecification;

public class FotocasaWebCrawler extends WebCrawler {
  private final Set<SiteListingsPage> siteListingPages = new HashSet<>();

  private final HtmlParser<URL> urlHtmlParser;

  public FotocasaWebCrawler(
      @NotNull SiteCollector siteCollector,
      @NotNull HtmlParser<RealEstate> listingHtmlParser,
      @NotNull HtmlParser<URL> urlHtmlParser,
      @NotNull UrlBuilder urlBuilder,
      @NotNull CrawlSpecification crawlSpecification) {
    this.siteCollector = siteCollector;
    this.listingHtmlParser = listingHtmlParser;
    this.urlHtmlParser = urlHtmlParser;
    this.urlBuilder = urlBuilder;
    this.crawlSpecification = crawlSpecification;
  }

  @Override
  public void crawl() {}

  private void collectListingPages() {}
}
