package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;

import java.util.List;
import java.util.Optional;

public interface ListingStockService {
    List<ListingStock> getAllStocks();
    Optional<ListingStock> findByTicker(String ticker);
    Optional<ListingStock> findById(Long id);
    String getWorkingTimeById(Long id);
    List<ListingHistory> getListingHistoriesByTimestamp(String ticker, Integer from, Integer to);
    List<ListingHistory> getListingHistoriesByTimestamp(Long id, Integer from, Integer to);
}
