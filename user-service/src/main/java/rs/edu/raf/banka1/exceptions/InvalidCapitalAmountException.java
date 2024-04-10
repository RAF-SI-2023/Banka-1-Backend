package rs.edu.raf.banka1.exceptions;

public class InvalidCapitalAmountException extends RuntimeException {
    public InvalidCapitalAmountException(Double amount) {
        super("Invalid capital amount: " + amount);
    }
}
