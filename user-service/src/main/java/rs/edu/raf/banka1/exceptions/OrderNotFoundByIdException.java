package rs.edu.raf.banka1.exceptions;

public class OrderNotFoundByIdException extends RuntimeException {
    public OrderNotFoundByIdException(Long orderId) {
        super("Order not found by id: " + orderId);
    }
}
