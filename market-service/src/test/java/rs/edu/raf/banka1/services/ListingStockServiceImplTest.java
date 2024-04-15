package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import rs.edu.raf.banka1.Banka1Application;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
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

    private StockMapper stockMapper;

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
        stockMapper = mock(StockMapper.class);
        listingStockService = new ListingStockServiceImpl();
        listingStockService.setStockRepository(stockRepository);
        listingStockService.setCountryRepository(countryRepository);
        listingStockService.setHolidayRepository(holidayRepository);
        listingStockService.setListingHistoryRepository(listingHistoryRepository);
        listingStockService.setStockMapper(stockMapper);

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

//    @Test
//    public void testGetWorkingTimeById_ClosedOutsideWorkingHours() {
//        String result = listingStockService.getWorkingTimeById(testId);
//        assertEquals("CLOSED ", result); // Note the space in "CLOSED "
//    }
//
//    @Test
//    public void testGetWorkingTimeById_AfterHours() {
//        String result = listingStockService.getWorkingTimeById(testId);
//        assertEquals("AFTER_HOURS", result);
//    }
//
//    @Test
//    public void testGetWorkingTimeById_Opened() {
//        ListingStock listingStock = new ListingStock();
//        listingStock.setExchange(new Exchange());
//        when(stockRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        Country country = new Country();
//        country.setTimezoneOffset(0);
//
//        ZoneId zoneId = ZoneId.systemDefault();
//        LocalDate today = LocalDate.now(zoneId);
//
//        LocalTime openTime = LocalTime.of(9, 0); // Set the opening time
//        LocalTime closeTime = LocalTime.of(17, 0); // Set the closing time
//
//        ZonedDateTime openZonedDateTime = ZonedDateTime.of(today, openTime, zoneId);
//        ZonedDateTime closeZonedDateTime = ZonedDateTime.of(today, closeTime, zoneId);
//
//        country.setOpenTime(Date.from(openZonedDateTime.toInstant()));
//        country.setCloseTime(Date.from(closeZonedDateTime.toInstant()));
//
//        when(countryRepository.findById(anyLong())).thenReturn(Optional.of(country));
//
//        when(holidayRepository.findByCountryId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));
//
//        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
//            mocked.when(() -> LocalDateTime.now(any(ZoneId.class))).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)));
//            String result = listingStockService.getWorkingTimeById(1L);
//
//            assertEquals("OPENED", result);
//        }
//    }


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



}
