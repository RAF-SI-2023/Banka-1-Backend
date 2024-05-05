package rs.edu.raf.banka1.exceptions;

public class CapitalNotFoundByCodeException extends NotFoundException {
    public CapitalNotFoundByCodeException(String code) {
        super("Capital not found by code: " + code);
    }
}
