package webcrawlers.fotocasa;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestFilesUtils;
import webcrawling.HtmlParser;

class FotocasaHtmlParsersTest {

  private static final String LISTINGS_PAGE_SAMPLE_FILENAME =
      "listings-page-sample-2020-10-07.html";
  private Document listingsPageSample;
  private static final String FOTOCASA_PAGINATION_SEARCH_RESULTS_CSS_CLASS_NAME =
      "a.sui-LinkBasic.sui-PaginationBasic-link";
  private static final String FOTOCASA_LISTINGS_SEARCH_RESULTS_CSS_CLASS_NAME = "a.re-Card-link";

  @BeforeEach
  void setUp() throws IOException {
    File htmlSampleFile = TestFilesUtils.getHtmlSampleAsFile(LISTINGS_PAGE_SAMPLE_FILENAME);
    this.listingsPageSample = Jsoup.parse(htmlSampleFile, null);
  }

  @Test
  void searchPaginationParserReturnsPaginationUrlsGivenAFotocasaSearchResultsPage() {
    HtmlParser<URL> searchPaginationParser =
        new FotocasaFetchUrlsHtmlParser(FOTOCASA_PAGINATION_SEARCH_RESULTS_CSS_CLASS_NAME);
    List<URL> urlStrings = searchPaginationParser.parse(this.listingsPageSample);
    Assert.assertNotNull(urlStrings);
    long expectedPages = 4L;
    Assert.assertEquals(
        expectedPages,
        urlStrings.stream()
            .peek(Assert::assertNotNull)
            .map(
                url -> {
                  String path = url.getPath();
                  return Integer.parseInt(path.substring(path.length() - 1));
                })
            .count());
  }

  @Test
  void searchListingsParserReturnsPaginationUrlsGivenAFotocasaSearchResultsPage() {
    HtmlParser<URL> searchListingsParser =
        new FotocasaFetchUrlsHtmlParser(FOTOCASA_LISTINGS_SEARCH_RESULTS_CSS_CLASS_NAME);
    List<URL> urlStrings = searchListingsParser.parse(this.listingsPageSample);
    Assert.assertNotNull(urlStrings);
    Assert.assertTrue(urlStrings.size() > 0);
    urlStrings.forEach(Assert::assertNotNull);
  }
}
