package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.*;
import rs.edu.raf.banka1.dtos.PublicCapitalDto;
import rs.edu.raf.banka1.model.*;

import java.util.List;

public interface CapitalService {
    Capital createCapital(ListingType listingType, Long listingId, Double total, Double reserved, BankAccount bankAccount);
    Capital getCapitalByListingIdAndTypeAndBankAccount(Long listingId, ListingType type, BankAccount bankAccount);
    void reserveBalance(Long listingId, ListingType type, BankAccount bankAccount, Double amount);
    void commitReserved(Long listingId, ListingType type, BankAccount bankAccount, Double amount);
    void releaseReserved(Long listingId, ListingType type, BankAccount bankAccount, Double amount);
    void addBalance(Long listingId, ListingType type, BankAccount bankAccount, Double amount);
    void removeBalance(Long listingId, ListingType type, BankAccount bankAccount, Double amount);
//    Double getCapital(String accountNumber);
    Double estimateBalanceForex(Long forexId);
    Double estimateBalanceFuture(Long futureId);
    Double estimateBalanceStock(Long stockId);
    List<CapitalProfitDto> getListingCapitalsQuantity();
    boolean hasEnoughCapitalForOrder(MarketOrder order);
    List<PublicCapitalDto> getAllPublicStockCapitals();
    List<PublicCapitalDto> getAllPublicListingCapitals();
    Boolean addToPublicCapital(User userPrincipal, AddPublicCapitalDto setPublicCapitalDto);
    void removeFromPublicCapital(Long listingId, ListingType listingType, BankAccount bankAccount, Double amount);
    CapitalDto getCapitalForStockId(Long stockId);
    CapitalDto getCapitalForForexId(Long forexId);
}
