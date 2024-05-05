package rs.edu.raf.banka1.exceptions;

public class NotEnoughCapitalAvailableException extends BadRequestException {
    public NotEnoughCapitalAvailableException() {
        super("Not enough capital is available.");
    }
}
