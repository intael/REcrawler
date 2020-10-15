package realestate.measures;

import lombok.ToString;

@ToString
public class Price {
  private final double amount;
  private final String currency; // ISO 4217 currency code

  public Price(double amount, String currency) {
    guard(amount);
    this.amount = amount;
    this.currency = currency;
  }

  private void guard(double amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Price's amount can not be negative.");
    }
  }

  public double getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }
}
