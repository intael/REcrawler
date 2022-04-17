package webcrawling.spanishestate.entities;

import realestate.RealEstate;
import realestate.measures.Price;
import realestate.measures.Surface;

public class SpanishEstateHome extends RealEstate {

  private final int seNumber;
  private final String seReference;
  protected final Price price;
  private final String region;
  private final String location;
  private final String type;
  private final int bedrooms;
  private final int bathrooms;
  protected final Surface surface;
  private final Surface plot;
  private final String title;
  private final String description;

  public SpanishEstateHome(
      int seNumber,
      String seReference,
      Price price,
      String region,
      String location,
      String type,
      int bedrooms,
      int bathrooms,
      Surface surface,
      Surface plot,
      String title,
      String description) {
    this.seNumber = seNumber;
    this.seReference = seReference;
    this.price = price;
    this.region = region;
    this.location = location;
    this.type = type;
    this.bedrooms = bedrooms;
    this.bathrooms = bathrooms;
    this.surface = surface;
    this.plot = plot;
    this.title = title;
    this.description = description;
  }

  public int getSeNumber() {
    return seNumber;
  }

  public String getSeReference() {
    return seReference;
  }

  public Price getPrice() {
    return price;
  }

  public String getRegion() {
    return region;
  }

  public String getLocation() {
    return location;
  }

  public String getType() {
    return type;
  }

  public int getBedrooms() {
    return bedrooms;
  }

  public int getBathrooms() {
    return bathrooms;
  }

  public Surface getSurface() {
    return surface;
  }

  public Surface getPlot() {
    return plot;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  private SpanishEstateHome(Builder builder) {
    seNumber = builder.seNumber;
    seReference = builder.seReference;
    price = builder.price;
    region = builder.region;
    location = builder.location;
    type = builder.type;
    bedrooms = builder.bedrooms;
    bathrooms = builder.bathrooms;
    surface = builder.surface;
    plot = builder.plot;
    title = builder.title;
    description = builder.description;
  }

  /** {@code SpanishEstateHome} builder static inner class. */
  public static final class Builder {
    private final int seNumber;
    private final String seReference;
    private final Price price;
    private String region;
    private String location;
    private String type;
    private int bedrooms;
    private int bathrooms;
    private Surface surface;
    private Surface plot;
    private String title;
    private String description;

    public Builder(int seNumber, String seReference, Price price) {
      nonNegativeGuard(seNumber, "seNumber");
      this.seNumber = seNumber;
      this.seReference = seReference;
      this.price = price;
    }

    private void nonNegativeGuard(int argument, String attribute) {
      if (argument < 0)
        throw new IllegalArgumentException(
            String.format("Argument for %s can not be negative!", attribute));
    }

    /**
     * Sets the {@code surface} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param surface the {@code surface} to set
     * @return a reference to this Builder
     */
    public Builder withSurface(Surface surface) {
      this.surface = surface;
      return this;
    }

    /**
     * Sets the {@code region} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param region the {@code region} to set
     * @return a reference to this Builder
     */
    public Builder withRegion(String region) {
      this.region = region;
      return this;
    }

    /**
     * Sets the {@code location} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param location the {@code location} to set
     * @return a reference to this Builder
     */
    public Builder withLocation(String location) {
      this.location = location;
      return this;
    }

    /**
     * Sets the {@code type} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param type the {@code type} to set
     * @return a reference to this Builder
     */
    public Builder withType(String type) {
      this.type = type;
      return this;
    }

    /**
     * Sets the {@code bedrooms} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param bedrooms the {@code bedrooms} to set
     * @return a reference to this Builder
     */
    public Builder withBedrooms(int bedrooms) {
      nonNegativeGuard(bedrooms, "bedrooms");
      this.bedrooms = bedrooms;
      return this;
    }

    /**
     * Sets the {@code bathrooms} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param bathrooms the {@code bathrooms} to set
     * @return a reference to this Builder
     */
    public Builder withBathrooms(int bathrooms) {
      nonNegativeGuard(bathrooms, "bathrooms");
      this.bathrooms = bathrooms;
      return this;
    }

    /**
     * Sets the {@code plot} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param plot the {@code plot} to set
     * @return a reference to this Builder
     */
    public Builder withPlot(Surface plot) {
      this.plot = plot;
      return this;
    }

    /**
     * Sets the {@code title} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param title the {@code title} to set
     * @return a reference to this Builder
     */
    public Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    /**
     * Sets the {@code description} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param description the {@code description} to set
     * @return a reference to this Builder
     */
    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    /**
     * Returns a {@code SpanishEstateHome} built from the parameters previously set.
     *
     * @return a {@code SpanishEstateHome} built with parameters of this {@code
     *     SpanishEstateHome.Builder}
     */
    public SpanishEstateHome build() {
      return new SpanishEstateHome(this);
    }
  }
}
