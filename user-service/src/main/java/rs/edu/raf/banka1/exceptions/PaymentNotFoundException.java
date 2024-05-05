package rs.edu.raf.banka1.exceptions;

public class PaymentNotFoundException extends NotFoundException{
    public PaymentNotFoundException(Long customerId) {
        super("Payment with id: " + customerId + " not found");
    }
}
