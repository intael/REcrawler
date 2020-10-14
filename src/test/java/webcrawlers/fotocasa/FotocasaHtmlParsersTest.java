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
import realestate.RealEstate;
import util.TestFilesUtils;
import webcrawlers.fotocasa.entities.FotocasaHome;
import webcrawling.HtmlParser;

class FotocasaHtmlParsersTest {

  private static final String LISTINGS_PAGE_SAMPLE_FILENAME =
      "listings-page-sample-2020-10-07.html";
  private static final String HOUSE_PAGE_SAMPLE_FILENAME = "house-page-sample-2020-10-12.html";
  private Document listingsSearchResultPageSample;
  private Document housePageSample;
  private static final String FOTOCASA_PAGINATION_SEARCH_RESULTS_CSS_CLASS_NAME =
      "a.sui-LinkBasic.sui-PaginationBasic-link";
  private static final String FOTOCASA_LISTINGS_SEARCH_RESULTS_CSS_CLASS_NAME = "a.re-Card-link";

  @BeforeEach
  void setUp() throws IOException {
    File htmlSampleFile = TestFilesUtils.getHtmlSampleAsFile(LISTINGS_PAGE_SAMPLE_FILENAME);
    this.listingsSearchResultPageSample = Jsoup.parse(htmlSampleFile, null);
    htmlSampleFile = TestFilesUtils.getHtmlSampleAsFile(HOUSE_PAGE_SAMPLE_FILENAME);
    this.housePageSample = Jsoup.parse(htmlSampleFile, null);
  }

  @Test
  void searchPaginationParserReturnsPaginationUrlsGivenAFotocasaSearchResultsPage() {
    HtmlParser<URL> searchPaginationParser =
        new FotocasaFetchUrlsHtmlParser(FOTOCASA_PAGINATION_SEARCH_RESULTS_CSS_CLASS_NAME);
    List<URL> urlStrings = searchPaginationParser.parse(this.listingsSearchResultPageSample);
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
    List<URL> urlStrings = searchListingsParser.parse(this.listingsSearchResultPageSample);
    Assert.assertNotNull(urlStrings);
    Assert.assertTrue(urlStrings.size() > 0);
    urlStrings.forEach(Assert::assertNotNull);
  }

  @Test
  void realEstateParserReturnsRealEstateGivenAFotocasaHousePage() {
    HtmlParser<RealEstate> searchListingsParser = new FotocasaListingHtmlParser();
    List<RealEstate> realEstates = searchListingsParser.parse(housePageSample);
    Assert.assertEquals(1, realEstates.size());
    FotocasaHome parsedHouse = (FotocasaHome) realEstates.get(0);
    Assert.assertNotNull(parsedHouse);
  }
}
