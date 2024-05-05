package rs.edu.raf.banka1.exceptions;

public class InvalidOrderListingAmountException extends BadRequestException {
    public InvalidOrderListingAmountException() {
        super("Invalid order amount.");
    }
}
