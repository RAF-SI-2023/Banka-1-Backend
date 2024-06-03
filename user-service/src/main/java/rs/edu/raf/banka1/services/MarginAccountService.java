package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.MarginAccount;

public interface MarginAccountService {
    MarginAccount getMarginAccount(Long id, ListingType listingType, String currencyCode);
}
