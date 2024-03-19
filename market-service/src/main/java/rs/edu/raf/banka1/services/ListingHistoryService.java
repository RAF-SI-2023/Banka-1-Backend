package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingHistoryModel;

import java.util.List;

public interface ListingHistoryService {
    List<ListingHistoryModel> fetchAllListingsHistory();

    //    fetch single listing-history
    List<ListingHistoryModel> fetchSingleListingHistory(String ticker);

    //    return 1 if it's a new listing, 0 if it's just an update
    int addListingToHistory(ListingHistoryModel listingHistoryModel);

    //    returns how many new listings were added
    int addAllListingsToHistory(List<ListingHistoryModel> listingHistoryModels);
}
