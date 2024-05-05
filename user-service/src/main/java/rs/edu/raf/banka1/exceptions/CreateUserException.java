package rs.edu.raf.banka1.exceptions;

import rs.edu.raf.banka1.requests.customer.CreateCustomerRequest;

public class CreateUserException extends BadRequestException {
    private Reason reason;
    private CreateCustomerRequest activationRequest;

    public CreateUserException(Reason reason, CreateCustomerRequest activationRequest) {
        super(null);
        this.reason = reason;
        this.activationRequest = activationRequest;
    }

    @Override
    public String getMessage() {
        switch (reason){
            case BANK_ACCOUNT_NOT_FOUND -> {
                return String.format(reason.getMessage(),activationRequest.getAccount().getAccountName());
            }
            case CURRENCY_NOT_FOUND -> {
                return String.format(reason.getMessage(),activationRequest.getAccount().getCurrencyCode());
            }
        }
        return reason.getMessage();
    }

    public enum Reason implements ExceptionReason {

        BANK_ACCOUNT_NOT_FOUND("Bank account %s is not found"),
        CURRENCY_NOT_FOUND("Currency %s is not found");

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
