package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.classgraph.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pmw.tinylog.Logger;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import rs.edu.raf.banka1.Banka1Application;
import rs.edu.raf.banka1.mapper.StockMapper;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.entities.Country;
import rs.edu.raf.banka1.model.entities.Exchange;
import rs.edu.raf.banka1.model.entities.Holiday;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.utils.Requests;


import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

//@RunWith(SpringRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class ListingStockServiceImplTest {

    private ListingHistoryRepository listingHistoryRepository;

    private StockRepository stockRepository;

    private CountryRepository countryRepository;

    private HolidayRepository holidayRepository;

    private ExchangeRepository exchangeRepository;

    private StockMapper stockMapper;

    private ObjectMapper objectMapper;
    @Mock
    private Resource resource;

    private Requests requests;

    private ListingStockServiceImpl listingStockService;

    private ListingStock stockAAPL;
    private ListingStock stockMSFT;
    private ListingStock stockDT;
    private List<ListingStock> stocks;
    private Exchange exchangeDT;
    private Country countryUS;

    private List<ListingHistory> lst = new ArrayList<>();
    private ListingHistory model1;
    private ListingHistory model2;
    private long date;

    @BeforeEach
    public void setUp(){

        MockitoAnnotations.openMocks(this);

        listingHistoryRepository = mock(ListingHistoryRepository.class);
        stockRepository = mock(StockRepository.class);
        countryRepository = mock(CountryRepository.class);
        holidayRepository = mock(HolidayRepository.class);
        exchangeRepository = mock(ExchangeRepository.class);
        stockMapper = mock(StockMapper.class);
        listingStockService = new ListingStockServiceImpl();
        listingStockService.setStockRepository(stockRepository);
        listingStockService.setCountryRepository(countryRepository);
        listingStockService.setHolidayRepository(holidayRepository);
        listingStockService.setListingHistoryRepository(listingHistoryRepository);
        listingStockService.setExchangeRepository(exchangeRepository);
        listingStockService.setStockMapper(stockMapper);

        objectMapper = new ObjectMapper();
        requests = mock(Requests.class);
        listingStockService.setRequests(requests);

        // stock data
        stockAAPL = new ListingStock();
        stockAAPL.setTicker("AAPL");
        stockAAPL.setPrice(100.0);

        stockMSFT = new ListingStock();
        stockMSFT.setTicker("MSFT");
        stockMSFT.setPrice(200.0);

        SimpleDateFormat hoursDateFormat = new SimpleDateFormat("HH:mm:ss");
        countryUS = new Country();
        countryUS.setISOCode("US");
        countryUS.setId(113L);
        countryUS.setTimezoneOffset(0);
        try {
            countryUS.setOpenTime(new java.util.Date(hoursDateFormat.parse("08:00:00").getTime()));
            countryUS.setCloseTime(new java.util.Date(hoursDateFormat.parse("18:00:00").getTime()));

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        exchangeDT = new Exchange();
        exchangeDT.setId(2295L);
        exchangeDT.setExchangeName("NEW YORK STOCK EXCHANGE, INC.");
        exchangeDT.setMicCode("XNYS");
        exchangeDT.setCountry(countryUS);

        stockDT = new ListingStock();
        stockDT.setTicker("DT");
        stockDT.setName("Dynatrace Holdings LLC");
        stockDT.setListingType("Stock");
        stockDT.setExchangeName("NEW YORK STOCK EXCHANGE, INC.");
        stockDT.setListingId(1L);
        stockDT.setExchange(exchangeDT);
        stockDT.setVolume(2566374);
        stockDT.setPriceChange(-0.705);
        stockDT.setPrice(45.735);
        stockDT.setOutstandingShares(295999000);
        stockDT.setLow(45.31);
        stockDT.setLastRefresh(1712005154);
        stockDT.setHigh(46.29);
        stockDT.setDividendYield((double) 0);

        stocks = new ArrayList<>();
        stocks.add(stockAAPL);
        stocks.add(stockMSFT);
        stocks.add(stockDT);

        // history data
        model1 = new ListingHistory();
        model1.setTicker("AAPL");
        model1.setDate((int) Date.valueOf("2021-01-01").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
        model1.setPrice(100.0);
        model1.setChanged(2.0);
        model1.setChanged(0.0);
        model1.setVolume(1000);

        model2 = new ListingHistory();
        model2.setTicker("MSFT");
        model2.setDate((int) Date.valueOf("2021-01-01").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
        model2.setPrice(100.0);
        model2.setChanged(2.0);
        model2.setChanged(0.0);
        model2.setVolume(1000);

        lst.add(model1);
        lst.add(model2);

        date = Date.valueOf("2021-01-01").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    @Test
    public void testGetWorkingTimeById_StockNotFound() {
        Long testId = 1L;

        when(stockRepository.findById(testId)).thenReturn(Optional.empty());

        String result = listingStockService.getWorkingTimeById(testId);
        assertEquals("Stock not found", result);
    }

    @Test
    public void testGetWorkingTimeById_CountryNotFound() {
        Long testId = 1L;

        ListingStock mockListingStock = stockDT;
        mockListingStock.setExchange(exchangeDT);
        when(stockRepository.findById(testId)).thenReturn(Optional.of(mockListingStock));

        when(countryRepository.findById(anyLong())).thenReturn(Optional.empty());

        String result = listingStockService.getWorkingTimeById(testId);
        assertEquals("Country not found", result);
    }

    private final Long testId = 1L;

    @Test
    public void testGetWorkingTimeById_ClosedForHoliday() {
        Holiday holiday = new Holiday();
        holiday.setCountry(countryUS);
        holiday.setDate(new java.util.Date());
        holiday.setId(1000L);

        when(stockRepository.findById(testId)).thenReturn(Optional.of(stockDT));

        when(countryRepository.findById(anyLong())).thenReturn(Optional.of(countryUS));

        when(holidayRepository.findByCountryId(any())).thenReturn(
                Optional.of(Collections.singletonList(holiday))
        );

        String result = listingStockService.getWorkingTimeById(testId);
        assertEquals("CLOSED", result);
    }

    @Test
    public void testGetWorkingTimeById_Opened() {

        ListingStock stock;
        Country country;
        Exchange exchange;

        stock = new ListingStock();
        stock.setListingId(1L);

        country = new Country();
        country.setId(1L);
        country.setTimezoneOffset(0);
        // Set openTime and closeTime
        SimpleDateFormat hoursDateFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            country.setOpenTime(new java.util.Date(hoursDateFormat.parse("08:00:00").getTime()));
            country.setCloseTime(new java.util.Date(hoursDateFormat.parse("18:00:00").getTime()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        exchange = new Exchange();
        exchange.setCountry(country);
        stock.setExchange(exchange);


        when(stockRepository.findById(anyLong())).thenReturn(Optional.of(stock));
        when(countryRepository.findById(anyLong())).thenReturn(Optional.of(country));
        when(holidayRepository.findByCountryId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));

        String result = listingStockService.getWorkingTimeById(1L);

        assertEquals("OPENED", result);
    }

    @Test
    public void testGetWorkingTimeById_ClosedOutsideWorkingHours() {
        ListingStock stock;
        Country country;
        Exchange exchange;

        stock = new ListingStock();
        stock.setListingId(10L);

        country = new Country();
        country.setId(10L);
        country.setTimezoneOffset(0);
        // Set openTime and closeTime da su jednaki pa je uvek zatvoren
        SimpleDateFormat hoursDateFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            country.setOpenTime(new java.util.Date(hoursDateFormat.parse("08:00:00").getTime()));
            country.setCloseTime(new java.util.Date(hoursDateFormat.parse("08:00:00").getTime()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        exchange = new Exchange();
        exchange.setCountry(country);
        stock.setExchange(exchange);

        when(stockRepository.findById(anyLong())).thenReturn(Optional.of(stock));
        when(countryRepository.findById(anyLong())).thenReturn(Optional.of(country));
        when(holidayRepository.findByCountryId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));

        // Mock the current time to be outside the working hours
        Clock fixedClock = Clock.fixed(Instant.parse("2022-01-01T20:00:00Z"), ZoneId.of("UTC"));
        listingStockService.setClock(fixedClock);

        String result = listingStockService.getWorkingTimeById(10L);

        assertEquals("CLOSED", result);
    }


    @Test
    public void addListingStockNotPresentTest(){
        when(stockRepository.findByTicker("AAPL")).thenReturn(Optional.empty());
        assertEquals(1, listingStockService.addListingStock(stockAAPL));
    }

    @Test
    public void addListingStockPresentTest(){
        ListingStock updateStock = new ListingStock();
        updateStock.setTicker("AAPL");
        updateStock.setPrice(101.0);
        when(listingStockService.findByTicker("AAPL")).thenReturn(Optional.of(stockAAPL));
        assertEquals(0, listingStockService.addListingStock(updateStock));
    }

    @Test
    public void addAllListingStocksPresentTests(){
        when(listingStockService.findByTicker("AAPL")).thenReturn(Optional.of(stockAAPL));
        when(listingStockService.findByTicker("MSFT")).thenReturn(Optional.of(stockMSFT));
        assertEquals(1, listingStockService.addAllListingStocks(stocks));
    }

    @Test
    public void addAllListingStocksNotPresentTests(){
        when(listingStockService.findByTicker("AAPL")).thenReturn(Optional.empty());
        when(listingStockService.findByTicker("MSFT")).thenReturn(Optional.empty());
        assertEquals(stocks.size(), listingStockService.addAllListingStocks(stocks));
    }

    @Test
    public void addListingToHistoryNotPresentTest(){
        when(listingHistoryRepository.findByTickerAndDate("AAPL", model1.getDate())).thenReturn(Optional.empty());
        assertEquals(1, listingStockService.addListingToHistory(model1));
    }

    @Test
    public void addListingToHistoryPresentTest(){
        ListingHistory listingHistory = new ListingHistory();
        listingHistory.setTicker("AAPL");
        listingHistory.setDate(date);
        listingHistory.setPrice(100.0);
        listingHistory.setChanged(2.0);
        listingHistory.setChanged(0.0);
        listingHistory.setVolume(1000);

        ListingHistory updateModel = new ListingHistory();
        updateModel.setTicker("AAPL");
        updateModel.setDate(date);
        updateModel.setPrice(700.0);
        listingHistory.setChanged(2.0);
        updateModel.setChanged(1.0);
        updateModel.setVolume(10000);

        when(listingHistoryRepository.findByTickerAndDate("AAPL", date)).thenReturn(Optional.of(listingHistory));
        assertEquals(0, listingStockService.addListingToHistory(updateModel));
    }

    @Test
    public void addAllListingsToHistoryEveryPresentTest(){
        when(listingHistoryRepository.findByTickerAndDate("AAPL", model1.getDate())).thenReturn(Optional.of(model1));
        when(listingHistoryRepository.findByTickerAndDate("MSFT", model2.getDate())).thenReturn(Optional.of(model2));
        assertEquals(0, listingStockService.addAllListingsToHistory(lst));
    }

    @Test
    public void addAllListingsToHistoryNothingPresentTest(){
        when(listingHistoryRepository.findByTickerAndDate("AAPL", model1.getDate())).thenReturn(Optional.empty());
        when(listingHistoryRepository.findByTickerAndDate("MSFT", model2.getDate())).thenReturn(Optional.empty());
        assertEquals(lst.size(), listingStockService.addAllListingsToHistory(lst));
    }

    @Test
    public void testGetAllStocks() {
        List<ListingStock> expectedStocks = new ArrayList<>();
        expectedStocks.add(new ListingStock());
        expectedStocks.add(new ListingStock());

        when(stockRepository.findAll()).thenReturn(expectedStocks);

        List<ListingStock> actualStocks = listingStockService.getAllStocks();

        assertEquals(expectedStocks.size(), actualStocks.size());
        for (int i = 0; i < expectedStocks.size(); i++) {
            assertEquals(expectedStocks.get(i), actualStocks.get(i));
        }
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    public void testFetchNStocks() {
        List<ListingStock> allStocks = new ArrayList<>();
        allStocks.add(new ListingStock());
        allStocks.add(new ListingStock());

        when(listingStockService.getAllStocks()).thenReturn(allStocks);

        int n = 2;
        List<ListingStock> fetchedStocks = listingStockService.fetchNStocks(n);

        assertEquals(n, fetchedStocks.size());
    }

    @Test
    public void testFindByTicker() {

        ListingStock listingStock = new ListingStock();
        listingStock.setTicker("AAPL");
        Optional<ListingStock> optionalListingStock = Optional.of(listingStock);

        when(stockRepository.findByTicker("AAPL")).thenReturn(optionalListingStock);

        Optional<ListingStock> result = listingStockService.findByTicker("AAPL");

        assertEquals(optionalListingStock, result);
    }

    @Test
    public void testFindByTicker_TickerNotFound() {
        when(stockRepository.findByTicker("AAPL")).thenReturn(Optional.empty());
        Optional<ListingStock> result = listingStockService.findByTicker("AAPL");
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testFindById() {
        ListingStock listingStock = new ListingStock();
        listingStock.setListingId(1L);
        Optional<ListingStock> optionalListingStock = Optional.of(listingStock);
        when(stockRepository.findById(1L)).thenReturn(optionalListingStock);
        Optional<ListingStock> result = listingStockService.findById(1L);
        assertEquals(optionalListingStock, result);
    }

    @Test
    public void testFindById_IdNotFound() {
        when(stockRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<ListingStock> result = listingStockService.findById(1L);
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testGetListingHistoriesByTimestamp() {

        // Create a list of ListingHistory objects to return for each case
        List<ListingHistory> allHistories = new ArrayList<>();
        List<ListingHistory> historiesBeforeTo = new ArrayList<>();
        List<ListingHistory> historiesAfterFrom = new ArrayList<>();
        List<ListingHistory> historiesBetweenFromTo = new ArrayList<>();

        // Set up the behavior of listingHistoryRepository methods to return the corresponding lists
        when(listingHistoryRepository.getListingHistoriesByTicker("AAPL")).thenReturn(allHistories);
        when(listingHistoryRepository.getListingHistoriesByTickerAndDateBefore("AAPL", 20220101)).thenReturn(historiesBeforeTo);
        when(listingHistoryRepository.getListingHistoriesByTickerAndDateAfter("AAPL", 20220101)).thenReturn(historiesAfterFrom);
        when(listingHistoryRepository.getListingHistoriesByTickerAndDateBetween("AAPL", 20220101, 20220131)).thenReturn(historiesBetweenFromTo);

        // Call the getListingHistoriesByTimestamp method with different parameters
        List<ListingHistory> result1 = listingStockService.getListingHistoriesByTimestamp("AAPL", null, null);
        List<ListingHistory> result2 = listingStockService.getListingHistoriesByTimestamp("AAPL", null, 20220101);
        List<ListingHistory> result3 = listingStockService.getListingHistoriesByTimestamp("AAPL", 20220101, null);
        List<ListingHistory> result4 = listingStockService.getListingHistoriesByTimestamp("AAPL", 20220101, 20220131);

        // Verify that the results are the expected lists of ListingHistory objects
        assertEquals(allHistories, result1);
        assertEquals(historiesBeforeTo, result2);
        assertEquals(historiesAfterFrom, result3);
        assertEquals(historiesBetweenFromTo, result4);
    }

//
//    @Test
//    public void testFetchNListingsHistory() {
//        // Mock the behavior of fetchNStocks
//        List<ListingStock> listingStocks = new ArrayList<>();
//        ListingStock stock1 = mock(ListingStock.class);
//        ListingStock stock2 = mock(ListingStock.class);
//        listingStocks.add(stock1);
//        listingStocks.add(stock2);
//        when(listingStockService.fetchNStocks(anyInt())).thenReturn(listingStocks);
//
//        // Mock the behavior of fetchSingleListingHistory
//        List<ListingHistory> singleStockHistory = new ArrayList<>();
//        ListingHistory history1 = mock(ListingHistory.class);
//        ListingHistory history2 = mock(ListingHistory.class);
//        singleStockHistory.add(history1);
//        singleStockHistory.add(history2);
//        when(listingStockService.fetchSingleListingHistory(stock1.getTicker())).thenReturn(singleStockHistory);
//        when(listingStockService.fetchSingleListingHistory(stock2.getTicker())).thenReturn(singleStockHistory);
//
//        // Call the method to be tested
//        List<ListingHistory> result = listingStockService.fetchNListingsHistory(2);
//
//        // Verify the result
//        assertEquals(4, result.size()); // Since each stock has two histories
//    }


//    @Test
//    public void testFetchNListingsHistoryThrowsException() {
//        // Mock the fetchNStocks method to throw an exception
//        doThrow(new RuntimeException("Test exception")).when(listingStockService).fetchNStocks(anyInt());
//
//        // Call the method under test
//        List<ListingHistory> result = listingStockService.fetchNListingsHistory(10);
//
//        // Assert that the exception was thrown
//        verify(listingStockService, times(1)).fetchNStocks(10);
//        verify(listingStockService, never()).fetchSingleListingHistory(anyString());
//        assertTrue(result.isEmpty());
//    }

//    @Test
//    public void testCreateListingStock() {
//        // Arrange
//        ListingStock stock = new ListingStock();
//        String symbol = "AAPL";
//        String companyName = "Apple Inc.";
//        String primaryExchange = "NASDAQ";
//        stock.setTicker(symbol);
//        stock.setName(companyName);
//        stock.setExchangeName(primaryExchange);
//        // Act
//        ListingStock result = listingStockService.createListingStock(symbol, companyName, primaryExchange);
//
//        // Assert
//        assertEquals(symbol, result.getTicker());
//        assertEquals(companyName, result.getName());
//        assertEquals(primaryExchange, result.getExchangeName());
//    }
//
//
//
//    @Test
//    public void testCreateListingStock2() throws Exception {
//        // Mock data
//        String symbol = "AAPL";
//        String companyName = "Apple Inc.";
//        String primaryExchange = "NASDAQ";
//        String updateListingApiUrl = "your_update_listing_api_url_here";
//        String basicStockInfoApiUrl = "your_basic_stock_info_api_url_here";
//        String alphaVantageAPIToken = "your_api_token_here";
//
//        String listingBaseUrl = updateListingApiUrl + symbol + "&apikey=" + alphaVantageAPIToken;
//        String listingStockUrl = basicStockInfoApiUrl + symbol + "&apikey=" + alphaVantageAPIToken;
//
//        // Mock response from requests
//        String baseResponse = "{ \"Global Quote\": { \"03. high\": 150.00, \"04. low\": 100.00, \"05. price\": 120.00, \"06. volume\": 100000, \"09. change\": 5.00 } }";
//        String stockResponse = "{ \"Name\": \"Apple Inc.\", \"DividendYield\": 1.25, \"SharesOutstanding\": 1000000000 }";
//
//        when(Requests.sendRequest(listingBaseUrl)).thenReturn(baseResponse);
//        when(Requests.sendRequest(listingStockUrl)).thenReturn(stockResponse);
//
//        JsonNode baseNode = objectMapper.readTree(baseResponse).get("Global Quote");
//        JsonNode stockNode = objectMapper.readTree(stockResponse);
//
//        // Mock response from exchangeRepository
//        Exchange exchange = new Exchange();
//        when(exchangeRepository.findByExchangeName(primaryExchange)).thenReturn(exchange);
//
//        // Mock stockMapper behavior
//        ListingStock expectedStock = new ListingStock();
//        when(stockMapper.createListingStock(symbol, companyName, exchange, 120.00, 150.00, 100.00, 5.00, 100000, 1000000000, 1.25)).thenReturn(expectedStock);
//
//        // Test
//        ListingStock result = listingStockService.createListingStock(symbol, companyName, primaryExchange);
//        assertEquals(expectedStock, result);
//    }
//
//    @Test
//    public void testCreateListingStock4() throws Exception {
//        // Mock data
//        String symbol = "AAPL";
//        String companyName = "Apple Inc.";
//        String primaryExchange = "NASDAQ";
//        String updateListingApiUrl = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=";
//        String basicStockInfoApiUrl = "https://www.alphavantage.co/query?function=OVERVIEW&symbol=";
//        String alphaVantageAPIToken = "OF6BVKZOCXWHD9NS";
//
//        String listingBaseUrl = updateListingApiUrl + symbol + "&apikey=" + alphaVantageAPIToken;
//        String listingStockUrl = basicStockInfoApiUrl + symbol + "&apikey=" + alphaVantageAPIToken;
//
//        // Mock response from requests
//        String baseResponse = "{ \"Global Quote\": { \"03. high\": 150.00, \"04. low\": 100.00, \"05. price\": 120.00, \"06. volume\": 100000, \"09. change\": 5.00 } }";
//        String stockResponse = "{ \"Name\": \"Apple Inc.\", \"DividendYield\": 1.25, \"SharesOutstanding\": 1000000000 }";
//
//        when(Requests.sendRequest(listingBaseUrl)).thenReturn(baseResponse);
//        when(Requests.sendRequest(listingStockUrl)).thenReturn(stockResponse);
//
//        JsonNode baseNode = mock(JsonNode.class);
//        when(objectMapper.readTree(baseResponse)).thenReturn(baseNode);
//        when(baseNode.get("03. high").asDouble()).thenReturn(150.00);
//        when(baseNode.get("04. low").asDouble()).thenReturn(100.00);
//        when(baseNode.get("05. price").asDouble()).thenReturn(120.00);
//        when(baseNode.get("06. volume").asDouble()).thenReturn(10000.0);
//        when(baseNode.get("09. change").asDouble()).thenReturn(50.00);
//        // Mock other fields similarly
//
//        JsonNode stockNode = mock(JsonNode.class);
//        when(objectMapper.readTree(stockResponse)).thenReturn(stockNode);
//        when(stockNode.get("Name").asText()).thenReturn("Apple Inc.");
//        when(stockNode.get("DividendYield").asDouble()).thenReturn(1.25);
//        when(stockNode.get("SharesOutstanding").asInt()).thenReturn(1000000000);
////
////        when(objectMapper.readTree(baseResponse)).thenReturn(baseNode);
////        when(objectMapper.readTree(stockResponse)).thenReturn(mock(JsonNode.class));
//
//        // Mock response from exchangeRepository
//        Exchange exchange = new Exchange();
//        when(exchangeRepository.findByExchangeName(primaryExchange)).thenReturn(exchange);
//
//        // Mock stockMapper behavior
//        ListingStock expectedStock = new ListingStock();
//        when(stockMapper.createListingStock(symbol, companyName, exchange, 120.00, 150.00, 100.00, 5.00, 100000, 1000000000, 1.25)).thenReturn(expectedStock);
//
//        // Test
//        ListingStock result = listingStockService.createListingStock(symbol, companyName, primaryExchange);
//        assertEquals(expectedStock, result);
//    }


//    @Test
//    public void testFetchSingleListingHistory() {
//        // Arrange
//        String ticker = "AAPL";
//        String historyURl= "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=";
//        String apiKey = "OF6BVKZOCXWHD9NS";
//        String apiUrl = historyURl + ticker + "&outputsize=compact&apikey=" + apiKey;
//        String response = "{\n" +
//                "    \"Meta Data\": {\n" +
//                "        \"1. Information\": \"Daily Prices (open, high, low, close) and Volumes\",\n" +
//                "        \"2. Symbol\": \"AAPL\",\n" +
//                "        \"3. Last Refreshed\": \"2024-04-12\",\n" +
//                "        \"4. Output Size\": \"Compact\",\n" +
//                "        \"5. Time Zone\": \"US/Eastern\"\n" +
//                "    },\n" +
//                "    \"Time Series (Daily)\": {\n" +
//                "        \"2024-04-12\": {\n" +
//                "            \"1. open\": \"174.2600\",\n" +
//                "            \"2. high\": \"178.3600\",\n" +
//                "            \"3. low\": \"174.2100\",\n" +
//                "            \"4. close\": \"176.5500\",\n" +
//                "            \"5. volume\": \"101670886\"\n" +
//                "        },\n" +
//                "        \"2024-04-11\": {\n" +
//                "            \"1. open\": \"168.3400\",\n" +
//                "            \"2. high\": \"175.4600\",\n" +
//                "            \"3. low\": \"168.1600\",\n" +
//                "            \"4. close\": \"175.0400\",\n" +
//                "            \"5. volume\": \"91070275\"\n" +
//                "        }}}";
//
//        try {
//            when(requests.sendRequest(apiUrl)).thenReturn(response);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//
//        // Act
//        List<ListingHistory> result = listingStockService.fetchSingleListingHistory(ticker);
//
//        // Assert
//        // Replace with appropriate assertions
//        assertEquals(1, result.size());
//        assertEquals(ticker, result.get(0).getTicker());
//    }
//
//    @Test
//    public void testFetchSingleListingHistory9() throws Exception {
//        // Mock data
//        String ticker = "AAPL";
//        String historyURl= "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=";
//        String apiKey = "OF6BVKZOCXWHD9NS";
//        String response = "response_from_api_here";
//
//        // Mock requests.sendRequest behavior
//        when(requests.sendRequest(anyString())).thenReturn(response);
//
//        // Mock objectMapper.readTree behavior
//        JsonNode rootNode = mock(JsonNode.class);
//        when(objectMapper.readTree(response)).thenReturn(rootNode);
//
//        JsonNode timeSeriesNode = mock(JsonNode.class);
//        when(rootNode.get("Time Series (Daily)")).thenReturn(timeSeriesNode);
//
//        Iterator<Map.Entry<String, JsonNode>> fields = mock(Iterator.class);
//        when(timeSeriesNode.fields()).thenReturn(fields);
//        when(fields.hasNext()).thenReturn(true, false);
//
//        Map.Entry<String, JsonNode> entry = mock(Map.Entry.class);
//        when(fields.next()).thenReturn(entry);
//        when(entry.getKey()).thenReturn("2024-04-01");
//
//        JsonNode dataNode = mock(JsonNode.class);
//        when(entry.getValue()).thenReturn(dataNode);
//
//        ListingHistory expectedListingHistory = new ListingHistory();
//        when(listingStockService.createListingHistoryModelFromJson(dataNode, ticker, 123456789)).thenReturn(expectedListingHistory);
//
//        // Test fetchSingleListingHistory
//        ListingStockServiceImpl  yourServiceClass = new ListingStockServiceImpl();
//        yourServiceClass.setHistoryListingApiUrl(historyURl);
//        yourServiceClass.setAlphaVantageAPIToken(apiKey);
//        yourServiceClass.setRequests(requests);
//
//
//        List<ListingHistory> result = yourServiceClass.fetchSingleListingHistory(ticker);
//
//        // Verify behavior
//        assertEquals(1, result.size());
//        assertEquals(expectedListingHistory, result.get(0));
//    }
//
//    @Test
//    public void testFetchSingleListingHistoryReturnsListOfListingHistories() throws Exception {
//        String ticker = "AAPL";
//        String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + ticker + "&apikey=demo";
//        String response = "{\"Time Series (Daily)\": {\"2023-03-13\": {\"1. open\": \"147.6400\", \"2. high\": \"148.8000\", \"3. low\": \"146.8300\", \"4. close\": \"147.5500\", \"5. volume\": \"78955600\"}}}";
//        List<ListingHistory> expectedListingHistories = new ArrayList<>();
//        JsonNode rootNode = null;
//        try {
//            rootNode = objectMapper.readTree(response);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        JsonNode timeSeriesNode = rootNode.get("Time Series (Daily)");
//        Iterator<Map.Entry<String, JsonNode>> fields = timeSeriesNode.fields();
//        while (fields.hasNext()) {
//            Map.Entry<String, JsonNode> entry = fields.next();
//            String dateStr = entry.getKey();
//            LocalDate date = LocalDate.parse(dateStr);
//            int unixTimestamp = (int) date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
//            JsonNode dataNode = entry.getValue();
//            ListingHistory listingHistory = listingStockService.createListingHistoryModelFromJson(dataNode, ticker, unixTimestamp);
//            expectedListingHistories.add(listingHistory);
//        }
//
//        when(requests.sendRequest(apiUrl)).thenReturn(response);
//
//        List<ListingHistory> result = listingStockService.fetchSingleListingHistory(ticker);
//
//        assertEquals(expectedListingHistories, result);
//    }

}
