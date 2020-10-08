package webcrawlers.fotocasa;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FotocasaPaginationHtmlParserTest {

  private static final String LISTINGS_PAGE_SAMPLE_FILENAME =
      "listings-page-sample-2020-10-07.html";
  private Document listingsPageSample;
  private final FotocasaPaginationHtmlParser instance = new FotocasaPaginationHtmlParser();

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
    Path projectDirectory = FileSystems.getDefault().getPath(".").normalize().toAbsolutePath();
    Path resourcesDirectory = Paths.get(projectDirectory.toString(), "src", "test", "resources");
    Path htmlSamplesDirectory = Paths.get(resourcesDirectory.toString(), "html-samples");
    File htmlSampleFile =
        Paths.get(htmlSamplesDirectory.toString(), LISTINGS_PAGE_SAMPLE_FILENAME).toFile();
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
