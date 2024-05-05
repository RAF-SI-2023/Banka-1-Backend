package rs.edu.raf.banka1.exceptions;

import rs.edu.raf.banka1.requests.CreateBankAccountRequest;

public class CreateBankAccountException extends BadRequestException {
    private Reason reason;
    private CreateBankAccountRequest createBankAccountRequest;

    public CreateBankAccountException(Reason reason, CreateBankAccountRequest createBankAccountRequest) {
        super(null);
        this.reason = reason;
        this.createBankAccountRequest = createBankAccountRequest;
    }

    @Override
    public String getMessage() {
        return "Can not create bank account because " + formatReasonString();
    }

    public String formatReasonString() {
        switch (reason){
            case CURRENCY_NOT_FOUND -> {
                return String.format(reason.getMessage(), createBankAccountRequest.getAccount().getCurrencyCode());
            }
            case CUSTOMER_NOT_FOUND -> {
                return String.format(reason.getMessage(), createBankAccountRequest.getCustomerId());
            }
            case COMPANY_NOT_FOUND -> {
                return String.format(reason.getMessage(), createBankAccountRequest.getCompanyId());
            }
        }
        return reason.getMessage();
    }

    public enum Reason implements ExceptionReason {

        INVALID_ACCOUNT_TYPE("invalid account type provided in createBankAccount request."),
        CURRENCY_NOT_FOUND("currency %s is not found"),
        COMPANY_NOT_FOUND("company %s is not found"),
        CUSTOMER_NOT_FOUND("customer %s is not found");

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
