package realestate.measures;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import realestate.exceptions.InvalidPrice;
import realestate.exceptions.NegativeAmount;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PriceTest {
    @Test
    void createPriceWithNegativeAmountShouldRaiseProperContextException() {
        NegativeAmount exception = assertThrows(
                InvalidPrice.class,
                () ->  new Price(-1, "EUR")
        );

        Assert.assertEquals(-1.0, exception.getContextValues("Amount").get(0));
        Assert.assertEquals("Price", exception.getContextValues("Entity").get(0));
    }

    @Test
    void createPriceWithValidAmountShouldNotRaiseException() {
        new Price(1, "EUR");
    }
}
