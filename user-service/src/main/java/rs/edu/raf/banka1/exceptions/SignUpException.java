package rs.edu.raf.banka1.exceptions;

import rs.edu.raf.banka1.requests.InitialActivationRequest;

public class SignUpException extends BadRequestException {
    private Reason reason;
    private InitialActivationRequest activationRequest;

    public SignUpException(Reason reason, InitialActivationRequest activationRequest) {
        super(null);
        this.reason = reason;
        this.activationRequest = activationRequest;
    }

    @Override
    public String getMessage() {
        switch (reason){
            case BANK_ACCOUNT_NOT_FOUND,BANK_ACCOUNT_IS_NOT_LINKED_TO_CUSTOMER -> {
                return String.format(reason.getMessage(),activationRequest.getAccountNumber());
            }
            case BANK_ACCOUNT_IS_NOT_LINKED_TO_EMAIL_OR_PHONE -> {
                return String.format(reason.getMessage(),activationRequest.getEmail(),activationRequest.getPhoneNumber());
            }
        }
        return reason.getMessage();
    }

    public enum Reason implements ExceptionReason {

        BANK_ACCOUNT_NOT_FOUND("Bank account %s is not found"),
        CUSTOMER_IS_ALREADY_ACTIVE("Customer is already active"),
        BANK_ACCOUNT_IS_NOT_LINKED_TO_CUSTOMER("Bank account %s is not linked to customer"),
        BANK_ACCOUNT_IS_NOT_LINKED_TO_EMAIL_OR_PHONE("Bank account is not linked to customer with email %s or phone %s");

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
