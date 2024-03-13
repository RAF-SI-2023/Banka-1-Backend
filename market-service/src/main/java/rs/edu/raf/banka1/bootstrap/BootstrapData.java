package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.mapper.ListingMapper;
import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.model.ListingModel;
import rs.edu.raf.banka1.services.ListingService;

import java.util.List;

@Component
public class BootstrapData implements CommandLineRunner {

    @Autowired
    private ListingService listingService;

    @Autowired
    private ListingMapper listingMapper;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Loading Data...");

//        fetchiing and bootstrapping listing data

//        call it only from time to time to update json because api isn't free and we need it only once
//        listingService.initializeListings();
//        loading data from json and fetches data from other API (professor will give us API token for this one)
        List<ListingModel> listingModels = listingService.fetchListings();
//        use this to update or initialize database with fresh data
        listingService.updateAllListingsDatabase(listingModels);

//        fetching and bootstrapping listing history data (not recommended as each listing generates 100 history records and we have around 3500 listings)
//        List<ListingHistoryModel> listingHistoryModels = listingService.fetchAllListingsHistory();
//        so better alternative is to fetch history only for given listing when needed
        List<ListingHistoryModel> oneListingHistoryList = listingService.fetchSingleListingHistory("AAPL");
        listingService.addAllListingsToHistory(oneListingHistoryList);

      System.out.println("Data loaded!");
    }
}
