package webcrawling.specification;

import realestate.measures.Price;

public class PriceRange {
  private Price lowerBound;
  private Price upperBound;

  public PriceRange(Price lowerBound, Price upperBound) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }
}
