package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.model.ListingStock;

import java.util.List;

public interface ListingStockService {
    void generateJSONSymbols();
    void populateListingStocks();
    void updateValuesForListingStock(ListingStock listingStock);

 //   List<ListingHistoryModel> fetchAllListingsHistory();
  //  void initializeStock();
   // void getStockData(String symbol);
}
