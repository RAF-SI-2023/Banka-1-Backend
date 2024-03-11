package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.model.ListingModel;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;
import rs.edu.raf.banka1.repositories.ListingRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ListingServiceImpl implements ListingService{
    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private ListingHistoryRepository listingHistoryRepository;

    @Override
    public List<ListingModel> fetchListings() {
        System.out.println("Fetching data...");
        ListingModel listingModel = new ListingModel();
        listingModel.setTicker("AAPL");
        listingModel.setName("Apple Inc.");
        listingModel.setExchange("NASDAQ");
        listingModel.setPrice(125.0);
        listingModel.setAsk(125.1);
        listingModel.setBid(124.9);
        listingModel.setChanged(0.5);
        listingModel.setVolume(1000000);
        listingModel.setLastRefresh(java.time.LocalDateTime.now());

        return List.of(listingModel, listingModel);
    }

    @Override
    public void updateAllListings(List<ListingModel> listings) {
        listingRepository.saveAll(listings);
    }

//    if we want to add listing to history for certain day, if we already have saved it, we should just update it
    @Override
    public int addListingToHistory(ListingHistoryModel listingHistoryModel) {
        Optional<ListingHistoryModel> listingHistoryModelOptional = listingHistoryRepository.findByTickerAndDate(listingHistoryModel.getTicker(), listingHistoryModel.getDate());
        if (listingHistoryModelOptional.isPresent()) {
            ListingHistoryModel listingHistoryModel1 = listingHistoryModelOptional.get();
            listingHistoryModel1.setPrice(listingHistoryModel.getPrice());
            listingHistoryModel1.setAsk(listingHistoryModel.getAsk());
            listingHistoryModel1.setBid(listingHistoryModel.getBid());
            listingHistoryModel1.setChanged(listingHistoryModel.getChanged());
            listingHistoryModel1.setVolume(listingHistoryModel.getVolume());

            listingHistoryRepository.save(listingHistoryModel1);
            return 0;
        }else{
            listingHistoryRepository.save(listingHistoryModel);
            return 1;
        }
    }

    @Override
    public int addAllListingsToHistory(List<ListingHistoryModel> listingHistoryModels) {

        return listingHistoryModels.stream().mapToInt(this::addListingToHistory).sum();
    }


}
