package rs.edu.raf.banka1.exceptions;

import rs.edu.raf.banka1.requests.CreatePaymentRequest;
import rs.edu.raf.banka1.requests.customer.CreateCustomerRequest;

public class CreatePaymentException extends BadRequestException {
    private Reason reason;
    private CreatePaymentRequest activationRequest;

    public CreatePaymentException(Reason reason, CreatePaymentRequest activationRequest) {
        super("Failed to create payment from request: {}");
        this.reason = reason;
        this.activationRequest = activationRequest;
    }

    @Override
    public String getMessage() {
        switch (reason){
            case BANK_ACCOUNT_NOT_FOUND -> {
                return String.format(reason.getMessage(),activationRequest.getSenderAccountNumber());
            }
        }
        return reason.getMessage();
    }

    public enum Reason implements ExceptionReason {

        BANK_ACCOUNT_NOT_FOUND("Bank account %s is not found"),
        BAD_VALIDATION("Payment validation failed");

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
