package webcrawling.spanishestate;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realestate.RealEstate;
import webcrawling.UrlBuilder;
import webcrawling.WebCrawler;
import webcrawling.parsing.HtmlParser;
import webcrawling.site_collectors.SiteCollector;
import webcrawling.utils.FetchDocumentCallable;

public class SpanishEstateWebCrawler implements WebCrawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpanishEstateWebCrawler.class);
    private final Set<URL> searchResultsPages = new HashSet<>();
    private final Set<RealEstate> collectedHomes = ConcurrentHashMap.newKeySet();
    private final HtmlParser<URL> searchResultsPageParser;
    private final SiteCollector siteCollector;
    private final HtmlParser<RealEstate> listingHtmlParser;
    private final UrlBuilder urlBuilder;

    public Set<RealEstate> getCollectedRealEstates() {
        return new HashSet<>(collectedHomes);
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
    }

    @Override
    public void crawl() {
        List<URL> listingUrls;
        var page = 1;
        LOGGER.info("Starting to collect search result pages.");
        do {
            String newSearchResultsPageUrl = urlBuilder.buildUrl(page);
            LOGGER.info("----------------");
            LOGGER.info("Search Results page: " + page);
            listingUrls = fetchSearchResultsPage(newSearchResultsPageUrl)
                    .map(searchResultsPageParser::parse).orElseGet(List::of);
            searchResultsPages.addAll(listingUrls);
            LOGGER.info("# Home pages collected: " + searchResultsPages.size());
            page++;
        } while (!listingUrls.isEmpty());
        LOGGER.info("Home page URLs collected successfully! Collecting home pages now!");
        collectPagesConcurrently(searchResultsPages, this::getHome);
    }

    private Optional<Document> fetchSearchResultsPage(String searchUrl) {
        try {
            Document currentSearchResultsPage;
            LOGGER.info("Attempting to fetch search result page...");
            currentSearchResultsPage =
                    siteCollector
                            .collect(searchUrl)
                            .orElseThrow(() -> new IOException("Initial Search Results Page failed"));
            return Optional.of(currentSearchResultsPage);
        } catch (IOException ex) {
            LOGGER.error("Initial search results request failed.");
        }
        return Optional.empty();
    }

    private void collectPagesConcurrently(Set<URL> pages, Consumer<Document> htmlActionCallback) {
        ExecutorService executorService = Executors.newWorkStealingPool(20);
        List<Future<Optional<Document>>> futures;
        List<Callable<Optional<Document>>> callables =
                pages.stream()
                        .map((url) -> new FetchDocumentCallable(url.toString(), siteCollector))
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
        this.collectedHomes.addAll(listingHtmlParser.parse(resultsPage));
        LOGGER.info("Saved: " + resultsPage.title());
    }
}
