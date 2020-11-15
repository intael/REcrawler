package webcrawlers.spanishestate;

import com.google.inject.Guice;
import webcrawlers.spanishestate.parsing.SpanishEstateFetchUrlsHtmlParser;
import webcrawlers.spanishestate.parsing.SpanishEstateListingHtmlParser;
import webcrawling.UrlBuilder;
import webcrawling.WebCrawler;
import webcrawling.site_collectors.GenericSiteCollector;
import webcrawling.site_collectors.SiteCollector;
import webcrawling.site_collectors.dependency_injection.GenericSiteCollectorModule;

public class SpanishEstateWebCrawlerFactory {
  private final UrlBuilder urlBuilder;

  public SpanishEstateWebCrawlerFactory(UrlBuilder urlBuilder) {
    this.urlBuilder = urlBuilder;
  }

  public WebCrawler build() {
    SiteCollector siteCollector =
        Guice.createInjector(new GenericSiteCollectorModule())
            .getInstance(GenericSiteCollector.class);
    return new SpanishEstateWebCrawler(
        siteCollector,
        new SpanishEstateListingHtmlParser(),
        new SpanishEstateFetchUrlsHtmlParser(),
        urlBuilder);
  }
}
