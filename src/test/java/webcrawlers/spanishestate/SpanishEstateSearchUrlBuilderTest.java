package webcrawlers.spanishestate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import realestate.measures.Price;
import webcrawling.specification.PriceRange;

class SpanishEstateSearchUrlBuilderTest {

  SpanishEstateSearchUrlBuilder instanceWithoutOptionals;
  SpanishEstateSearchUrlBuilder instanceWithAllOptionals;

  @BeforeEach
  void setUp() {
    instanceWithoutOptionals =
        new SpanishEstateSearchUrlBuilder.Builder("province-barcelona").build();
    Price lowerBound = new Price(800, "EUR");
    Price upperBound = new Price(2000, "EUR");
    PriceRange somePriceRange = new PriceRange(lowerBound, upperBound);
    instanceWithAllOptionals =
        new SpanishEstateSearchUrlBuilder.Builder("province-barcelona")
            .withRegion("region-baix-llobregat")
            .withMunicipality("municipality-sant-boi-de-llobregat")
            .withPropertyType("7,3,21")
            .withPriceRange(somePriceRange)
            .withMinimumRooms(3)
            .withMinimumRestRooms(2)
            .withMinimumSurface(100)
            .build();
  }

  @Test
  public void buildUrlWithoutOptionalsCreatesExpectedUrl() {
    assertEquals(
        "https://www.spanishestate.com/sale/property/province-barcelona/?search=true",
        instanceWithoutOptionals.buildUrl());
  }

  @Test
  public void buildUrlWithAllOptionalsCreatesExpectedUrl() {
    assertEquals(
        "https://www.spanishestate.com/sale/property/province-barcelona/region-baix-llobregat/municipality-sant-boi-de-llobregat/?search=true&pricefrom=800&priceto=2000&propertytypes=7%2C3%2C21&bedrooms=3&bathrooms=2&m2_surface=100",
        instanceWithAllOptionals.buildUrl());
  }

  @Test
  void builderThrowsIllegalArgExceptionWhenSearchTermIsEmptyString() {
    assertThrows(
        IllegalArgumentException.class, () -> new SpanishEstateSearchUrlBuilder.Builder(""));
  }

  @Test
  public void buildUrlWithOptionalsAndWeirdCharactersCreatesExpectedUrl() {
    assertEquals(
        "https://www.spanishestate.com/sale/property/province-barcelona/?search=true",
        instanceWithoutOptionals.buildUrl());
  }
}
