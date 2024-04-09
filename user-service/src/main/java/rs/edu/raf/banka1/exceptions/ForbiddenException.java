package rs.edu.raf.banka1.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("You are not allowed to take this action");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
