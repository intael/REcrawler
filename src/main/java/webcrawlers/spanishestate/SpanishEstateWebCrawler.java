package webcrawlers.spanishestate;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realestate.RealEstate;
import webcrawling.UrlBuilder;
import webcrawling.WebCrawler;
import webcrawling.parsing.HtmlParser;
import webcrawling.site_collectors.SiteCollector;
import webcrawling.utils.FetchDocumentCallable;
import webcrawling.utils.FileUtil;

public class SpanishEstateWebCrawler implements WebCrawler {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpanishEstateWebCrawler.class);
  private static final int PROBING_REQUEST_MAX_RETRIES = 5;
  private static final int SEARCH_RESULTS_PAGE_MAX_COOKIE_ROTATION_RETRIES = 6;
  private static final int SEARCH_RESULTS_DEFAULT_MAX_RETRIES = 9;
  private static final String COOKIE_HEADER = "Cookie";
  private static final String COOKIE_PREFIX = "CookiePrefix";
  private static final String REFERER_HEADER = "Referer";
  private final Set<URL> searchResultsPages = new HashSet<>();
  private final Set<RealEstate> collectedHomes = ConcurrentHashMap.newKeySet();
  private final Map<URL, String> listingUrlToSearchUrl = new HashMap<>();
  private static final int MAX_PROBING_RETRIES = 30;

  // Headers
  private Map<String, String> defaultHeaders;
  private static final Path CRAWLER_CONFIG_DIRECTORY_PATH = Path.of("webcrawlers", "spanishestate");
  private static final Path GLOBAL_DEFAULT_HEADERS_PROPERTIES_PATH =
      Path.of(CRAWLER_CONFIG_DIRECTORY_PATH.toString(), "GlobalDefaultHeaders.properties");

  // Listing Page Headers
  private Map<String, String> listingPageHeaders;
  private static final Path LISTING_PAGE_DEFAULT_HEADERS_PROPERTIES_PATH =
      Path.of(CRAWLER_CONFIG_DIRECTORY_PATH.toString(), "ListingPageDefaultHeaders.properties");

  // Search Results Headers
  private Map<String, String> searchResultsHeaders;
  private static final Path SEARCH_RESULTS_HEADERS_PROPERTIES_PATH =
      Path.of(
          CRAWLER_CONFIG_DIRECTORY_PATH.toString(), "SearchResultsPageDefaultHeaders.properties");

  // Cookies
  private final Map<String, String> defaultCookies = new HashMap<>();
  private static final String CFTOKEN_COOKIE = "cftoken";
  private static final String CFID_COOKIE = "cfid";

  private final HtmlParser<URL> searchResultsPageParser;
  private final SiteCollector siteCollector;
  private final HtmlParser<RealEstate> listingHtmlParser;
  private final UrlBuilder urlBuilder;

  public Set<URL> getSearchResultsPages() {
    return new HashSet<>(searchResultsPages); // defensive copy
  }

  public Set<RealEstate> getCollectedRealEstates() {
    return new HashSet<>(collectedHomes); // defensive copy
  }

  public SpanishEstateWebCrawler(
      @NotNull SiteCollector siteCollector,
      @NotNull HtmlParser<RealEstate> listingHtmlParser,
      @NotNull HtmlParser<URL> searchResultsPageHtmlParser,
      @NotNull UrlBuilder urlBuilder) {
    this.siteCollector = siteCollector;
    this.listingHtmlParser = listingHtmlParser;
    this.searchResultsPageParser = searchResultsPageHtmlParser;
    this.urlBuilder = urlBuilder;
    try {
      this.defaultHeaders =
          new FileUtil().readPropertiesFile(GLOBAL_DEFAULT_HEADERS_PROPERTIES_PATH.toString());
      this.searchResultsHeaders =
          new FileUtil().readPropertiesFile(SEARCH_RESULTS_HEADERS_PROPERTIES_PATH.toString());
      this.listingPageHeaders =
          new FileUtil()
              .readPropertiesFile(LISTING_PAGE_DEFAULT_HEADERS_PROPERTIES_PATH.toString());

    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  @Override
  public void crawl() {
    reloadCookies(defaultCookies, defaultHeaders);
    String searchUrl = urlBuilder.buildUrl();
    Document currentSearchResultsPage;
    currentSearchResultsPage = fetchSearchResultsPage(searchUrl, defaultCookies, defaultHeaders);
    if (currentSearchResultsPage == null) {
      return;
    }
    List<URL> searchResultPages =
        new ArrayList<>(searchResultsPageParser.parse(currentSearchResultsPage));
    searchResultsPages.addAll(searchResultPages);
    int page = 2;
    LOGGER.info("All good, starting to collect search result pages.");
    while (page <= 5 && !searchResultPages.isEmpty()) {
      String newSearchResultsPageUrl = urlBuilder.buildUrl(page);
      LOGGER.info("----------------");
      LOGGER.info("Search Results page: " + page);
      currentSearchResultsPage =
          fetchSearchResultsPage(newSearchResultsPageUrl, defaultCookies, defaultHeaders);
      if (currentSearchResultsPage == null) {
        break; // giving up on collecting search result listing pages
      }
      defaultHeaders.put(REFERER_HEADER, newSearchResultsPageUrl);
      searchResultPages = searchResultsPageParser.parse(currentSearchResultsPage);
      searchResultsPages.addAll(searchResultPages);
      searchResultPages.forEach(
          (listingUrl) -> listingUrlToSearchUrl.put(listingUrl, newSearchResultsPageUrl));
      LOGGER.info("# Home pages collected: " + searchResultsPages.size());
      page++;
    }
    LOGGER.info("Home page URLs collected successfully! Collecting home pages now!");
    collectPagesConcurrently(searchResultsPages, this::getHome);
  }

  @Nullable
  private Document fetchSearchResultsPage(
      String searchUrl, Map<String, String> cookies, Map<String, String> headers) {
    int cookieRotationAttempts = 0;
    while (cookieRotationAttempts < SEARCH_RESULTS_PAGE_MAX_COOKIE_ROTATION_RETRIES) {
      try {
        Document currentSearchResultsPage;
        LOGGER.info("Attempting to fetch search result page...");
        currentSearchResultsPage =
            siteCollector
                .collect(searchUrl, SEARCH_RESULTS_DEFAULT_MAX_RETRIES, headers, cookies)
                .orElseThrow(() -> new IOException("Initial Search Results Page failed"));
        return currentSearchResultsPage;
      } catch (RuntimeException | IOException ex) {
        LOGGER.error("Initial search results request failed. Reloading cookies and retrying...");
        reloadCookies(cookies, headers);
        cookieRotationAttempts++;
      }
    }
    System.exit(1);
    LOGGER.error("Initial search results failed. Exiting.");
    return null;
  }

  private Optional<Connection> runProbingRequest() {
    int globalAttempts = 0;
    while (globalAttempts < MAX_PROBING_RETRIES) {
      try {
        LOGGER.info("Attempting probing request...");
        return Optional.of(
            siteCollector
                .probingRequest(
                    searchResultsHeaders.get(REFERER_HEADER),
                    PROBING_REQUEST_MAX_RETRIES,
                    this.defaultHeaders,
                    new HashMap<>())
                .orElseThrow(() -> new RuntimeException("Failed the probing request.")));
      } catch (RuntimeException | IOException ex) {
        LOGGER.error("Probing request failed. Retrying...");
        globalAttempts++;
      }
    }
    LOGGER.info("Probing request attempts failed. Giving up :(");
    return Optional.empty();
  }

  private void reloadCookies(Map<String, String> cookies, Map<String, String> headers) {
    Connection probingRequestConnection =
        runProbingRequest()
            .orElseThrow(
                () -> new RuntimeException("Probing request failed, cookies were not reloaded!"));
    Map<String, String> probingRequestCookies = getProbingRequestCookies(probingRequestConnection);
    probingRequestCookies.forEach(cookies::put); // reload cookies
    setSearchResultHeaders(
        headers, buildCookieHeader(probingRequestCookies)); // reload header cookies
  }

  private Map<String, String> getProbingRequestCookies(Connection jsoupConnection) {
    return jsoupConnection.response().cookies();
  }

  private String buildCookieHeader(Map<String, String> cookies) {
    return String.format(
        "%s=%s; %s=%s",
        CFTOKEN_COOKIE, cookies.get(CFTOKEN_COOKIE), CFID_COOKIE, cookies.get(CFID_COOKIE));
  }

  private void setSearchResultHeaders(Map<String, String> headers, String cookieHeaderValue) {
    headers.compute(
        COOKIE_HEADER, (key, value) -> searchResultsHeaders.get(COOKIE_PREFIX) + cookieHeaderValue);
    searchResultsHeaders.entrySet().stream()
        .filter((entry) -> !entry.getKey().equals(COOKIE_PREFIX))
        .forEach((entry) -> headers.put(entry.getKey(), entry.getValue()));
  }

  private void collectPagesConcurrently(Set<URL> pages, Consumer<Document> htmlActionCallback) {
    ExecutorService executorService = Executors.newWorkStealingPool();
    List<Future<Optional<Document>>> futures;
    List<Callable<Optional<Document>>> callables =
        pages.stream()
            .map(
                (url) -> {
                  Map<String, String> customHeader = new HashMap<>(defaultHeaders);
                  customHeader.put(REFERER_HEADER, listingUrlToSearchUrl.get(url));
                  customHeader.putAll(listingPageHeaders);
                  Map<String, String> customCookies = new HashMap<>(defaultCookies);
                  return new FetchDocumentCallable(
                      url.toString(),
                      siteCollector,
                      SEARCH_RESULTS_DEFAULT_MAX_RETRIES,
                      customHeader,
                      customCookies);
                })
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

  private void getHome(Document resultsPage) {
    // TODO: always a list of one element... consider adding a parseOne method to HtmlParser
    List<RealEstate> home = listingHtmlParser.parse(resultsPage);
    this.collectedHomes.addAll(home);
  }
}
