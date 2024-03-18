package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingForex;

import java.util.List;

public interface ForexService {
    List<ListingForex> initializeForex();

    List<ListingForex> fetchAllForexPairs(String forex_place);

    List<ListingForex> updateAllPrices(List<ListingForex> listingForexList);

    ListingForex getUpdatedForex(ListingForex listingForex);

    void saveAllForexes(List<ListingForex> listingForexList);


}
