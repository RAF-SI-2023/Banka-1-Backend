package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingHistory;

import java.util.List;
import java.util.Optional;

public interface ForexService {
    List<ListingForex> getAllForexes();

    ListingForex getForexByTicker(String ticker);

    List<ListingHistory> getListingHistoriesByTimestamp(Long id, Integer from, Integer to);

    Optional<ListingForex> findById(Long id);
}
