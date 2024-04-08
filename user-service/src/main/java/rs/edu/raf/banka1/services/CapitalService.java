package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.CapitalDto;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Capital;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.ListingType;

import java.util.List;

public interface CapitalService {
    Capital createCapitalForBankAccount(BankAccount bankAccount, Currency currency, Double total, Double reserved);
    Capital createCapitalForListing(ListingType listingType, Long listingId, Double total, Double reserved);
    List<CapitalDto> getCapitalForListing(String accountNumber, ListingType listingType);
    List<CapitalDto> getAllCapitals(String accountNumber);
    Double estimateBalanceForex(String accountNumber, Long forexId);
    Double estimateBalanceFuture(String accountNumber, Long futureId);
    Double estimateBalanceStock(String accountNumber, Long stockId);
}
