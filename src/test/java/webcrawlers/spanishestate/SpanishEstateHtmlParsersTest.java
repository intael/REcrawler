package webcrawlers.spanishestate;

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
import webcrawling.parsing.HtmlParser;
import webcrawling.spanishestate.entities.SpanishEstateHome;
import webcrawling.spanishestate.parsing.SpanishEstateFetchUrlsHtmlParser;
import webcrawling.spanishestate.parsing.SpanishEstateListingHtmlParser;

public class SpanishEstateHtmlParsersTest {
  public static final String DOMAIN = "spanishestate";
  private static final String SEARCH_RESULTS_SAMPLE_FILENAME =
      "search-results-sample-2020-10-27.html";
  private static final String EMPTY_SEARCH_RESULTS_SAMPLE_FILENAME =
      "empty-search-results-sample-2020-10-30.html";
  private static final List<String> HOUSE_PAGE_SAMPLES_FILENAMES =
      List.of("home-sample-2020-10-29.html");
  private Document listingsSearchResultPageSample;
  private Document emptyListingsSearchResultPageSample;
  private final List<Document> housePageSamples = new ArrayList<>();

  @BeforeEach
  void setUp() throws IOException {
    this.listingsSearchResultPageSample =
        TestFilesUtils.readAndParseHtmlFile(DOMAIN, SEARCH_RESULTS_SAMPLE_FILENAME);
    this.emptyListingsSearchResultPageSample =
        TestFilesUtils.readAndParseHtmlFile(DOMAIN, EMPTY_SEARCH_RESULTS_SAMPLE_FILENAME);
    for (String sample : HOUSE_PAGE_SAMPLES_FILENAMES) {
      housePageSamples.add(TestFilesUtils.readAndParseHtmlFile(DOMAIN, sample));
    }
  }

  @Test
  void searchPaginationParserReturnsPaginationUrlsGivenASpanishEstateaSearchResultsPage() {
    HtmlParser<URL> searchPaginationParser = new SpanishEstateFetchUrlsHtmlParser();
    List<URL> urlStrings = searchPaginationParser.parse(this.listingsSearchResultPageSample);
    Assert.assertEquals(12L, urlStrings.size());
  }

  @Test
  void searchPaginationParserReturnsNoUrlGivenAnEmptySpanishEstateaSearchResultsPage() {
    HtmlParser<URL> searchPaginationParser = new SpanishEstateFetchUrlsHtmlParser();
    List<URL> urlStrings = searchPaginationParser.parse(this.emptyListingsSearchResultPageSample);
    Assert.assertEquals(0, urlStrings.size());
  }

  @Test
  void realEstateParserReturnsRealEstateGivenASpanishEstateHousePage() {
    HtmlParser<RealEstate> searchListingsParser = new SpanishEstateListingHtmlParser();
    for (Document sample : housePageSamples) {
      List<RealEstate> realEstates = searchListingsParser.parse(sample);
      Assert.assertEquals(1, realEstates.size());
      SpanishEstateHome parsedHouse = (SpanishEstateHome) realEstates.get(0);
      Assert.assertNotNull(parsedHouse);
    }
  }
}
