package realestate.measures;

import lombok.ToString;

@ToString
public class Surface {

  private final double amount;

  public Surface(double amount) {
    guard(amount);
    this.amount = amount;
  }

  private void guard(double amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Surface's amount can not be negative.");
    }
  }

  public double getAmount() {
    return amount;
  }
}
