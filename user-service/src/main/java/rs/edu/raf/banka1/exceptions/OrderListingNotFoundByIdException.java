package rs.edu.raf.banka1.exceptions;

public class OrderListingNotFoundByIdException extends RuntimeException {
    public OrderListingNotFoundByIdException(Long id) {
        super("Listing not found by id: " + id);
    }
}
