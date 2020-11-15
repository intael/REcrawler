package webcrawlers.fotocasa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.net.URL;
import java.util.Locale;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import realestate.RealEstate;
import webcrawlers.fotocasa.parsing.FotocasaFetchUrlsHtmlParser;
import webcrawlers.fotocasa.parsing.FotocasaListingHtmlParser;
import webcrawlers.fotocasa.repositories.FotocasaRealEstateMySqlRepository;
import webcrawling.RealEstateRepository;
import webcrawling.UrlBuilder;
import webcrawling.parsing.HtmlParser;
import webcrawling.site_collectors.GenericSiteCollector;
import webcrawling.site_collectors.SiteCollector;
import webcrawling.site_collectors.dependency_injection.GenericSiteCollectorModule;

class FotocasaWebCrawlerTest {

  private FotocasaWebCrawler instance;

  @BeforeEach
  void setUp() {
    Injector injector = Guice.createInjector(new GenericSiteCollectorModule());
    SiteCollector siteCollector = injector.getInstance(GenericSiteCollector.class);
    HtmlParser<RealEstate> listingHtmlParser = new FotocasaListingHtmlParser();
    HtmlParser<URL> searchResultsPagesHtmlParser =
        new FotocasaFetchUrlsHtmlParser("a.sui-LinkBasic.sui-PaginationBasic-link");
    HtmlParser<URL> listingsPagesHtmlParser = new FotocasaFetchUrlsHtmlParser("a.re-Card-link");
    UrlBuilder urlBuilder =
        new FotocasaSearchUrlBuilder.Builder("cornella-de-llobregat", new Locale("en")).build();
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
    Assert.assertTrue(this.instance.getCollectedRealEstates().size() > 0);

    RealEstateRepository repo = new FotocasaRealEstateMySqlRepository();
    this.instance.getCollectedRealEstates().forEach(repo::save);
  }
}
