package rs.edu.raf.banka1.exceptions;

public class CapitalNotFoundByBankAccountException extends NotFoundException {
    public CapitalNotFoundByBankAccountException(String accountNumber) {
        super("Capital not found by bank account: " + accountNumber);
    }
}
