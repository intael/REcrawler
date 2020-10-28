package webcrawlers.spanishestate;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import realestate.measures.Surface;
import webcrawling.UrlBuilder;
import webcrawling.specification.PriceRange;

public class SpanishEstateSearchUrlBuilder implements UrlBuilder {
  private static final String PROTOCOL = "https";
  private static final String SITE_HOST = "www.spanishestate.com";
  private static final String SEARCH_ENDPOINT_PARAMETER = "search";
  private static final String SEARCH_ENDPOINT_PARAMETER_DEFAULT_VALUE = "true";
  private static final String MIN_SURFACE_ENDPOINT_PARAMETER = "m2_surface";
  private static final String PROPERTY_TYPE_ENDPOINT_PARAMETER = "propertytypes";
  private static final String PRICE_FROM_ENDPOINT_PARAMETER = "pricefrom";
  private static final String PRICE_TO_ENDPOINT_PARAMETER = "priceto";
  private static final String BEDROOMS_ENDPOINT_PARAMETER = "bedrooms";
  private static final String BATHROOMS_ENDPOINT_PARAMETER = "bathrooms";
  private final String province;
  private final String region;
  private final String municipality;
  private final String propertyTypes;
  private final PriceRange priceRange;
  private final Integer minimumRooms;
  private final Integer minimumRestRooms;
  private final Surface surface;

  @Override
  public String buildUrl() {
    String path =
        String.join(
                "/",
                buildStringFromOptionalStrings( // this is needed because the potential nulls from
                                                // region and municipality can cause problems if not
                                                // explicitly excluded
                    "sale", "property", province, region, municipality))
            + "/";
    Map<String, String> optionalQueryParameters =
        generateOptionalQueryParametersMap(
            priceRange, minimumRooms, minimumRestRooms, propertyTypes, surface);
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
              + "Query Params: "
              + optionalQueryParameters);
    }
    return url;
  }

  private List<String> buildStringFromOptionalStrings(String... optionalStrings) {
    List<String> pathComponents = new ArrayList<>();
    for (String string : optionalStrings) {
      if (string != null) pathComponents.add(string);
    }
    return pathComponents;
  }

  private Map<String, String> generateOptionalQueryParametersMap(
      PriceRange priceRange,
      int minimumRooms,
      int minimumRestRooms,
      String propertyTypes,
      Surface surface) {
    String minRooms = minimumRooms == 0 ? "" : Integer.toString(minimumRooms);
    String minRestRooms = minimumRestRooms == 0 ? "" : Integer.toString(minimumRestRooms);
    String minPrice =
        priceRange == null ? "" : Integer.toString((int) priceRange.getLowerBound().getAmount());
    String maxPrice =
        priceRange == null ? "" : Integer.toString((int) priceRange.getUpperBound().getAmount());
    String propTypes = propertyTypes == null ? "" : propertyTypes; // TODO: Add some validation
    String surfaceAmount = surface == null ? "" : Integer.toString((int) surface.getAmount());
    Map<String, String> queryParameters = new LinkedHashMap<>(); // Insertion order matters
    queryParameters.put(SEARCH_ENDPOINT_PARAMETER, SEARCH_ENDPOINT_PARAMETER_DEFAULT_VALUE);
    queryParameters.put(PRICE_FROM_ENDPOINT_PARAMETER, minPrice);
    queryParameters.put(PRICE_TO_ENDPOINT_PARAMETER, maxPrice);
    queryParameters.put(PROPERTY_TYPE_ENDPOINT_PARAMETER, propTypes);
    queryParameters.put(BEDROOMS_ENDPOINT_PARAMETER, minRooms);
    queryParameters.put(BATHROOMS_ENDPOINT_PARAMETER, minRestRooms);
    queryParameters.put(MIN_SURFACE_ENDPOINT_PARAMETER, surfaceAmount);
    return queryParameters;
  }

  private SpanishEstateSearchUrlBuilder(Builder builder) {
    province = builder.province;
    region = builder.region;
    municipality = builder.municipality;
    propertyTypes = builder.propertyTypes;
    priceRange = builder.priceRange;
    minimumRooms = builder.minimumRooms;
    minimumRestRooms = builder.minimumRestRooms;
    surface = builder.surface;
  }

  /** {@code SpanishEstateSearchUrlBuilder} builder static inner class. */
  public static final class Builder {
    private String province;
    private String region;
    private String municipality;
    private String propertyTypes;
    private PriceRange priceRange;
    private int minimumRooms;
    private int minimumRestRooms;
    private Surface surface;

    public Builder(@NotNull String province) {
      guard(province);
      this.province = province;
    }

    private void guard(@NotNull String searchTerm) {
      if (searchTerm.length() == 0)
        throw new IllegalArgumentException("The province argument can not be a 0 length string");
    }

    /**
     * Sets the {@code region} and returns a reference to this Builder so that the methods can be
     * chained together. Note: If passing a region, be sure it matches what the site expects or the
     * site will say there are no results.
     *
     * @param region the {@code region} to set
     * @return a reference to this Builder
     */
    public Builder withRegion(String region) {
      this.region = region;
      return this;
    }

    /**
     * Sets the {@code municipality} and returns a reference to this Builder so that the methods can
     * be chained together. Note: If passing a municipality, be sure it matches what the site
     * expects or the site will say there are no results.
     *
     * @param municipality the {@code municipality} to set
     * @return a reference to this Builder
     */
    public Builder withMunicipality(String municipality) {
      if (region == null)
        throw new IllegalStateException(
            "Municipality can not be initialized without region being already set. Call withRegion first and make sure this municipality belongs to that region or the site will return 0 results.");
      this.municipality = municipality;
      return this;
    }

    /**
     * Sets the {@code propertyType} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param propertyType the {@code propertyType} to set
     * @return a reference to this Builder
     */
    public Builder withPropertyType(String propertyType) {
      this.propertyTypes = propertyType;
      return this;
    }

    /**
     * Sets the {@code priceRange} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param priceRange the {@code priceRange} to set
     * @return a reference to this Builder
     */
    public Builder withPriceRange(@NotNull PriceRange priceRange) {
      this.priceRange = priceRange;
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
      this.minimumRestRooms = minimumRestRooms;
      return this;
    }

    /**
     * Sets the {@code surface} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param surface the {@code surface} to set
     * @return a reference to this Builder
     */
    public Builder withMinimumSurface(int surface) {
      this.surface = new Surface(surface);
      return this;
    }

    /**
     * Returns a {@code SpanishEstateSearchUrlBuilder} built from the parameters previously set.
     *
     * @return a {@code SpanishEstateSearchUrlBuilder} built with parameters of this {@code
     *     SpanishEstateSearchUrlBuilder.Builder}
     */
    public SpanishEstateSearchUrlBuilder build() {
      return new SpanishEstateSearchUrlBuilder(this);
    }
  }
}
