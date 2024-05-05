package rs.edu.raf.banka1.exceptions;

import rs.edu.raf.banka1.model.ListingType;

public class CapitalNotFoundByListingIdAndTypeException extends NotFoundException {
    public CapitalNotFoundByListingIdAndTypeException(Long id, ListingType type) {
        super("Capital not found by listingId: " + id + " and type: " + type);
    }
}
