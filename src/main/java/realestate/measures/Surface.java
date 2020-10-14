package realestate.measures;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Surface {

  @Getter private final double amount;

  public Surface(double amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Surface can not be negative.");
    }
    this.amount = amount;
  }
}
