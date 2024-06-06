package rs.edu.raf.banka1.exceptions;

import rs.edu.raf.banka1.model.ListingType;

public class MarginAccountNotFoundException extends RuntimeException {
    public MarginAccountNotFoundException(Long id, ListingType listingType, String currencyCode) {
        super("Margin account not found by id: " + id + ", listing type: " + listingType + ", currency code: " + currencyCode);
    }
}
