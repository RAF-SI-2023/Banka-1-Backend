package rs.edu.raf.banka1.exceptions;

public class InvalidCapitalAmountException extends BadRequestException {
    public InvalidCapitalAmountException(Double amount) {
        super("Invalid capital amount: " + amount);
    }
}
