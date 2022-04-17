package webcrawling.spanishestate;

import com.google.inject.Guice;
import webcrawling.UrlBuilder;
import webcrawling.WebCrawler;
import webcrawling.site_collectors.PlaywrightSiteCollector;
import webcrawling.site_collectors.SiteCollector;
import webcrawling.site_collectors.dependency_injection.PlaywrightSiteCollectorModule;
import webcrawling.spanishestate.parsing.SpanishEstateFetchUrlsHtmlParser;
import webcrawling.spanishestate.parsing.SpanishEstateListingHtmlParser;

public class SpanishEstateWebCrawlerFactory {
  private final UrlBuilder urlBuilder;

  public SpanishEstateWebCrawlerFactory(UrlBuilder urlBuilder) {
    this.urlBuilder = urlBuilder;
  }

  public WebCrawler build() {
    SiteCollector siteCollector =
        Guice.createInjector(new PlaywrightSiteCollectorModule())
            .getInstance(PlaywrightSiteCollector.class);
    return new SpanishEstateWebCrawler(
        siteCollector,
        new SpanishEstateListingHtmlParser(),
        new SpanishEstateFetchUrlsHtmlParser(),
        urlBuilder);
  }
}
