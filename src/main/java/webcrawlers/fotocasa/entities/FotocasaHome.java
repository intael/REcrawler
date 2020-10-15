package webcrawlers.fotocasa.entities;

import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import realestate.RealEstate;
import realestate.measures.Price;
import realestate.measures.Surface;

@ToString
public class FotocasaHome extends RealEstate {

  private final Price price;
  private final Surface surface;
  private final String description;
  private final String title;
  private final String agencyName;
  private final String agencyReference;
  private final String fotocasaReference;
  private final Integer numBedrooms;
  private final Integer numBathRooms;
  private final HomeCategory homeCategory;
  private final Boolean hotWater;
  private final Boolean heating;
  private final String status;
  private final String antiquity;
  private final String parking;
  private final Boolean furnished;
  private final String floor;
  private final Boolean elevator;
  private final String orientation;

  public FotocasaHome(
      Price price,
      Surface surface,
      String description,
      String title,
      String agencyName,
      String agencyReference,
      String fotocasaReference,
      Integer numBedrooms,
      Integer numBathRooms,
      HomeCategory homeCategory,
      Boolean hotWater,
      Boolean heating,
      String status,
      String antiquity,
      String parking,
      Boolean furnished,
      String floor,
      Boolean elevator,
      String orientation) {
    this.price = price;
    this.surface = surface;
    this.description = description;
    this.title = title;
    this.agencyName = agencyName;
    this.agencyReference = agencyReference;
    this.fotocasaReference = fotocasaReference;
    this.numBedrooms = numBedrooms;
    this.numBathRooms = numBathRooms;
    this.homeCategory = homeCategory;
    this.hotWater = hotWater;
    this.heating = heating;
    this.status = status;
    this.antiquity = antiquity;
    this.parking = parking;
    this.furnished = furnished;
    this.floor = floor;
    this.elevator = elevator;
    this.orientation = orientation;
  }

  public Price getPrice() {
    return price;
  }

  public Surface getSurface() {
    return surface;
  }

  public String getDescription() {
    return description;
  }

  public String getTitle() {
    return title;
  }

  public String getAgencyName() {
    return agencyName;
  }

  public String getAgencyReference() {
    return agencyReference;
  }

  public String getFotocasaReference() {
    return fotocasaReference;
  }

  public Integer getNumBedrooms() {
    return numBedrooms;
  }

  public Integer getNumBathRooms() {
    return numBathRooms;
  }

  public HomeCategory getHomeCategory() {
    return homeCategory;
  }

  public Boolean getHotWater() {
    return hotWater;
  }

  public Boolean getHeating() {
    return heating;
  }

  public String getStatus() {
    return status;
  }

  public String getAntiquity() {
    return antiquity;
  }

  public String getParking() {
    return parking;
  }

  public Boolean getFurnished() {
    return furnished;
  }

  public String getFloor() {
    return floor;
  }

  public Boolean getElevator() {
    return elevator;
  }

  public String getOrientation() {
    return orientation;
  }

  private FotocasaHome(Builder builder) {
    price = builder.price;
    surface = builder.surface;
    description = builder.description;
    title = builder.title;
    agencyName = builder.agencyName;
    agencyReference = builder.agencyReference;
    fotocasaReference = builder.fotocasaReference;
    numBedrooms = builder.numBedrooms;
    numBathRooms = builder.numBathRooms;
    homeCategory = builder.homeCategory;
    hotWater = builder.hotWater;
    heating = builder.heating;
    status = builder.status;
    antiquity = builder.antiquity;
    parking = builder.parking;
    furnished = builder.furnished;
    floor = builder.floor;
    elevator = builder.elevator;
    orientation = builder.orientation;
  }

  /** {@code FotocasaHome} builder static inner class. */
  public static final class Builder {
    private final Price price;
    private final Surface surface;
    private String description;
    private String title;
    private String agencyName;
    private String agencyReference;
    private final String fotocasaReference;
    private Integer numBedrooms;
    private Integer numBathRooms;
    private HomeCategory homeCategory;
    private Boolean hotWater;
    private Boolean heating;
    private String status;
    private String antiquity;
    private String parking;
    private Boolean furnished;
    private String floor;
    private Boolean elevator;
    private String orientation;

    public Builder(
        @NotNull Price price, @NotNull Surface surface, @NotNull String fotocasaReference) {
      this.price = price;
      this.surface = surface;
      this.fotocasaReference = fotocasaReference;
    }

    /**
     * Sets the {@code description} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param description the {@code description} to set
     * @return a reference to this Builder
     */
    public Builder withDescription(@NotNull String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the {@code title} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param title the {@code title} to set
     * @return a reference to this Builder
     */
    public Builder withTitle(@NotNull String title) {
      this.title = title;
      return this;
    }

    /**
     * Sets the {@code agencyName} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param agencyName the {@code agencyName} to set
     * @return a reference to this Builder
     */
    public Builder withAgencyName(@NotNull String agencyName) {
      this.agencyName = agencyName;
      return this;
    }

    /**
     * Sets the {@code agencyReference} and returns a reference to this Builder so that the methods
     * can be chained together.
     *
     * @param agencyReference the {@code agencyReference} to set
     * @return a reference to this Builder
     */
    public Builder withAgencyReference(@NotNull String agencyReference) {
      this.agencyReference = agencyReference;
      return this;
    }

    /**
     * Sets the {@code numBedrooms} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param numBedrooms the {@code numBedrooms} to set
     * @return a reference to this Builder
     */
    public Builder withNumBedrooms(@NotNull Integer numBedrooms) {
      if (numBedrooms < 0)
        throw new IllegalArgumentException("The number of bedrooms can not be negative.");
      this.numBedrooms = numBedrooms;
      return this;
    }

    /**
     * Sets the {@code numBathRooms} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param numBathRooms the {@code numBathRooms} to set
     * @return a reference to this Builder
     */
    public Builder withNumBathRooms(@NotNull Integer numBathRooms) {
      if (numBathRooms < 0)
        throw new IllegalArgumentException("The number of bathrooms can not be negative.");
      this.numBathRooms = numBathRooms;
      return this;
    }

    /**
     * Sets the {@code homeCategory} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param homeCategory the {@code homeCategory} to set
     * @return a reference to this Builder
     */
    public Builder withHomeCategory(@NotNull HomeCategory homeCategory) {
      this.homeCategory = homeCategory;
      return this;
    }

    /**
     * Sets the {@code hotWater} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param hotWater the {@code hotWater} to set
     * @return a reference to this Builder
     */
    public Builder withHotWater(@NotNull Boolean hotWater) {
      this.hotWater = hotWater;
      return this;
    }

    /**
     * Sets the {@code heating} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param heating the {@code heating} to set
     * @return a reference to this Builder
     */
    public Builder withHeating(@NotNull Boolean heating) {
      this.heating = heating;
      return this;
    }

    /**
     * Sets the {@code status} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param status the {@code status} to set
     * @return a reference to this Builder
     */
    public Builder withStatus(@NotNull String status) {
      this.status = status;
      return this;
    }

    /**
     * Sets the {@code antiquity} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param antiquity the {@code antiquity} to set
     * @return a reference to this Builder
     */
    public Builder withAntiquity(@NotNull String antiquity) {
      this.antiquity = antiquity;
      return this;
    }

    /**
     * Sets the {@code parking} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param parking the {@code parking} to set
     * @return a reference to this Builder
     */
    public Builder withParking(@NotNull String parking) {
      this.parking = parking;
      return this;
    }

    /**
     * Sets the {@code furnished} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param furnished the {@code furnished} to set
     * @return a reference to this Builder
     */
    public Builder withFurnished(@NotNull Boolean furnished) {
      this.furnished = furnished;
      return this;
    }

    /**
     * Sets the {@code floor} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param floor the {@code floor} to set
     * @return a reference to this Builder
     */
    public Builder withFloor(@NotNull String floor) {
      this.floor = floor;
      return this;
    }

    /**
     * Sets the {@code elevator} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param elevator the {@code elevator} to set
     * @return a reference to this Builder
     */
    public Builder withElevator(@NotNull Boolean elevator) {
      this.elevator = elevator;
      return this;
    }

    /**
     * Sets the {@code orientation} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param orientation the {@code orientation} to set
     * @return a reference to this Builder
     */
    public Builder withOrientation(@NotNull String orientation) {
      this.orientation = orientation;
      return this;
    }

    /**
     * Returns a {@code FotocasaHome} built from the parameters previously set.
     *
     * @return a {@code FotocasaHome} built with parameters of this {@code FotocasaHome.Builder}
     */
    public FotocasaHome build() {
      return new FotocasaHome(this);
    }
  }
}
