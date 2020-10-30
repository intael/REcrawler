package webcrawlers.fotocasa;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import realestate.RealEstate;
import util.TestFilesUtils;
import webcrawlers.fotocasa.entities.FotocasaHome;
import webcrawling.HtmlParser;

class FotocasaHtmlParsersTest {
  public static final String DOMAIN = "fotocasa";
  private static final String SEARCH_RESULTS_SAMPLE_FILENAME =
      "listings-page-sample-2020-10-07.html";
  private static final List<String> HOUSE_PAGE_SAMPLES_FILENAMES =
      List.of(
          "house-page-sample-2020-10-12.html",
          "house-page-sample-2020-10-15.html",
          "house-page-sample-2020-10-15-bis.html");
  private Document listingsSearchResultPageSample;
  private final List<Document> housePageSamples = new ArrayList<>();
  private static final String FOTOCASA_PAGINATION_SEARCH_RESULTS_CSS_CLASS_NAME =
      "a.sui-LinkBasic.sui-PaginationBasic-link";
  private static final String FOTOCASA_LISTINGS_SEARCH_RESULTS_CSS_CLASS_NAME = "a.re-Card-link";

  @BeforeEach
  void setUp() throws IOException {
    this.listingsSearchResultPageSample =
        TestFilesUtils.readAndParseHtmlFile(DOMAIN, SEARCH_RESULTS_SAMPLE_FILENAME);
    for (String sample : HOUSE_PAGE_SAMPLES_FILENAMES) {
      housePageSamples.add(TestFilesUtils.readAndParseHtmlFile(DOMAIN, sample));
    }
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
    for (Document sample : housePageSamples) {
      List<RealEstate> realEstates = searchListingsParser.parse(sample);
      Assert.assertEquals(1, realEstates.size());
      FotocasaHome parsedHouse = (FotocasaHome) realEstates.get(0);
      Assert.assertNotNull(parsedHouse);
    }
  }
}
