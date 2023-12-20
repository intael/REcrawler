package webcrawling.site_collectors;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.microsoft.playwright.*;
import java.io.IOException;
import java.util.Optional;

import com.microsoft.playwright.options.LoadState;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrawling.site_collectors.dependency_injection.LaunchOptionsQualifier;

public class PlaywrightSiteCollector implements SiteCollector {
  private static final Logger LOGGER = LoggerFactory.getLogger(PlaywrightSiteCollector.class);
  private final int maxRetries;
  private final Browser browser;

  @Inject
  public PlaywrightSiteCollector(
      @LaunchOptionsQualifier BrowserType.LaunchOptions launchOptions,
      @Named("maxRetries") int maxRetries) {
    this.maxRetries = maxRetries;
    this.browser = Playwright.create().chromium().launch(launchOptions);
  }

  @Override
  synchronized public Optional<Document> collect(String url) {
    var retries = 0;
    while (retries <= maxRetries) {
      try (Page page = browser.newPage()) {
        Response response = page.navigate(url);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        if (response.status() != 200) {
          throw new IOException("Response is not 200.");
        }
        return Optional.of(Jsoup.parse(response.text()));
      } catch (IOException | PlaywrightException ioe) {
        LOGGER.info("Failed to collect url: " + url + ioe.getMessage() + " Retrying.");
        retries++;
      }
    }
    LOGGER.warn("Giving up on retries for " + url);
    return Optional.empty();
  }
}
