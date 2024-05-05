package rs.edu.raf.banka1.exceptions;

public class InvalidTokenException extends BadRequestException {
    private Reason reason;

    public InvalidTokenException(Reason reason) {
        super(null);
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return reason.getMessage();
    }

    public enum Reason implements ExceptionReason {

        INVALID_TOKEN("Invalid token for activation"),
        INVALID_JWT("Invalid jwt token"),
        CUSTOMER_IS_ALREADY_ACTIVE("Customer is already active");

        private final String message;

        Reason(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
