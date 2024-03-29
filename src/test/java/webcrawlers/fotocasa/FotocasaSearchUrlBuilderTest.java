package webcrawlers.fotocasa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import realestate.measures.Price;
import webcrawlers.fotocasa.entities.FotocasaContract;
import webcrawlers.fotocasa.entities.HomeCategory;
import webcrawling.specification.PriceRange;

public class FotocasaSearchUrlBuilderTest {

  FotocasaSearchUrlBuilder instanceWithoutOptionals;
  FotocasaSearchUrlBuilder instanceWithAllOptionals;

  @BeforeEach
  public void setUp() {
    instanceWithoutOptionals =
        new FotocasaSearchUrlBuilder.Builder("barcelona", new Locale("en")).build();
    Price lowerBound = new Price(800, "EUR");
    Price upperBound = new Price(2000, "EUR");
    PriceRange somePriceRange = new PriceRange(lowerBound, upperBound);
    instanceWithAllOptionals =
        new FotocasaSearchUrlBuilder.Builder("barcelona", new Locale("en"))
            .withContract(FotocasaContract.RENTAL)
            .withPriceRange(somePriceRange)
            .atZone("Sants")
            .withHome(HomeCategory.FLATS)
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
  void builderThrowsIllegalArgExceptionWhenSearchTermIsEmptyString() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new FotocasaSearchUrlBuilder.Builder("", new Locale("en")));
  }
}
