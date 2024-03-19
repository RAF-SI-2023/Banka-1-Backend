package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.model.ListingStock;

import java.util.List;

public interface ListingStockService {
    void generateJSONSymbols();
    void populateListingStocks();
    void updateValuesForListingStock(ListingStock listingStock);
    List<ListingStock> getAllStocks();

    List<ListingHistoryModel> fetchAllListingsHistory();

    //    fetch single listing-history
    List<ListingHistoryModel> fetchSingleListingHistory(String ticker);

    //    return 1 if it's a new listing, 0 if it's just an update
    int addListingToHistory(ListingHistoryModel listingHistoryModel);

    //    returns how many new listings were added
    int addAllListingsToHistory(List<ListingHistoryModel> listingHistoryModels);

}
