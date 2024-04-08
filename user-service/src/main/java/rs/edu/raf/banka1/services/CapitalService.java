package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Capital;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.ListingType;

public interface CapitalService {
    Capital createCapitalForBankAccount(BankAccount bankAccount, Currency currency, Double total, Double reserved);
    Capital createCapitalForListing(ListingType listingType, Long listingId, Double total, Double reserved);
    Capital getCapitalByCurrencyCode(String currencyCode);
    Capital getCapitalByListingIdAndType(Long listingId, ListingType type);
    void reserveBalance(String currencyCode, Double amount);
    void commitReserved(String currencyCode, Double amount);
    void releaseReserved(String currencyCode, Double amount);
    void addBalance(String currencyCode, Double amount);
    void removeBalance(String currencyCode, Double amount);
    void reserveBalance(Long listingId, ListingType type, Double amount);
    void commitReserved(Long listingId, ListingType type, Double amount);
    void releaseReserved(Long listingId, ListingType type, Double amount);
    void addBalance(Long listingId, ListingType type, Double amount);
    void removeBalance(Long listingId, ListingType type, Double amount);

}
