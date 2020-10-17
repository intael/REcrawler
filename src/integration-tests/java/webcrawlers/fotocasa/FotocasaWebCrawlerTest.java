package webcrawlers.fotocasa;

import java.net.URL;
import java.util.Locale;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import realestate.RealEstate;
import webcrawling.HtmlParser;
import webcrawling.SiteCollector;
import webcrawling.UrlBuilder;

class FotocasaWebCrawlerTest {

  private FotocasaWebCrawler instance;

  @BeforeEach
  void setUp() {
    SiteCollector siteCollector =
        new FotocasaSiteCollector(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
    HtmlParser<RealEstate> listingHtmlParser = new FotocasaListingHtmlParser();
    HtmlParser<URL> searchResultsPagesHtmlParser =
        new FotocasaFetchUrlsHtmlParser("a.sui-LinkBasic.sui-PaginationBasic-link");
    HtmlParser<URL> listingsPagesHtmlParser = new FotocasaFetchUrlsHtmlParser("a.re-Card-link");
    UrlBuilder urlBuilder =
        new SearchRealEstateUrlBuilder.Builder("sant-boi-de-llobregat", new Locale("en")).build();
    this.instance =
        new FotocasaWebCrawler(
            siteCollector,
            listingHtmlParser,
            searchResultsPagesHtmlParser,
            listingsPagesHtmlParser,
            urlBuilder);
  }

  @Test
  @EnabledIfEnvironmentVariable(named = "ENABLE_INTEGRATION_TESTS", matches = "true")
  void testCrawlFetchesSearchResultPaginationUrls() {
    this.instance.crawl();
    Assert.assertTrue(this.instance.getSearchResultsPages().size() > 0);
  }
}
