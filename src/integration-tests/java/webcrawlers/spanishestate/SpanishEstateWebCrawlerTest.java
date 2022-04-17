package webcrawlers.spanishestate;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.net.URL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import realestate.RealEstate;
import webcrawling.RealEstateRepository;
import webcrawling.UrlBuilder;
import webcrawling.parsing.HtmlParser;
import webcrawling.site_collectors.PlaywrightSiteCollector;
import webcrawling.site_collectors.SiteCollector;
import webcrawling.site_collectors.dependency_injection.PlaywrightSiteCollectorModule;
import webcrawling.spanishestate.SpanishEstateSearchUrlBuilder;
import webcrawling.spanishestate.SpanishEstateWebCrawler;
import webcrawling.spanishestate.parsing.SpanishEstateFetchUrlsHtmlParser;
import webcrawling.spanishestate.parsing.SpanishEstateListingHtmlParser;
import webcrawling.spanishestate.repositories.SpanishEstateHomeMySqlRepository;

class SpanishEstateWebCrawlerTest {

  private SpanishEstateWebCrawler instance;

  @BeforeEach
  void setUp() {
    Injector injector = Guice.createInjector(new PlaywrightSiteCollectorModule());
    SiteCollector siteCollector = injector.getInstance(PlaywrightSiteCollector.class);
    HtmlParser<RealEstate> listingHtmlParser = new SpanishEstateListingHtmlParser();
    HtmlParser<URL> searchResultsPagesHtmlParser = new SpanishEstateFetchUrlsHtmlParser();
    UrlBuilder urlBuilder =
        new SpanishEstateSearchUrlBuilder.Builder(
                "province-lleida") // region-baix-llobregat region-barcelones
            // "municipality-barcelona" //
            // "municipality-sant-boi-de-llobregat"
            .build();
    this.instance =
        new SpanishEstateWebCrawler(
            siteCollector, listingHtmlParser, searchResultsPagesHtmlParser, urlBuilder);
  }

  @Test
  @EnabledIfEnvironmentVariable(named = "ENABLE_INTEGRATION_TESTS", matches = "true")
  void crawl() {
    this.instance.crawl();
    System.out.println("Collected Home URLs: " + instance.getSearchResultsPages().size());
    System.out.println("Collected homes: " + instance.getCollectedRealEstates().size());
    Assertions.assertTrue(this.instance.getCollectedRealEstates().size() > 0);
    RealEstateRepository repository = new SpanishEstateHomeMySqlRepository();
    this.instance.getCollectedRealEstates().forEach(repository::save);
  }
}
