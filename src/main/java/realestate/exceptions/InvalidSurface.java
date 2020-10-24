package realestate.exceptions;

public class InvalidSurface extends NegativeAmount {
    public InvalidSurface(double amount) {
        super(amount, "Surface");
    }
}
