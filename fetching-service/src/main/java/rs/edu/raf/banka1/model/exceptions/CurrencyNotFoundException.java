package rs.edu.raf.banka1.model.exceptions;

public class CurrencyNotFoundException extends RuntimeException {

    public CurrencyNotFoundException(long id) {
        super("Currency with id " + id + "is not found.");
    }

    public CurrencyNotFoundException(String name) {
        super("Currency with name " + name + "is not found.");
    }
}
