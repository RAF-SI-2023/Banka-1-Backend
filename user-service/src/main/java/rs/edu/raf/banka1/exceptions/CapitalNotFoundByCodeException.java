package rs.edu.raf.banka1.exceptions;

public class CapitalNotFoundByCodeException extends RuntimeException {
    public CapitalNotFoundByCodeException(String code) {
        super("Capital not found by code: " + code);
    }
}
