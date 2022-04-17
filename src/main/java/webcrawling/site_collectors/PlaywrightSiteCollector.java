package webcrawling.site_collectors;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.microsoft.playwright.*;
import java.io.IOException;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrawling.site_collectors.dependency_injection.LaunchOptionsQualifier;

public class PlaywrightSiteCollector implements SiteCollector {
  private static final Logger LOGGER = LoggerFactory.getLogger(PlaywrightSiteCollector.class);
  private final int maxRetries;
  private final BrowserType.LaunchOptions launchOptions;

  @Inject
  public PlaywrightSiteCollector(
      @LaunchOptionsQualifier BrowserType.LaunchOptions launchOptions,
      @Named("maxRetries") int maxRetries) {
    this.maxRetries = maxRetries;
    this.launchOptions = launchOptions;
  }

  @Override
  public Optional<Document> collect(String url) {
    int retries = 0;
    while (retries <= maxRetries) {
      try (Browser browser = Playwright.create().chromium().launch(launchOptions);
          Page page = browser.newPage()) {
        Response response = page.navigate(url);
        if (response.status() != 200) {
          throw new IOException("Response is not 200.");
        }
        return Optional.of(Jsoup.parse(response.text()));
      } catch (IOException ioe) {
        LOGGER.info("Failed to collect url: " + url + ioe.getMessage() + " Retrying.");
        retries++;
      }
    }
    return Optional.empty();
  }
}
