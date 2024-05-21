package rs.edu.raf.banka1.cucumber;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rs.edu.raf.banka1.bootstrap.BootstrapData;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.model.dtos.CurrencyDto;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.services.*;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FetchingServiceSteps {

    private StockRepository stockRepository;
    private ListingHistoryRepository listingHistoryRepository;
    private FutureRepository futureRepository;
    private CurrencyRepository currencyRepository;
    private ExchangeRepository exchangeRepository;
    private InflationRepository inflationRepository;
    private ForexRepository forexRepository;
    private OptionsRepository optionsRepository;

    @Autowired
    private ListingStockService listingStockService;
    @Autowired
    private FuturesService futuresService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private BootstrapData bootstrapData;
    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private ForexService forexService;
    @Autowired
    private OptionsService optionsService;

    public FetchingServiceSteps(StockRepository stockRepository,ListingHistoryRepository listingHistoryRepository,
                                FutureRepository futureRepository, CurrencyRepository currencyRepository,
                                ExchangeRepository exchangeRepository, InflationRepository inflationRepository,
                                ForexRepository forexRepository,OptionsRepository optionsRepository) {
        this.stockRepository = stockRepository;
        this.listingHistoryRepository = listingHistoryRepository;
        this.futureRepository = futureRepository;
        this.currencyRepository = currencyRepository;
        this.exchangeRepository = exchangeRepository;
        this.inflationRepository = inflationRepository;
        this.forexRepository = forexRepository;
        this.optionsRepository = optionsRepository;
    }

    @Given("the fetching service is running")
    public void serviceRunning(){

    }

    //stocks

    @When("I fetch N listing stocks from an external API")
    public void fetchingStocks(){
        List<ListingStock> fetchedListingStocks = listingStockService.fetchNListingStocks(12);
    }

    @Then("the fetched listing stocks should be stored in the database")
    public void stocksInDatabase(){
        List<ListingStock> savedStocks = stockRepository.findAll();
        assertEquals(12,savedStocks.size());
    }

    @When("I fetch listing stock by ticker {string}")
    public void fetchByTicker(String ticker){
        listingStockService.findByTicker(ticker);
    }

    @Then("the fetched listing stock {string} should be stored in the database")
    public void singleStockInDatabase(String ticker){
        Optional<ListingStock> stocks = stockRepository.findByTicker(ticker);
        if(stocks.isEmpty()){
            fail("This stock doesnt exist " + ticker);
        }
        assertNotNull(stocks);
    }

    //stock history

    @When("I fetch N listing histories")
    public void fetchNListingHistories(){
        List<ListingHistory> histories = listingStockService.fetchNListingsHistory(2);
    }

    @Then("the fetched listing histories should be stored in the database")
    public void historiesInDatabase(){
        List<ListingHistory> histories = listingHistoryRepository.findAll();
        assertFalse(histories.isEmpty());
    }

    @When("I fetch single listing history by ticker {string} from an external API")
    public void singleListingHistory(String ticker){
        List<ListingHistory> histories = listingStockService.fetchSingleListingHistory(ticker);
    }

    @Then("the fetched listing history for {string} should be stored in the database")
    public void singleHistoryDatabase(String ticker){
        Optional<ListingHistory> history = listingHistoryRepository.findByTicker(ticker);
        if(history.isEmpty()){
            fail("Stock history empty for stock " + ticker);
        }
        assertFalse(history.isEmpty());
    }

    //options
    @When("I fetch options from an external API")
    public void fetchOptions(){
        optionsService.fetchOptions();
    }

    @Then("the fetched options should be stored in the database")
    public void optionsDatabase(){
        assertFalse(optionsRepository.findAll().isEmpty());
    }

    @When("I fetch options by ticker {string} from an external API")
    public void fetchOptionByTicker(String ticker){
        optionsService.getOptionsByTicker(ticker);
    }

    @Then("the fetched options by ticker {string} should be stored in the database")
    public void fetchOptionByTickerDatabase(String ticker){
       Optional<List<OptionsModel>> options = optionsRepository.findByTicker(ticker);
       assertFalse(options.isEmpty());

    }
    //futures

    @When("I fetch N futures from an external API")
    public void fetchNFutures(){
        futuresService.fetchNFutures(3);
    }

    @Then("fetched N futures should be stored in the database")
    public void fetchedNFuturesDatabase(){
        assertFalse(futureRepository.findAll().isEmpty());
    }

    @When("I fetch N future histories from an external API")
    public void fetchNFutureHistories(){
        ListingFuture future = new ListingFuture();
        future.setAlternativeTicker("APPN");
        futuresService.fetchNFutureHistories(List.of(future),2);
    }

    @Then("fetched N future histories should be stored in the database")
    public void fetchedNFutureHistoriesDatabase(){
        List<ListingHistory> futures = listingHistoryRepository.findAll();
        assertFalse(futures.isEmpty());
    }

    //forex

    @When("I fetch forex")
    public void fetchForex(){
        List<ListingForex> forexes = forexService.initializeForex();
    }

    @Then("the fetched forexes should be stored in the database")
    public void forexDatabase(){
        assertFalse(forexRepository.findAll().isEmpty());
    }

    @When("I fetch single forex history")
    public void fetchSingleForexHistory(){
        ListingForex forex = new ListingForex();
        forex.setBaseCurrency("EUR");
        forex.setQuoteCurrency("AUD");
        forexService.getForexHistory(forex);
    }

    @Then("the fetched single forex history should be stored in the database")
    public void singleForexHistoryDatabase(){
        assertNotNull(listingHistoryRepository.findByTicker("EUR/AUD"));
    }

    //currency

    @When("I fetch currency")
    public void currencyFetch(){
        // Call the method to load currencies
        List<CurrencyDto> currencyList = bootstrapData.loadCurrencies();
        currencyService.addCurrencies(currencyList);
    }

    @Then("currency and inflation should be stored in the database")
    public void currencyDatabase(){
        assertFalse(currencyRepository.findAll().isEmpty());
        assertFalse(inflationRepository.findAll().isEmpty());
    }

    //exchange

    @When("I fetch exchange")
    public void fetchExchange(){
        exchangeService.seedDatabase();
    }

    @Then("the fetched exchange should be stored in the database")
    public void exchangeDatabase(){
        assertFalse(exchangeRepository.findAll().isEmpty());
    }
}



