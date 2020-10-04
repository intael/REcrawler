package webcrawlers.fotocasa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import realestate.measures.Price;
import webcrawlers.fotocasa.specification.FotocasaContract;
import webcrawlers.fotocasa.specification.Home;
import webcrawling.specification.PriceRange;

public class SearchRealEstateUrlBuilderTest {

  SearchRealEstateUrlBuilder instanceWithoutOptionals;
  SearchRealEstateUrlBuilder instanceWithAllOptionals;

  @BeforeEach
  public void setUp() {
    instanceWithoutOptionals =
        new SearchRealEstateUrlBuilder.Builder("barcelona", new Locale("en")).build();
    Price lowerBound = new Price(800, "EUR");
    Price upperBound = new Price(2000, "EUR");
    PriceRange somePriceRange = new PriceRange(lowerBound, upperBound);
    instanceWithAllOptionals =
        new SearchRealEstateUrlBuilder.Builder("barcelona", new Locale("en"))
            .withContract(FotocasaContract.RENTAL)
            .withPriceRange(somePriceRange)
            .atZone("Sants")
            .withHome(Home.FLATS)
            .withMinimumRestRooms(2)
            .withMinimumRooms(3)
            .build();
  }

  @Test
  public void buildUrlWithoutOptionalsCreatesExpectedUrl() {
    assertEquals(
        "https://www.fotocasa.es/en/buy/homes/barcelona/all-zones/l",
        instanceWithoutOptionals.buildUrl());
  }

  @Test
  public void buildUrlWithAllOptionalsCreatesExpectedUrl() {
    assertEquals(
        "https://www.fotocasa.es/en/rental/flats/barcelona/sants/l?minPrice=800&maxPrice=2000&minRooms=3&minBathrooms=2",
        instanceWithAllOptionals.buildUrl());
  }

  @Test
  void builderRejectsNullOptionalQueryParameters() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SearchRealEstateUrlBuilder.Builder("barcelona", new Locale("en")).atZone(null));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SearchRealEstateUrlBuilder.Builder("barcelona", new Locale("en"))
                .withContract(null));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SearchRealEstateUrlBuilder.Builder("barcelona", new Locale("en"))
                .withPriceRange(null));
  }
}
