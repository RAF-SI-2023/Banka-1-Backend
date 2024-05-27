package rs.edu.raf.banka1.exceptions;

public class OTCListingTypeException extends RuntimeException {
    public OTCListingTypeException() {
        super("Individuals must trade with STOCKs only.");
    }
}
