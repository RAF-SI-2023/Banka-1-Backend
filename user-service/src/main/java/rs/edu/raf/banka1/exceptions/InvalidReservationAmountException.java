package rs.edu.raf.banka1.exceptions;

public class InvalidReservationAmountException extends RuntimeException {
    public InvalidReservationAmountException() {
        super("Cannot commit/release reserved funds because amount is invalid.");
    }
}
