package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.Listing;
import rs.edu.raf.banka1.model.ListingHistory;

import java.util.List;

public interface ListingService {
//    only do it once and store it into json file (because api isn't free and we need it once anyway)
    void initializeListings();

//    fetch all listings based on json file
    List<Listing> fetchListings();

//    updated listing in the database
    void updateAllListingsDatabase(List<Listing> listings);

//    fetch all listings-history based on json file
    List<ListingHistory> fetchAllListingsHistory();

//    fetch single listing-history
    List<ListingHistory> fetchSingleListingHistory(String ticker);

//    return 1 if it's a new listing, 0 if it's just an update
    int addListingToHistory(ListingHistory listingHistory);

//    returns how many new listings were added
    int addAllListingsToHistory(List<ListingHistory> listingHistories);

//    returns Listing histories between two timestamps
    List<ListingHistory> getListingHistoriesByTimestamp(long id, Integer from, Integer to);
}
