package webcrawlers.fotocasa;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import webcrawlers.fotocasa.specification.Home;
import webcrawling.UrlBuilder;
import webcrawling.specification.Contract;
import webcrawling.specification.PriceRange;

public class SearchRealEstateUrlBuilder implements UrlBuilder {
  private static final String PROTOCOL = "https"; // move to property file
  private static final String SITE_HOST = "www.fotocasa.es"; // move to property file
  private static final String SEARCH_LISTINGS_ENDPOINT = "l";
  private static final String MIN_PRICE_ENDPOINT_PARAMETER = "minPrice";
  private static final String MAX_PRICE_ENDPOINT_PARAMETER = "maxPrice";
  private static final String MIN_ROOMS_ENDPOINT_PARAMETER = "minRooms";
  private static final String MIN_RESTROOMS_ENDPOINT_PARAMETER = "minBathrooms";
  private final String zone;
  private final String language;
  private final PriceRange priceRange;
  private final Home home;
  private final Integer minimumRooms;
  private final Integer minimumRestRooms;
  private final Contract contract;
  private final String searchTerm;

  private SearchRealEstateUrlBuilder(Builder builder) {
    zone = builder.zone;
    language = builder.language;
    priceRange = builder.priceRange;
    home = builder.home;
    minimumRooms = builder.minimumRooms;
    contract = builder.contract;
    searchTerm = builder.searchTerm;
    minimumRestRooms = builder.minimumRestRooms;
  }

  public String buildUrl() {
    String path =
        String.join(
            "/",
            this.language,
            this.contract.toString().toLowerCase(),
            home.toString().toLowerCase(),
            searchTerm,
            zone,
            SEARCH_LISTINGS_ENDPOINT);
    String minRooms = minimumRooms == 0 ? "" : minimumRooms + "+";
    String minRestRooms = minimumRestRooms == 0 ? "" : minimumRestRooms + "+";
    String minPrice =
        priceRange == null ? "" : Double.toString(priceRange.getLowerBound().getAmount());
    String maxPrice =
        priceRange == null ? "" : Double.toString(priceRange.getUpperBound().getAmount());
    Map<String, String> queryParameters =
        new HashMap<>(
            Map.of(
                MIN_PRICE_ENDPOINT_PARAMETER,
                minPrice,
                MAX_PRICE_ENDPOINT_PARAMETER,
                maxPrice,
                MIN_ROOMS_ENDPOINT_PARAMETER,
                minRooms,
                MIN_RESTROOMS_ENDPOINT_PARAMETER,
                minRestRooms));
    queryParameters.keySet().removeIf(key -> queryParameters.get(key).length() == 0);
    URIBuilder urlBuilder = new URIBuilder().setScheme(PROTOCOL).setHost(SITE_HOST).setPath(path);
    queryParameters.forEach(urlBuilder::addParameter);
    String url;
    try {
      url = urlBuilder.build().toString();
    } catch (URISyntaxException syntaxExc) {
      throw new RuntimeException(
          "Failed to build URL with the following components. Path: "
              + path
              + "Query Parans: "
              + queryParameters);
    }
    return url;
  }

  /** {@code SearchRealEstateUrlBuilder} builder static inner class. */
  public static final class Builder {
    private final String zone;
    private final String language;
    private final Contract contract;
    private final String searchTerm;
    private PriceRange priceRange;
    private Home home;
    private int minimumRooms;
    private int minimumRestRooms;

    public Builder(
        @NotNull String searchTerm, String zone, @NotNull Locale locale, Contract contract) {
      this.zone = Optional.ofNullable(zone).orElse("all-zones");
      try {
        this.language = locale.getLanguage();
      } catch (MissingResourceException missingLangage) {
        throw new IllegalArgumentException("The locale misses a valid language.");
      }
      this.searchTerm = searchTerm;
      this.contract = contract;
      this.home = Home.ALL_THE_HOUSES; // default
    }

    /**
     * Sets the {@code priceRange} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param priceRange the {@code priceRange} to set
     * @return a reference to this Builder
     */
    @NotNull
    public Builder withPriceRange(PriceRange priceRange) {
      this.priceRange = priceRange;
      return this;
    }

    /**
     * Sets the {@code home} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param home the {@code home} to set
     * @return a reference to this Builder
     */
    @NotNull
    public Builder withHome(Home home) {
      this.home = home;
      return this;
    }

    /**
     * Sets the {@code minimumRooms} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param minimumRooms the {@code minimumRooms} to set
     * @return a reference to this Builder
     */
    public Builder withMinimumRooms(int minimumRooms) {
      if (minimumRooms < 1 || minimumRooms > 4)
        throw new IllegalArgumentException(
            "Minimum rooms argument needs to be between 1 and 4 or null.");
      this.minimumRooms = minimumRooms;
      return this;
    }

    /**
     * Sets the {@code minimumRestRooms} and returns a reference to this Builder so that the methods
     * can be chained together.
     *
     * @param minimumRestRooms the {@code minimumRestRooms} to set
     * @return a reference to this Builder
     */
    public Builder withMinimumRestRooms(int minimumRestRooms) {
      if (minimumRestRooms < 1 || minimumRestRooms > 3)
        throw new IllegalArgumentException(
            "Minimum rooms argument needs to be between 1 and 3 or null.");
      this.minimumRestRooms = minimumRestRooms;
      return this;
    }

    /**
     * Returns a {@code SearchRealEstateUrlBuilder} built from the parameters previously set.
     *
     * @return a {@code SearchRealEstateUrlBuilder} built with parameters of this {@code
     *     SearchRealEstateUrlBuilder.Builder}
     */
    @NotNull
    public SearchRealEstateUrlBuilder build() {
      return new SearchRealEstateUrlBuilder(this);
    }
  }
}
