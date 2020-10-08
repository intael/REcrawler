package webcrawlers.fotocasa;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestFilesUtils;
import webcrawling.HtmlParser;

class FotocasaPaginationHtmlParserTest {

  private static final String LISTINGS_PAGE_SAMPLE_FILENAME =
      "listings-page-sample-2020-10-07.html";
  private Document listingsPageSample;
  private final HtmlParser<String> instance = new FotocasaPaginationHtmlParser();

  private URL deserializeUrl(String urlString) {
    try {
      return new URL(urlString);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @BeforeEach
  void setUp() throws IOException {
    File htmlSampleFile = TestFilesUtils.getHtmlSampleAsFile(LISTINGS_PAGE_SAMPLE_FILENAME);
    this.listingsPageSample = Jsoup.parse(htmlSampleFile, null);
  }

  @Test
  void parseReturnsPaginationUrlsGivenAFotocasaSearchResultsPage() throws MalformedURLException {
    List<String> urlStrings = instance.parse(this.listingsPageSample);
    Assert.assertNotNull(urlStrings);
    List<URL> urls =
        urlStrings.stream()
            .map(this::deserializeUrl)
            .peek(Assert::assertNotNull)
            .collect(Collectors.toList());
  }
}
