package rs.edu.raf.banka1.exceptions;

import rs.edu.raf.banka1.model.ListingType;

public class CapitalNotFoundByListingIdAndTypeException extends RuntimeException {
    public CapitalNotFoundByListingIdAndTypeException(Long id, ListingType type) {
        super("Capital not found by listingId: " + id + " and type: " + type);
    }
}
