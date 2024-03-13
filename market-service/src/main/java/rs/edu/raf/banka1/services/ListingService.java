package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.model.ListingModel;

import java.util.List;

public interface ListingService {
//    only do it once and store it into json file (because api isn't free and we need it once anyway)
    void initializeListings();

//    fetch all listings based on json file
    List<ListingModel> fetchListings();

//    updated listing in the database
    void updateAllListingsDatabase(List<ListingModel> listings);

//    fetch all listings-history based on json file
    List<ListingHistoryModel> fetchAllListingsHistory();

//    fetch single listing-history
    List<ListingHistoryModel> fetchSingleListingHistory(String ticker);

//    return 1 if it's a new listing, 0 if it's just an update
    int addListingToHistory(ListingHistoryModel listingHistoryModel);

//    returns how many new listings were added
    int addAllListingsToHistory(List<ListingHistoryModel> listingHistoryModels);
}
