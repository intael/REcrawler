package realestate.measures;

public class Price {
  private final double amount;
  private final String currency; // ISO 4217 currency code

  public Price(double amount, String currency) {
    if (amount < 0) {
      throw new IllegalArgumentException("Price can not be negative.");
    }
    this.amount = amount;
    this.currency = currency;
  }

  public double getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }
}
