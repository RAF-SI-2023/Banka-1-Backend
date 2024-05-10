package rs.edu.raf.banka1.exceptions;

public class OTCInvalidBankAccountCurrencyException extends RuntimeException {
    public OTCInvalidBankAccountCurrencyException() {
        super("Capitals can only work with RSD currency accounts.");
    }
}
