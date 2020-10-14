package realestate.measures;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Price {
  @Getter private final double amount;
  @Getter private final String currency; // ISO 4217 currency code

  public Price(double amount, String currency) {
    if (amount < 0) {
      throw new IllegalArgumentException("Price can not be negative.");
    }
    this.amount = amount;
    this.currency = currency;
  }
}
