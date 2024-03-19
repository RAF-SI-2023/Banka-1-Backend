package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingHistoryModel;

import java.util.ArrayList;
import java.util.List;
/*
public class ListingHistoryServiceImpl implements ListingHistoryService{
    @Override
    public List<ListingHistoryModel> fetchAllListingsHistory() {
        try{
            List<ListingModel> listingModels = fetchListingsName();
            List<ListingHistoryModel> listingHistoryModels = new ArrayList<>();
            for (ListingModel lmodel : listingModels)
                listingHistoryModels.addAll(fetchSingleListingHistory(lmodel.getTicker()));

            return listingHistoryModels;
        }catch (Exception e) {
            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    @Override
    public List<ListingHistoryModel> fetchSingleListingHistory(String ticker) {
        return null;
    }

    @Override
    public int addListingToHistory(ListingHistoryModel listingHistoryModel) {
        return 0;
    }

    @Override
    public int addAllListingsToHistory(List<ListingHistoryModel> listingHistoryModels) {
        return 0;
    }
}
*/