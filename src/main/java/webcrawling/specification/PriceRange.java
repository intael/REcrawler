package webcrawling.specification;

import org.jetbrains.annotations.NotNull;
import realestate.measures.Price;

public class PriceRange {
  public Price getLowerBound() {
    return lowerBound;
  }

  public Price getUpperBound() {
    return upperBound;
  }

  private final Price lowerBound;
  private final Price upperBound;

  public PriceRange(@NotNull Price lowerBound, @NotNull Price upperBound) {
    if (lowerBound.getAmount() > upperBound.getAmount()) {
      throw new IllegalArgumentException(
          "Invalid Price Range: The lower bound price is higher than the upper bound price.");
    }
    if (!lowerBound.getCurrency().equals(upperBound.getCurrency()))
      throw new IllegalArgumentException(
          "Invalid Price Range: The upper and lower bound price currencies do not match.");
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Override
  public String toString() {
    return "PriceRange{" + "lowerBound=" + lowerBound + ", upperBound=" + upperBound + '}';
  }
}
