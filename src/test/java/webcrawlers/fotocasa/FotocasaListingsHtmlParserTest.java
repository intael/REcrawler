package webcrawlers.fotocasa;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import realestate.RealEstate;
import util.TestFilesUtils;
import webcrawling.HtmlParser;

class FotocasaListingsHtmlParserTest {
  private static final String LISTINGS_PAGE_SAMPLE_FILENAME =
      "listings-page-sample-2020-10-07.html";
  private Document listingsPageSample;
  private final HtmlParser<RealEstate> instance = new FotocasaListingsHtmlParser();


  @BeforeEach
  void setUp() throws IOException {
    File htmlSampleFile = TestFilesUtils.getHtmlSampleAsFile(LISTINGS_PAGE_SAMPLE_FILENAME);
    this.listingsPageSample = Jsoup.parse(htmlSampleFile, null);
  }

  @Test
  void parseReturnsRealEstateInstanceGivenAFotocasaSearchResultsPage() {
    List<RealEstate> urlStrings = instance.parse(this.listingsPageSample);
    Assert.assertNotNull(urlStrings);
  }
}