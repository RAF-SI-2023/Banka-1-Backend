package rs.edu.raf.banka1.exceptions;

import rs.edu.raf.banka1.requests.CreateTransferRequest;

public class CreateTransferException extends BadRequestException {
    private Reason reason;
    private CreateTransferRequest createTransferRequest;

    public CreateTransferException(Reason reason, CreateTransferRequest createTransferRequest) {
        super(null);
        this.reason = reason;
        this.createTransferRequest = createTransferRequest;
    }

    public CreateTransferException(Reason reason) {
        super(null);
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        switch (reason){
            case SENDER_NOT_FOUND -> {
                return String.format(reason.getMessage(), createTransferRequest.getSenderAccountNumber());
            }
            case RECIPIENT_NOT_FOUND -> {
                return String.format(reason.getMessage(), createTransferRequest.getRecipientAccountNumber());
            }
        }
        return reason.getMessage();
    }

    public enum Reason implements ExceptionReason {

        INVALID_PARAMETERS("Transfer denied. Invalid parameters or insufficient balance"),
        BANK_NOT_FOUND_FOR_CONVERSION("Transfer denied. Reason: bank not found for currency conversion."),
        SENDER_NOT_FOUND("Sender %s is not found"),
        RECIPIENT_NOT_FOUND("Recipient %s is not found");

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
