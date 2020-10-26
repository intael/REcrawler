package webcrawlers.fotocasa;

import java.net.URL;
import java.util.Locale;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import realestate.RealEstate;
import webcrawlers.fotocasa.repositories.FotocasaRealEstateMySqlRepository;
import webcrawling.HtmlParser;
import webcrawling.RealEstateRepository;
import webcrawling.UrlBuilder;
import webcrawling.repositories.RemoteRestProxyRepository;
import webcrawling.repositories.ShortListUserAgentRepository;
import webcrawling.site_collectors.GenericSiteCollector;

class FotocasaWebCrawlerTest {

  private FotocasaWebCrawler instance;

  @BeforeEach
  void setUp() {
    GenericSiteCollector siteCollector =
        new GenericSiteCollector(
            new ShortListUserAgentRepository(),
            new RemoteRestProxyRepository(
                "https://api.proxyscrape.com/?request=getproxies&proxytype=http&timeout=750&country=all&ssl=all&anonymity=all"));
    HtmlParser<RealEstate> listingHtmlParser = new FotocasaListingHtmlParser();
    HtmlParser<URL> searchResultsPagesHtmlParser =
        new FotocasaFetchUrlsHtmlParser("a.sui-LinkBasic.sui-PaginationBasic-link");
    HtmlParser<URL> listingsPagesHtmlParser = new FotocasaFetchUrlsHtmlParser("a.re-Card-link");
    UrlBuilder urlBuilder =
        new SearchRealEstateUrlBuilder.Builder("cornella-de-llobregat", new Locale("en")).build();
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
    Assert.assertTrue(this.instance.getListingPageUrls().size() > 0);
    Assert.assertTrue(this.instance.getCollectedHomes().size() > 0);

    RealEstateRepository repo = new FotocasaRealEstateMySqlRepository();
    this.instance.getCollectedHomes().forEach(repo::save);
  }
}
