package realestate.measures;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import realestate.exceptions.InvalidSurface;
import realestate.exceptions.NegativeAmount;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SurfaceTest {
    @Test
    void createPriceWithNegativeAmountShouldRaiseProperContextException() {
        NegativeAmount exception = assertThrows(
                InvalidSurface.class,
                () ->  new Surface(-1)
        );

        Assert.assertEquals(-1.0, exception.getContextValues("Amount").get(0));
        Assert.assertEquals("Surface", exception.getContextValues("Entity").get(0));
    }

    @Test
    void createPriceWithValidAmountShouldNotRaiseException() {
        new Surface(1);
    }
}
