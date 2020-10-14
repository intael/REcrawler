package webcrawlers.fotocasa;

import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import webcrawlers.fotocasa.entities.FotocasaContract;
import webcrawlers.fotocasa.entities.HomeCategory;
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
  private final HomeCategory homeCategory;
  private final Integer minimumRooms;
  private final Integer minimumRestRooms;
  private final Contract contract;
  private final String searchTerm;

  private SearchRealEstateUrlBuilder(Builder builder) {
    zone = builder.zone;
    language = builder.language;
    priceRange = builder.priceRange;
    homeCategory = builder.homeCategory;
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
            homeCategory.toString().toLowerCase(),
            searchTerm,
            zone,
            SEARCH_LISTINGS_ENDPOINT);
    Map<String, String> optionalQueryParameters =
        generateOptionalQueryParametersMap(priceRange, minimumRooms, minimumRestRooms);
    optionalQueryParameters
        .keySet()
        .removeIf(key -> optionalQueryParameters.get(key).length() == 0);
    URIBuilder urlBuilder = new URIBuilder().setScheme(PROTOCOL).setHost(SITE_HOST).setPath(path);
    optionalQueryParameters.forEach(urlBuilder::addParameter);
    String url;
    try {
      url = urlBuilder.build().toString();
    } catch (URISyntaxException syntaxExc) {
      throw new RuntimeException(
          "Failed to build URL with the following components. Path: "
              + path
              + "Query Parans: "
              + optionalQueryParameters);
    }
    return url;
  }

  private Map<String, String> generateOptionalQueryParametersMap(
      PriceRange priceRange, int minimumRooms, int minimumRestRooms) {
    String minRooms = minimumRooms == 0 ? "" : Integer.toString(minimumRooms);
    String minRestRooms = minimumRestRooms == 0 ? "" : Integer.toString(minimumRestRooms);
    String minPrice =
        priceRange == null ? "" : Integer.toString((int) priceRange.getLowerBound().getAmount());
    String maxPrice =
        priceRange == null ? "" : Integer.toString((int) priceRange.getUpperBound().getAmount());
    Map<String, String> queryParameters = new LinkedHashMap<>(); // Insertion order matters
    queryParameters.put(MIN_PRICE_ENDPOINT_PARAMETER, minPrice);
    queryParameters.put(MAX_PRICE_ENDPOINT_PARAMETER, maxPrice);
    queryParameters.put(MIN_ROOMS_ENDPOINT_PARAMETER, minRooms);
    queryParameters.put(MIN_RESTROOMS_ENDPOINT_PARAMETER, minRestRooms);
    return queryParameters;
  }

  /** {@code SearchRealEstateUrlBuilder} builder static inner class. */
  public static final class Builder {
    private String zone;
    private final String language;
    private Contract contract;
    private final String searchTerm;
    private PriceRange priceRange;
    private HomeCategory homeCategory;
    private int minimumRooms;
    private int minimumRestRooms;

    public Builder(@NotNull String searchTerm, @NotNull Locale locale) {
      this.zone = "all-zones";
      try {
        this.language = locale.getLanguage();
      } catch (MissingResourceException missingLangage) {
        throw new IllegalArgumentException("The locale misses a valid language.");
      }
      if(searchTerm.length() == 0)
        throw new IllegalArgumentException("The searchterm argument can not be a 0 length string");
      this.searchTerm = searchTerm;
      this.contract = FotocasaContract.BUY;
      this.homeCategory = HomeCategory.HOMES; // default
    }

    /**
     * Sets the {@code priceRange} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param priceRange the {@code priceRange} to set
     * @return a reference to this Builder
     */
    @NotNull
    public Builder withPriceRange(@NotNull PriceRange priceRange) {
      this.priceRange = priceRange;
      return this;
    }

    /**
     * Sets the {@code zone} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param zone the {@code zone} to set
     * @return a reference to this Builder
     */
    @NotNull
    public Builder atZone(@NotNull String zone) {
      this.zone = zone.toLowerCase();
      return this;
    }

    /**
     * Sets the {@code contract} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param contract the {@code contract} to set
     * @return a reference to this Builder
     */
    @NotNull
    public Builder withContract(@NotNull Contract contract) {
      this.contract = contract;
      return this;
    }

    /**
     * Sets the {@code homeCategory} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param homeCategory the {@code homeCategory} to set
     * @return a reference to this Builder
     */
    @NotNull
    public Builder withHome(@NotNull HomeCategory homeCategory) {
      this.homeCategory = homeCategory;
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
