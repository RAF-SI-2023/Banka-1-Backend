package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingHistory;

import java.util.List;
import java.util.Optional;

public interface ForexService {
    List<ListingForex> initializeForex();

    List<ListingForex> fetchAllForexPairs(String forex_place);

    List<ListingForex> updateAllPrices(List<ListingForex> listingForexList);

    ListingForex getUpdatedForex(ListingForex listingForex);

    void saveAllForexes(List<ListingForex> listingForexList);

    List<ListingHistory> getForexHistory(ListingForex listingForex);

    List<ListingHistory> getAllForexHistories(List<ListingForex> listingForexList);
}
