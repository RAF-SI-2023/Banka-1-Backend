package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;

import java.util.List;
import java.util.Optional;

public interface ListingStockService {
    void generateJSONSymbols();
    List<ListingStock> fetchNListingStocks(int n);
    List<ListingStock> getAllStocks();

    List<ListingHistory> fetchNListingsHistory(int n);

    //    returns how many new listings were added
    int addAllListingStocks(List<ListingStock> listingStocks);

    //    return 1 if it's a new listing, 0 if it's just an update
    int addListingStock(ListingStock listingStock);

    //    fetch single listing-history
    List<ListingHistory> fetchSingleListingHistory(String ticker);

    //    return 1 if it's a new listing, 0 if it's just an update
    int addListingToHistory(ListingHistory listingHistory);

    //    returns how many new listings were added
    int addAllListingsToHistory(List<ListingHistory> listingHistories);

    Optional<ListingStock> findByTicker(String ticker);
    Optional<ListingStock> findById(Long id);

    List<ListingHistory> getListingHistoriesByTimestamp(String ticker, Integer from, Integer to);

    List<ListingHistory> getListingHistoriesByTimestamp(Long id, Integer from, Integer to);
}
