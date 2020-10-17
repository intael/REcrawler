package webcrawlers.fotocasa;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realestate.RealEstate;
import webcrawling.HtmlParser;
import webcrawling.SiteCollector;
import webcrawling.UrlBuilder;
import webcrawling.WebCrawler;

public class FotocasaWebCrawler extends WebCrawler {
  private static final Logger LOGGER = LoggerFactory.getLogger(FotocasaWebCrawler.class);
  private static final Pattern SEARCH_RESULTS_URL_INDEX_REGEX = Pattern.compile("[0-9]+$");
  private static final int SEARCH_RESULTS_DEFAULT_RETRY_TIMES = 6;
  private final Set<URL> listingsPages = new HashSet<>();
  private final Set<URL> searchResultsPages = new HashSet<>();

  private final HtmlParser<URL> listingsPagesHtmlParser;

  public Set<URL> getSearchResultsPages() {
    return new HashSet<>(searchResultsPages); // defensive copy
  }

  public FotocasaWebCrawler(
      @NotNull SiteCollector siteCollector,
      @NotNull HtmlParser<RealEstate> listingHtmlParser,
      @NotNull HtmlParser<URL> searchResultsPagesHtmlParser,
      @NotNull HtmlParser<URL> listingsPagesHtmlParser,
      @NotNull UrlBuilder urlBuilder) {
    this.siteCollector = siteCollector;
    this.listingHtmlParser = listingHtmlParser;
    this.searchResultsPagesHtmlParser = searchResultsPagesHtmlParser;
    this.listingsPagesHtmlParser = listingsPagesHtmlParser;
    this.urlBuilder = urlBuilder;
  }

  @Override
  public void crawl() {
    String searchUrl = urlBuilder.buildUrl();
    Optional<Document> initialSearchResult =
        fetchDocument(searchUrl, SEARCH_RESULTS_DEFAULT_RETRY_TIMES);
    if (initialSearchResult.isEmpty()) {
      LOGGER.error("Failed to even fetch the initial search results page.");
      return;
    }
    Document initialSearchResultsPage = initialSearchResult.get();
    try {
      collectSearchResultsPages(initialSearchResultsPage, 1);
    } catch (PaginationPageFetchFailure pageFetchFailure) {
      LOGGER.error(pageFetchFailure.getMessage());
    }
  }

  private Optional<Document> fetchDocument(String url, int retries) {
    try {
      return siteCollector.collect(url, retries);
    } catch (IOException ioe) {
      LOGGER.error("Failed to fetch document. Exception: " + ioe.toString());
    }
    return Optional.empty();
  }

  private IndexedUrl getPageWithHighestIndex(List<URL> searchResultPages) {
    return searchResultPages.stream()
        .map(this::transformUrlIntoIndexedUrl)
        .max(IndexedUrl::compareTo)
        .orElse(new IndexedUrl(null, 1));
  }

  private IndexedUrl transformUrlIntoIndexedUrl(URL url) {
    String urlPath = url.getPath();
    Matcher matcher = SEARCH_RESULTS_URL_INDEX_REGEX.matcher(urlPath);
    if (!matcher.find()) {
      LOGGER.debug(
          "Found a page without index. It's most likely the first Results Page, so we return a dummy IndexedUrl.");
      return new IndexedUrl(url, 1);
    }
    String indexString =
        matcher.group(0); // the first match is the only one since we're forcing it to be at the end
    int index = Integer.parseInt(indexString);
    return new IndexedUrl(url, index);
  }

  // TODO: Should we maybe turn this into a separate component to be injected here?
  private void collectSearchResultsPages(Document resultsPage, int globalMaxResultPagesIndex)
      throws PaginationPageFetchFailure {
    List<URL> pages = searchResultsPagesHtmlParser.parse(resultsPage);
    if (pages.size() == 0) {
      LOGGER.info("No additional pages were found.");
      return;
    }
    searchResultsPages.addAll(pages);
    IndexedUrl highestIndexPage = getPageWithHighestIndex(pages);
    if (highestIndexPage.getIndex() <= globalMaxResultPagesIndex) { // base case
      LOGGER.info(
          String.format("End of pagination reached at page %s.", globalMaxResultPagesIndex));
    } else { // recursive case
      LOGGER.info("Collected up to page: " + highestIndexPage.getIndex());
      LOGGER.debug("Total pages collected: " + searchResultsPages.size());
      LOGGER.info("Highest indexed url: " + highestIndexPage.getUrl());
      Document page =
          fetchDocument(highestIndexPage.getUrl().toString(), SEARCH_RESULTS_DEFAULT_RETRY_TIMES)
              .orElseThrow(
                  () ->
                      new PaginationPageFetchFailure(
                          String.format(
                              "Failed to fetch search results page %s. Aborting collection of search results page urls.",
                              highestIndexPage.getIndex())));
      collectSearchResultsPages(page, highestIndexPage.getIndex());
    }
  }
}
