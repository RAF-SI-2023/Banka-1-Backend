package rs.edu.raf.banka1.bootstrap;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;
import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.dtos.CurrencyDto;
import rs.edu.raf.banka1.services.*;
import rs.edu.raf.banka1.utils.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static rs.edu.raf.banka1.utils.Constants.*;

@Component
@AllArgsConstructor
public class BootstrapData implements CommandLineRunner {
    private final CurrencyService currencyService;
    private final ListingStockService listingStockService;
    private final ForexService forexService;
    private final OptionsService optionsService;
    private final FuturesService futuresService;
    private final ExchangeService exchangeService;

    @Override
    public void run(String... args) throws Exception {
        Logger.info("Loading Data...");

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        executorService.submit(() -> {
            List<ListingFuture> listingFutures = futuresService.fetchNFutures(maxFutures);
            List<ListingHistory> futureHistories = futuresService.fetchNFutureHistories(listingFutures, maxFutureHistories);
            futuresService.addAllFutures(listingFutures);
            listingStockService.addAllListingsToHistory(futureHistories);
            Logger.info("Futures data loaded!");
        });

        executorService.submit(() -> {
            List<CurrencyDto> currencyList = loadCurrencies();
            currencyService.addCurrencies(currencyList);
            Logger.info("Currency Data Loaded!");
        });

        // Since JSON symbols are available in repo, and the API key needs to be replaced or paid,
        // we only need to call the function below every once in a while
        // listingStockService.generateJSONSymbols();

        exchangeService.seedDatabase();
        Logger.info("Exchange data loaded!");
        // STOCK
        // Populate stock and stock history
        executorService.submit(() -> {
            // STOCK
            List<ListingStock> listingStocks = listingStockService.fetchNListingStocks(maxStockListings);
            listingStockService.addAllListingStocks(listingStocks);
            List<ListingHistory> listingHistories = listingStockService.fetchNListingsHistory(maxStockListingsHistory);
            listingStockService.addAllListingsToHistory(listingHistories);
            Logger.info("Stock Data Loaded!");
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
            Logger.info("Forex Data Loaded!");
        });
        ////////////////////////////////////////////////////////////////

        executorService.submit(() -> {
            optionsService.fetchOptions();
            Logger.info("Options Data Loaded!");
        });

        Logger.info("All Data loaded!");
    }

    public List<CurrencyDto> loadCurrencies() {
        List<CurrencyDto> currencyList = new ArrayList<>();
        String line = "";
        String csvSplitBy = ",";

//        try (BufferedReader br = new BufferedReader(new FileReader(Constants.currencyFilePath))) {
        BufferedReader br = null;

        try {
            Resource resource = new ClassPathResource(currencyFilePath, this.getClass().getClassLoader());

            InputStream in = resource.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            br = new BufferedReader(inputStreamReader);

            System.out.println("OKEJ");

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
            Logger.error("[BootstrapData] Caught exception " + e.getMessage());
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
            } catch (IOException e) {
                Logger.error("[-] Error occured when trying to close buffered reader " + e.getMessage());
            }
        }

        return currencyList;
    }
}
