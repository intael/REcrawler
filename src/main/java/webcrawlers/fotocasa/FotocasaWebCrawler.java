package webcrawlers.fotocasa;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realestate.RealEstate;
import webcrawling.HtmlParser;
import webcrawling.site_collectors.SiteCollector;
import webcrawling.UrlBuilder;
import webcrawling.WebCrawler;
import webcrawling.utils.FetchDocumentCallable;
import webcrawling.utils.IndexedUrl;

public class FotocasaWebCrawler extends WebCrawler {
  private static final Logger LOGGER = LoggerFactory.getLogger(FotocasaWebCrawler.class);
  private static final Pattern SEARCH_RESULTS_URL_INDEX_REGEX = Pattern.compile("[0-9]+$");
  private static final int SEARCH_RESULTS_DEFAULT_RETRY_TIMES = 30;
  private final Set<URL> searchResultsPages = new HashSet<>();
  private final Set<URL> listingPageUrls = ConcurrentHashMap.newKeySet();
  private final Set<RealEstate> collectedHomes = ConcurrentHashMap.newKeySet();

  private final HtmlParser<URL> listingsPagesHtmlParser;

  public Set<URL> getSearchResultsPages() {
    return new HashSet<>(searchResultsPages); // defensive copy
  }

  public Set<URL> getListingPageUrls() {
    return new HashSet<>(listingPageUrls); // defensive copy
  }

  public Set<RealEstate> getCollectedHomes() {
    return new HashSet<>(collectedHomes); // defensive copy
  }

  public FotocasaWebCrawler(
      @NotNull SiteCollector siteCollector,
      @NotNull HtmlParser<RealEstate> listingHtmlParser,
      @NotNull HtmlParser<URL> searchResultsPagesHtmlParser,
      @NotNull HtmlParser<URL> listingPagesHtmlParser,
      @NotNull UrlBuilder urlBuilder) {
    this.siteCollector = siteCollector;
    this.listingHtmlParser = listingHtmlParser;
    this.searchResultsPagesHtmlParser = searchResultsPagesHtmlParser;
    this.listingsPagesHtmlParser = listingPagesHtmlParser;
    this.urlBuilder = urlBuilder;
  }

  @Override
  public void crawl() {
    String searchUrl = urlBuilder.buildUrl();
    Optional<Document> initialSearchResult =
        new FetchDocumentCallable(searchUrl, siteCollector, SEARCH_RESULTS_DEFAULT_RETRY_TIMES)
            .call();
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
    collectPagesConcurrently(searchResultsPages, this::getHomePageUrls);
    LOGGER.info(String.format("Collected %s home pages.", this.listingPageUrls.size()));
    collectPagesConcurrently(listingPageUrls, this::getHome);
    LOGGER.info(String.format("Process finished! Collected %s homes.", this.collectedHomes.size()));
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
    getHomePageUrls(resultsPage);
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
      LOGGER.info("Collected up to search results page: " + highestIndexPage.getIndex());
      LOGGER.debug("Total search results pages collected: " + searchResultsPages.size());
      LOGGER.info("Highest indexed url: " + highestIndexPage.getUrl());
      Document page =
          new FetchDocumentCallable(
                  highestIndexPage.getUrl().toString(),
                  siteCollector,
                  SEARCH_RESULTS_DEFAULT_RETRY_TIMES)
              .call()
              .orElseThrow(
                  () ->
                      new PaginationPageFetchFailure(
                          String.format(
                              "Failed to fetch search results page %s. Aborting collection of search results page urls.",
                              highestIndexPage.getIndex())));
      collectSearchResultsPages(page, highestIndexPage.getIndex());
    }
  }

  private void collectPagesConcurrently(Set<URL> pages, Consumer<Document> htmlActionCallback) {
    ExecutorService executorService = Executors.newFixedThreadPool(20);
    List<Future<Optional<Document>>> futures;
    List<Callable<Optional<Document>>> callables =
        pages.stream()
            .map(
                (url) ->
                    new FetchDocumentCallable(
                        url.toString(), siteCollector, SEARCH_RESULTS_DEFAULT_RETRY_TIMES))
            .collect(Collectors.toList());
    try {
      futures = executorService.invokeAll(callables);
      executorService.shutdown();
      for (Future<Optional<Document>> future : futures) {
        future.get().ifPresent(htmlActionCallback);
      }
    } catch (InterruptedException | ExecutionException interruptedException) {
      LOGGER.error("Failed to collect home page urls: " + interruptedException.getMessage());
    }
  }

  private void getHomePageUrls(Document resultsPage) {
    List<URL> pages = listingsPagesHtmlParser.parse(resultsPage);
    this.listingPageUrls.addAll(pages);
    LOGGER.info("Total home pages collected: " + listingPageUrls.size());
  }

  private void getHome(Document resultsPage) {
    // TODO: always a list of one element... consider adding a parseOne method to HtmlParser
    List<RealEstate> home = listingHtmlParser.parse(resultsPage);
    this.collectedHomes.addAll(home);
  }
}
