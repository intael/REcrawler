package realestate.measures;

import lombok.ToString;
import realestate.exceptions.InvalidSurface;

@ToString
public class Surface {

  private final double amount;

  public Surface(double amount) {
    guard(amount);
    this.amount = amount;
  }

  private void guard(double amount) {
    if (amount < 0) {
      throw new InvalidSurface(amount);
    }
  }

  public double getAmount() {
    return amount;
  }
}
