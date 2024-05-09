package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import  org.springframework.core.io.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.mapper.StockMapper;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.entities.Country;
import rs.edu.raf.banka1.model.entities.Exchange;
import rs.edu.raf.banka1.model.entities.Holiday;
import rs.edu.raf.banka1.repositories.*;


import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;


import static org.junit.Assert.assertNotNull;
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

    @Mock
    private JsonNode jsonNode;


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

    //zasto ovo ne radi u 21h?
//    @Test
//    public void testGetWorkingTimeById_Opened() {
//        ListingStock stock;
//        Country country;
//        Exchange exchange;
//
//        stock = new ListingStock();
//        stock.setListingId(1L);
//
//        country = new Country();
//        country.setId(1L);
//        country.setTimezoneOffset(0);
//        // Set openTime and closeTime
//        SimpleDateFormat hoursDateFormat = new SimpleDateFormat("HH:mm:ss");
//        try {
//            country.setOpenTime(new java.util.Date(hoursDateFormat.parse("08:00:00").getTime()));
//            country.setCloseTime(new java.util.Date(hoursDateFormat.parse("18:00:00").getTime()));
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//        exchange = new Exchange();
//        exchange.setCountry(country);
//        stock.setExchange(exchange);
//
//
//        when(stockRepository.findById(anyLong())).thenReturn(Optional.of(stock));
//        when(countryRepository.findById(anyLong())).thenReturn(Optional.of(country));
//        when(holidayRepository.findByCountryId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));
//
//        String result = listingStockService.getWorkingTimeById(1L);
//
//        assertEquals("OPENED", result);
//    }

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
        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T22:00:00Z"), ZoneId.of("UTC"));
        listingStockService.setClock(fixedClock);

        String result = listingStockService.getWorkingTimeById(10L);

        assertEquals("CLOSED", result);
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
    public void testGetListingHistoriesByTimestampForTicker() {

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


    @Test
    public void testGetListingHistoriesByTimestampForId() {
        // Mock data
        Long id = 1L;
        Integer from = 1000;
        Integer to = 2000;
        String ticker = "AAPL";
        ListingStock stock = new ListingStock();
        stock.setListingId(id);
        stock.setTicker(ticker);
        List<ListingHistory> listingHistories = new ArrayList<>();
        ListingHistory history1 = new ListingHistory();
        ListingHistory history2 = new ListingHistory();
        listingHistories.add(history1);
        listingHistories.add(history2);

        // Mock stockRepository behavior
        when(stockRepository.findById(id)).thenReturn(Optional.of(stock));

        // Mock listingHistoryRepository behavior
        when(listingHistoryRepository.getListingHistoriesByTicker(ticker)).thenReturn(listingHistories);
        when(listingHistoryRepository.getListingHistoriesByTickerAndDateBefore(ticker, to)).thenReturn(listingHistories);
        when(listingHistoryRepository.getListingHistoriesByTickerAndDateAfter(ticker, from)).thenReturn(listingHistories);
        when(listingHistoryRepository.getListingHistoriesByTickerAndDateBetween(ticker, from, to)).thenReturn(listingHistories);

        List<ListingHistory> result;

        // Test from == null && to != null
        result = listingStockService.getListingHistoriesByTimestamp(id, null, to);
        assertEquals(listingHistories, result);

        // Test from != null && to == null
        result = listingStockService.getListingHistoriesByTimestamp(id, from, null);
        assertEquals(listingHistories, result);

        // Test from != null && to != null
        result = listingStockService.getListingHistoriesByTimestamp(id, from, to);
        assertEquals(listingHistories, result);

        // Verify behavior
        verify(stockRepository, times(3)).findById(id);
        verify(listingHistoryRepository, times(3)).getListingHistoriesByTicker(ticker);
        verify(listingHistoryRepository, times(1)).getListingHistoriesByTickerAndDateBefore(ticker, to);
        verify(listingHistoryRepository, times(1)).getListingHistoriesByTickerAndDateAfter(ticker, from);
        verify(listingHistoryRepository, times(1)).getListingHistoriesByTickerAndDateBetween(ticker, from, to);
    }

    @Test
    public void testGetListingHistoriesByTimestamp_StockNotFound() {
        Long id = 1L;
        when(stockRepository.findById(id)).thenReturn(Optional.empty());

        List<ListingHistory> result = listingStockService.getListingHistoriesByTimestamp(id, null, null);

        assertEquals(0, result.size());
    }

    //ovaj je opasan
    @Test
    public void testGetListingHistoriesByTimestamp_NoHistoriesFound()
    {
        Long id = 1L;
        ListingStock stock = new ListingStock();
        stock.setListingId(id);
        stock.setTicker("stock");
        String apiUrl = "https://your_api_url_here/" + stock.getTicker() + "&outputsize=compact&apikey=" + "mockAlphaToken";

        when(stockRepository.findById(id)).thenReturn(Optional.of(stock));
        when(listingHistoryRepository.getListingHistoriesByTicker("stock")).thenReturn(new ArrayList<>());

        List<ListingHistory> result = listingStockService.getListingHistoriesByTimestamp(id, null, null);

        assertEquals(0, result.size());
    }

//    @Test
//    public void testGenerateJSONSymbols() throws Exception {
//        // Arrange
//        String expectedResponse = "[{\"symbol\":\"AAPL\",\"companyName\":\"Apple Inc.\",\"primaryExchange\":\"NASDAQ\"}," +
//                "{\"symbol\":\"MSFT\",\"companyName\":\"Microsoft Corporation\",\"primaryExchange\":\"NASDAQ\"}]";
//        String expectedUrl = listingStockService.getListingNameApiUrl() + "Technology&token=" + listingStockService.getListingAPItoken();
//
//
//        // Mock the static method sendRequest of Requests class
//        try (MockedStatic<Requests> mockedRequests = Mockito.mockStatic(Requests.class)) {
//            mockedRequests.when(() -> Requests.sendRequest(expectedUrl)).thenReturn(expectedResponse);
//
//            // Mock the reformatNamesToJSON method of ListingStockServiceImpl class
//            ListingStockServiceImpl mockListingStockService = Mockito.mock(ListingStockServiceImpl.class);
//            ObjectMapper objectMapper = new ObjectMapper();
//            ArrayNode expectedArrayNode = objectMapper.createArrayNode();
//            Mockito.when(mockListingStockService.reformatNamesToJSON(expectedResponse)).thenReturn(expectedArrayNode);
//
//            // Act
//            mockListingStockService.generateJSONSymbols();
//
//            // Assert
//
//            // Verify that the mocked methods were called with the expected parameters
//            mockedRequests.verify(() -> Requests.sendRequest(expectedUrl), Mockito.times(1));
//            Mockito.verify(listingStockService, Mockito.times(1)).reformatNamesToJSON(expectedResponse);
//        }
//    }

//    @Test
//    public void testFetchNListingStocks() throws IOException {
//
//        // Mock the resource input stream
//        Resource resource = new ClassPathResource(Constants.listingsFilePath, this.getClass().getClassLoader());
//        when(objectMapper.readTree(resource.getInputStream())).thenReturn(
//                objectMapper.createObjectNode().putArray("testData").add(
//                        objectMapper.createObjectNode().put("symbol", "AAPL").put("companyName", "Apple Inc.").put("primaryExchange", "NASDAQ")
//                ).add(
//                        objectMapper.createObjectNode().put("symbol", "GOOGL").put("companyName", "Alphabet Inc.").put("primaryExchange", "NASDAQ")
//                )
//        );
//
//        // Mock the createListingStock method
//        ListingStock stock1 = new ListingStock();
//        ListingStock stock2 = new ListingStock();
//        when(listingStockService.createListingStock("AAPL", "Apple Inc.", "NASDAQ")).thenReturn(stock1);
//        when(listingStockService.createListingStock("GOOGL", "Alphabet Inc.", "NASDAQ")).thenReturn(stock2);
//
//        // Call the method to be tested
//        List<ListingStock> result = listingStockService.fetchNListingStocks(2);
//
//        // Assert the result
//        assertEquals(2, result.size());
//        assertEquals(stock1, result.get(0));
//        assertEquals(stock2, result.get(1));
//    }

    @Test
    public void testGetStockRepository() {
        StockRepository result = listingStockService.getStockRepository();
        assertEquals(stockRepository, result);
    }
    @Test
    public void testGetExchangeRepository() {
        ExchangeRepository result = listingStockService.getExchangeRepository();
        assertEquals(exchangeRepository, result);
    }
    @Test
    public void testGetCountryRepository() {
        CountryRepository result = Mockito.mock(CountryRepository.class);
        listingStockService.setCountryRepository(result);
        assertEquals(listingStockService.getCountryRepository(), result);
    }

    @Test
    public void testGetListingHolidayRepository(){
        HolidayRepository result =Mockito.mock(HolidayRepository.class);
        listingStockService.setHolidayRepository(result);
        assertEquals(listingStockService.getHolidayRepository(),result);
    }

    @Test
    public void testGetStockMapper(){
        StockMapper result = Mockito.mock(StockMapper.class);
        listingStockService.setStockMapper(result);
        assertEquals( listingStockService.getStockMapper(),result);
    }

    @Test
    public void testGetListingHistoryRepository(){
        ListingHistoryRepository result =Mockito.mock(ListingHistoryRepository.class);
        listingStockService.setListingHistoryRepository(result);
        assertEquals(listingStockService.getListingHistoryRepository(),result);
    }

}
