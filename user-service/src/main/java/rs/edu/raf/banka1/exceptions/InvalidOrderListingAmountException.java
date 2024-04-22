package rs.edu.raf.banka1.exceptions;

public class InvalidOrderListingAmountException extends RuntimeException {
    public InvalidOrderListingAmountException() {
        super("Invalid order amount.");
    }
}
