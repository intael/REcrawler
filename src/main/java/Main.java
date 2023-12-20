import com.github.bogdanovmn.cmdline.CmdLineAppBuilder;
import java.util.List;
import java.util.Optional;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrawling.RealEstateRepository;
import webcrawling.Site;
import webcrawling.UrlBuilder;
import webcrawling.WebCrawler;
import webcrawling.spanishestate.SpanishEstateSearchUrlBuilder;
import webcrawling.spanishestate.SpanishEstateWebCrawlerFactory;
import webcrawling.spanishestate.repositories.SpanishEstateHomeMySqlRepository;

public class Main {
  private static final String SITE = "site";
  private static final String GEOAREA1 = "geoarea1";
  private static final String GEOAREA1_SHORT_NAME = "g";
  private static final String GEOAREA2 = "geoarea2";
  private static final String GEOAREA2_SHORT_NAME = "g2";
  private static final String GEOAREA3 = "geoarea3";
  private static final String GEOAREA3_SHORT_NAME = "g3";
  private static final String MIN_BATHROOMS = "minBathrooms";
  private static final String MIN_ROOMS = "minRooms";
  private static Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {
    new CmdLineAppBuilder(args)
        .withJarName("REcrawler") // just for a help text (-h option)
        .withDescription("My program does ...")
        .withRequiredArg(
            SITE,
            "Site that should be scraped. Currently supported Sites are: "
                + List.of(Site.values()).toString().toLowerCase())
        .withRequiredArg(
            GEOAREA1,
            GEOAREA1_SHORT_NAME,
            "Geoarea1 we want to scrape Real Estate at. Check the documentation on this one, since the concept might be different for each Site.")
        .withArg(
            GEOAREA2,
            GEOAREA2_SHORT_NAME,
            "Optional. Requires specifying geoarea1. Geoarea2 we want to scrape Real Estate at. Check the documentation on this one, since the concept might be different for each Site.")
        .withDependencies(GEOAREA1_SHORT_NAME)
        .withArg(
            GEOAREA3,
            GEOAREA3_SHORT_NAME,
            "Optional. Requires specifying geoarea1 and geoarea2. Geoarea3 we want to scrape Real Estate at. Check the documentation on this one, since the concept might be different for each Site.")
        .withDependencies(GEOAREA1_SHORT_NAME, GEOAREA2_SHORT_NAME)
        .withArg(MIN_BATHROOMS, "Optional. Minimum number of Bathrooms the RealEstate should have.")
        .withArg(MIN_ROOMS, "Optional. Minimum number of Rooms the RealEstate should have.")
        .withArg(
            "propertyType",
            "Optional. The kind of RealEstate that should be scraped. Check the documentation on this one, since the concept might be different for each Site.")
        .withArg(
            "minPrice",
            "Optional. Requires specifying maxPrice. Lower Bound of the price range that the RealEstate's price should be in.")
        .withDependencies("maxPrice")
        .withArg(
            "maxPrice",
            "Optional. Requires specifying minPrice. Upper Bound of the price range that the RealEstate's price should be in.")
        .withDependencies("minPrice")
        .withEntryPoint(Main::run)
        .build()
        .run();
    System.exit(0);
  }

  private static void run(CommandLine commandLine) {
    WebCrawler crawler;
    RealEstateRepository realEstateRepository;
    Site selectedSite = Site.valueOf(commandLine.getOptionValue(SITE).toUpperCase());
    switch (selectedSite) {
      default:
        UrlBuilder urlBuilder = createSearchUrlBuilder(selectedSite, commandLine);
        realEstateRepository = new SpanishEstateHomeMySqlRepository();
        crawler = new SpanishEstateWebCrawlerFactory(urlBuilder).build();
        break;
    }
    crawler.crawl();
    LOGGER.info("Crawling finalized successfully! Persisting data...");
    crawler.getCollectedRealEstates().forEach(realEstateRepository::save);
    LOGGER.info("Data persisted successfully!");
  }

  private static UrlBuilder createSearchUrlBuilder(Site site, CommandLine commandLineApp) {
    switch (site) {
      case SPANISHESTATE:
        return new SpanishEstateSearchUrlBuilder.Builder(commandLineApp.getOptionValue(GEOAREA1))
            .withRegion(commandLineApp.getOptionValue(GEOAREA2))
            .withMunicipality(commandLineApp.getOptionValue(GEOAREA3))
            .withMinimumRestRooms(
                tryParseIntegerArg(commandLineApp.getOptionValue(MIN_BATHROOMS)).orElse(null))
            .withMinimumRooms(
                tryParseIntegerArg(commandLineApp.getOptionValue(MIN_ROOMS)).orElse(null))
            .build();
    }
    return null;
  }

  private static Optional<Integer> tryParseIntegerArg(String argValue) {
    try {
      return Optional.of(Integer.parseInt(argValue));
    } catch (NumberFormatException numberFormatException) {
      return Optional.empty();
    }
  }
}
