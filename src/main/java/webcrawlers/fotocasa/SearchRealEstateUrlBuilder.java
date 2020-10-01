package webcrawlers.fotocasa;

import java.util.Locale;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import webcrawlers.fotocasa.specification.Home;
import webcrawlers.fotocasa.specification.Rooms;
import webcrawling.UrlBuilder;
import webcrawling.specification.Contract;
import webcrawling.specification.PriceRange;

public class SearchRealEstateUrlBuilder implements UrlBuilder {
  private static final String SITE_URL = "https://www.fotocasa.es/"; // move to property file
  private final String zone;
  private final Locale locale;
  private Optional<PriceRange> priceRange;
  private Optional<Home> home;
  private Optional<Rooms> rooms;
  private final Contract contract;

  public SearchRealEstateUrlBuilder(
      @NotNull Contract contract,
      Locale locale,
      String zone,
      Home home,
      PriceRange priceRange,
      Rooms rooms) {

    this.contract = contract;
    this.priceRange = Optional.ofNullable(priceRange);
    this.home = Optional.ofNullable(home);
    this.rooms = Optional.ofNullable(rooms);
    this.zone = Optional.ofNullable(zone).orElse("all-zones");
    this.locale = Optional.ofNullable(locale).orElse(new Locale("en"));
  }

  public String buildUrl() {
    // build url logic
    return "";
  }
}
