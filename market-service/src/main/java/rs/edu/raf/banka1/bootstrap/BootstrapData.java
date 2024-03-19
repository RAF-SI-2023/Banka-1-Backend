package rs.edu.raf.banka1.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.mapper.ListingMapper;
import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.dtos.CurrencyDto;
import rs.edu.raf.banka1.services.CurrencyService;
import rs.edu.raf.banka1.services.ForexService;
import rs.edu.raf.banka1.services.ListingService;
import rs.edu.raf.banka1.utils.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final CurrencyService currencyService;

    @Autowired
    private ListingService listingService;

    @Autowired
    private ListingMapper listingMapper;

    private ForexService forexService;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Loading Data...");
//
//        List<CurrencyDto> currencyList = loadCurrencies();
//        currencyService.addCurrencies(currencyList);
//        System.out.println("Currency Data Loaded!");

//        ////////////////////////////////////////////////////////////////
////        LISTINGS
//
////        call it only from time to time to update json because api isn't free and we need it only once
////        listingService.initializeListings();
////        loading data from json and fetches data from other API (professor will give us API token for this one)
//        List<Listing> listings = listingService.fetchListings();
////        use this to update or initialize database with fresh data
//        listingService.updateAllListingsDatabase(listings);
//
////        fetching and bootstrapping listing history data (not recommended as each listing generates 100 history records and we have around 3500 listings)
////        List<ListingHistoryModel> listingHistoryModels = listingService.fetchAllListingsHistory();
////        so better alternative is to fetch history only for given listing when needed
//        List<ListingHistory> oneListingHistoryList = listingService.fetchSingleListingHistory("AAPL");
//        listingService.addAllListingsToHistory(oneListingHistoryList);
//        ////////////////////////////////////////////////////////////////


        ////////////////////////////////////////////////////////////////
//        FOREX

//        get initial forex names data (do it only once)
        List<ListingForex> listingForexList = forexService.initializeForex();

//        update forex prices (will be called every 15 minutes or so)
//        Warning: for the testing purposes I only update first 10 forex pairs (API limitations)
//        Warning: in the production we should update all forex pairs
        List<ListingForex> updated = forexService.updateAllPrices(listingForexList.subList(0, 10));
//        saves forex data to database (only after update)
//       because update uses other API which doesn't support all forex names, so we need to save only available forexs


//        add forexes histories to database
//        Warning: agreement is to add just histories for 10 forexes
        List<ListingHistory> histories = forexService.getAllForexHistories(updated);
        forexService.saveAllForexes(updated);
        listingService.addAllListingsToHistory(histories);
        ////////////////////////////////////////////////////////////////
        System.out.printf("Updated: " + updated.size());
        System.out.println("Histories: " + histories.size());
        System.out.println("All Data loaded!");
    }

    public List<CurrencyDto> loadCurrencies() {
        List<CurrencyDto> currencyList = new ArrayList<>();
        String line = "";
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(Constants.currencyFilePath))) {
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] currencyData = line.split(csvSplitBy);
                if (currencyData.length == 2) {
                    String code = currencyData[0].trim();
                    String name = currencyData[1].trim();
                    currencyList.add(new CurrencyDto(name, code));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currencyList;
    }
}
