package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.model.ListingModel;

import java.util.List;

public interface ListingService {
//    only do it once and store it into json file (because api isn't free and we need it once anyway)
    void initializeListings();

    List<ListingModel> fetchListings();
    void updateAllListings(List<ListingModel> listings);

//    return 1 if it's a new listing, 0 if it's just an update
    int addListingToHistory(ListingHistoryModel listingHistoryModel);

//    returns how many new listings were added
    int addAllListingsToHistory(List<ListingHistoryModel> listingHistoryModels);
}