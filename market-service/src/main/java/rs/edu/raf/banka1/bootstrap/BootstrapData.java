package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.mapper.ListingMapper;
import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.model.ListingModel;
import rs.edu.raf.banka1.repositories.ListingRepository;
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
        System.out.println(listingService.fetchListings().size());
        listingService.updateAllListings(listingService.fetchListings());
        List<ListingHistoryModel> lst = List.of(listingMapper.listingModelToListongHistoryModel(listingService.fetchListings().get(0)), listingMapper.listingModelToListongHistoryModel(listingService.fetchListings().get(0)));
        int res = listingService.addAllListingsToHistory(lst);
        System.out.println(res + " new listings added to history!");
        System.out.println("Data loaded!");
    }
}
