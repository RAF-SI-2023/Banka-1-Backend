package rs.edu.raf.banka1.exceptions;

public class CustomerNotFoundException  extends RuntimeException{
    public CustomerNotFoundException() {
        super("Customer not found");
    }
}
