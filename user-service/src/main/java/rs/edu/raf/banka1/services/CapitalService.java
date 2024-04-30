package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.*;
import rs.edu.raf.banka1.dtos.PublicCapitalDto;
import rs.edu.raf.banka1.model.*;

import java.util.List;

public interface CapitalService {
    Capital createCapital(ListingType listingType, Long listingId, Double total, Double reserved, BankAccount bankAccount);
    Capital getCapitalByListingIdAndType(Long listingId, ListingType type);
    void reserveBalance(Long listingId, ListingType type, Double amount);
    void commitReserved(Long listingId, ListingType type, Double amount);
    void releaseReserved(Long listingId, ListingType type, Double amount);
    void addBalance(Long listingId, ListingType type, Double amount);
    void removeBalance(Long listingId, ListingType type, Double amount);
    Double getCapital(String accountNumber);
    Double estimateBalanceForex(Long forexId);
    Double estimateBalanceFuture(Long futureId);
    Double estimateBalanceStock(Long stockId);
    List<CapitalProfitDto> getListingCapitalsQuantity();
    boolean hasEnoughCapitalForOrder(MarketOrder order);
    List<PublicCapitalDto> getAllPublicStockCapitals();
    List<PublicCapitalDto> getAllPublicListingCapitals();
    Boolean addToPublicCapital(Customer userPrincipal, AddPublicCapitalDto setPublicCapitalDto);
    CapitalDto getCapitalForStockId(Long stockId);
    CapitalDto getCapitalForForexId(Long forexId);
}
