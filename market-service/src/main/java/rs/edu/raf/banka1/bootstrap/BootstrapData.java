package rs.edu.raf.banka1.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.dtos.CurrencyDto;
import rs.edu.raf.banka1.services.CurrencyService;
import rs.edu.raf.banka1.services.ForexService;
import rs.edu.raf.banka1.services.ListingStockService;
import rs.edu.raf.banka1.services.OptionsService;
import rs.edu.raf.banka1.services.ExchangeService;
import rs.edu.raf.banka1.utils.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static rs.edu.raf.banka1.utils.Constants.maxStockListings;
import static rs.edu.raf.banka1.utils.Constants.maxStockListingsHistory;

@Component
@AllArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final CurrencyService currencyService;

    @Autowired
    private ListingStockService listingStockService;

    private ForexService forexService;

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private final ExchangeService exchangeService;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Loading Data...");
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        executorService.submit(() -> {
            List<CurrencyDto> currencyList = loadCurrencies();
            currencyService.addCurrencies(currencyList);
            System.out.println("Currency Data Loaded!");
        });

        // Since JSON symbols are available in repo, and the API key needs to be replaced or paid,
        // we only need to call the function below every once in a while
        // listingStockService.generateJSONSymbols();

        // STOCK
        // Populate stock and stock history
        executorService.submit(() -> {
            // EXCHANGE
            exchangeService.seedDatabase();
            System.out.println("Exchange data loaded!");

            // STOCK
            List<ListingStock> listingStocks = listingStockService.fetchNListingStocks(maxStockListings);
            listingStockService.addAllListingStocks(listingStocks);
            List<ListingHistory> listingHistories = listingStockService.fetchNListingsHistory(maxStockListingsHistory);
            listingStockService.addAllListingsToHistory(listingHistories);
            System.out.println("Stock Data Loaded!");
        });
        ////////////////////////////////////////////////////////////////
        // FOREX
        executorService.submit(() -> {

            // get initial forex names data (do it only once)
            List<ListingForex> listingForexList = forexService.initializeForex();

            // update forex prices (will be called every 15 minutes or so)
            // Warning: for the testing purposes I only update first 10 forex pairs (API limitations)
            // Warning: in the production we should update all forex pairs
            List<ListingForex> updated = forexService.updateAllPrices(listingForexList.subList(0, 10));
            // saves forex data to database (only after update)
            // because update uses other API which doesn't support all forex names, so we need to save only available forexs
            // first you need to save forexes and after that histories because histories need forex ids from database
            forexService.saveAllForexes(updated);

            // add forexes histories to database
            // Warning: agreement was to add just histories for 10 forexes
            List<ListingHistory> histories = forexService.getAllForexHistories(updated);
            listingStockService.addAllListingsToHistory(histories);
            System.out.println("Forex Data Loaded!");
        });
        ////////////////////////////////////////////////////////////////

        executorService.submit(() -> {
            optionsService.fetchOptions();
            System.out.println("Options Data Loaded!");
        });

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
