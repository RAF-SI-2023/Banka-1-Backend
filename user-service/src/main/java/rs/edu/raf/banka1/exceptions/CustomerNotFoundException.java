package rs.edu.raf.banka1.exceptions;

public class CustomerNotFoundException extends NotFoundException{
    public CustomerNotFoundException(Long customerId) {
        super("Customer with id: " + customerId + " not found");
    }

    public CustomerNotFoundException(String customer) {
        super("Customer " + customer + " not found");
    }
}
