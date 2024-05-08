package rs.edu.raf.banka1.exceptions;

public class SetNewPasswordException extends BadRequestException {
    private Reason reason;

    public SetNewPasswordException(Reason reason) {
        super(null);
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return "Can not set password because " + formatReasonString();
    }

    public String formatReasonString() {
        return reason.getMessage();
    }

    public enum Reason implements ExceptionReason {

        SHORT_PASSWORD("password should be at least 4 characters."),
        INVALID_TOKEN("token is invalid.");

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
