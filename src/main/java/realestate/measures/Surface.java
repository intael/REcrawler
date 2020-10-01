package realestate.measures;

public class Surface {

  private final double amount;
  private final String unit;

  public Surface(double amount, String unit) {
    if (amount < 0) {
      throw new IllegalArgumentException("Surface can not be negative.");
    }
    this.amount = amount;
    this.unit = unit;
  }

  public double getAmount() {
    return amount;
  }

  public String getUnit() {
    return unit;
  }
}
