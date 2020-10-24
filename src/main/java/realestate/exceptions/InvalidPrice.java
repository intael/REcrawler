package realestate.exceptions;

public class InvalidPrice extends NegativeAmount {
    public InvalidPrice(double amount) {
        super(amount, "Price");
    }
}
