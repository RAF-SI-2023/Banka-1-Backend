package rs.edu.raf.banka1.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.dtos.CurrencyDto;
import rs.edu.raf.banka1.services.CurrencyService;
import rs.edu.raf.banka1.services.ExchangeService;
import rs.edu.raf.banka1.services.ListingStockService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static rs.edu.raf.banka1.utils.Constants.maxStockListings;
import static rs.edu.raf.banka1.utils.Constants.maxStockListingsHistory;

@Component
@AllArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final CurrencyService currencyService;
    @Autowired
    private ListingStockService listingStockService;

    @Autowired
    private ExchangeService exchangeService;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Loading Data...");

        exchangeService.seedDatabase();
        System.out.println("Exchange data loaded!");

        List<CurrencyDto> currencyList = loadCurrencies();
        currencyService.addCurrencies(currencyList);
        System.out.println("Currency Data Loaded!");

//        Since JSON symbols are available in repo, and the API key needs to be replaced or paid,
//        we only need to call the function below every once in a while
//        listingStockService.generateJSONSymbols();

        // Populate stock and stock history
        List<ListingStock> listingStocks = listingStockService.fetchNListingStocks(maxStockListings);
        listingStockService.addAllListingStocks(listingStocks);
        List<ListingHistory> listingHistories = listingStockService.fetchNListingsHistory(maxStockListingsHistory);
        listingStockService.addAllListingsToHistory(listingHistories);

        System.out.println("All Data loaded!");
    }

    public List<CurrencyDto> loadCurrencies() {
        List<CurrencyDto> currencyList = new ArrayList<>();
        String csvFile = "market-service/src/main/resources/physical_currency_list.csv";
        String line = "";
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
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
