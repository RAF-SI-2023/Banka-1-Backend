package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Capital;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.ListingType;

public interface CapitalService {
    Capital createCapitalForBankAccount(BankAccount bankAccount, Currency currency, Double total, Double reserved);
    Capital createCapitalForListing(ListingType listingType, Long listingId, Double total, Double reserved);
}
