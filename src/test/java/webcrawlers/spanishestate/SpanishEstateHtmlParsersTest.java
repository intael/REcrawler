package webcrawlers.spanishestate;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestFilesUtils;
import webcrawling.HtmlParser;

public class SpanishEstateHtmlParsersTest {
  public static final String DOMAIN = "spanishestate";
  private static final String LISTINGS_PAGE_SAMPLE_FILENAME =
      "search-results-sample-2020-10-27.html";
  private static final List<String> HOUSE_PAGE_SAMPLES_FILENAMES = List.of();
  private Document listingsSearchResultPageSample;
  private final List<Document> housePageSamples = new ArrayList<>();

  @BeforeEach
  void setUp() throws IOException {
    this.listingsSearchResultPageSample =
        TestFilesUtils.readAndParseHtmlFile(DOMAIN, LISTINGS_PAGE_SAMPLE_FILENAME);
    for (String sample : HOUSE_PAGE_SAMPLES_FILENAMES) {
      housePageSamples.add(TestFilesUtils.readAndParseHtmlFile(DOMAIN, sample));
    }
  }

  @Test
  void searchPaginationParserReturnsPaginationUrlsGivenASpanishEstateaSearchResultsPage() {
    HtmlParser<URL> searchPaginationParser = new SpanishEstateFetchUrlsHtmlParser();
    List<URL> urlStrings = searchPaginationParser.parse(this.listingsSearchResultPageSample);
    long expectedPages = 12L;
    Assert.assertEquals(expectedPages, urlStrings.size());
  }
}
