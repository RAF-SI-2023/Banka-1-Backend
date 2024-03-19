package rs.edu.raf.banka1.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingHistoryModel;
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

        //        call it only from time to time to update json because api isn't free and we need it only once
        //this is used for all listing bases
//-------------------------------------NIKADA VISE NE POZIVATI OVO--------------------------------------------
        //listingStockService.generateJSONSymbols();
///---------------------------------OVO SE POZIVA ZA POPUNJAVANJE STOKOVA----------------------------------
       // listingStockService.populateListingStocks();

//        use this to update or initialize database with fresh data

//        fetching and bootstrapping listing history data (not recommended as each listing generates 100 history records and we have around 3500 listings)
   //-----------------------------!poziv za history za 10 stokova-----------------------------------------------
      //  List<ListingHistoryModel> listingHistoryModels = listingStockService.fetchAllListingsHistory();
//        so better alternative is to fetch history only for given listing when needed
        //List<ListingHistoryModel> oneListingHistoryList = listingStockService.fetchSingleListingHistory("AAPL");

     //   listingStockService.addAllListingsToHistory(listingHistoryModels);

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
