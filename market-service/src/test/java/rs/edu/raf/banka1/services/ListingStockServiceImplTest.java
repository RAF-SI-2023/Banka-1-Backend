package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class ListingStockServiceImplTest {
    @Mock
    private ListingHistoryRepository listingHistoryRepository;
    @Mock
    private StockMapper listingMapper;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private HolidayRepository holidayRepository;
    @Mock
    private StockMapper stockMapper;
    @InjectMocks
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
        countryUS.setTimezoneOffset(32400);
        try {
            countryUS.setOpenTime(new java.util.Date(hoursDateFormat.parse("20:00:00").getTime()));
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

        MockitoAnnotations.initMocks(this);
        listingStockService = new ListingStockServiceImpl();
        stockRepository = mock(StockRepository.class);
        countryRepository = mock(CountryRepository.class);
        holidayRepository = mock(HolidayRepository.class);
    }
    @Test
    public void testGetWorkingTimeById_StockNotFound() {
        Long testId = 1L;

        StockRepository mockStockRepository = Mockito.mock(StockRepository.class);
        when(mockStockRepository.findById(testId)).thenReturn(Optional.empty());

        ListingStockServiceImpl service = new ListingStockServiceImpl();
        service.setStockRepository(mockStockRepository);

        String result = service.getWorkingTimeById(testId);
        assertEquals("Stock not found", result);
    }

    @Test
    public void testGetWorkingTimeById_CountryNotFound() {
        Long testId = 1L;

        StockRepository mockStockRepository = Mockito.mock(StockRepository.class);
        CountryRepository mockCountryRepository = Mockito.mock(CountryRepository.class);

        ListingStock mockListingStock = stockDT;
        mockListingStock.setExchange(exchangeDT);
        when(mockStockRepository.findById(testId)).thenReturn(Optional.of(mockListingStock));

        when(mockCountryRepository.findById(anyLong())).thenReturn(Optional.empty());

        ListingStockServiceImpl service = new ListingStockServiceImpl();
        service.setStockRepository(mockStockRepository);
        service.setCountryRepository(mockCountryRepository);

        String result = service.getWorkingTimeById(testId);
        assertEquals("Country not found", result);
    }

    private final Long testId = 1L;

    @Test
    public void testGetWorkingTimeById_ClosedForHoliday() {
        StockRepository mockStockRepository = Mockito.mock(StockRepository.class);
        CountryRepository mockCountryRepository = Mockito.mock(CountryRepository.class);
        HolidayRepository mockHolidayRepository = Mockito.mock(HolidayRepository.class);

        Holiday holiday = new Holiday();
        holiday.setCountry(countryUS);
        holiday.setDate(new java.util.Date());
        holiday.setId(1000L);

        when(mockStockRepository.findById(testId)).thenReturn(Optional.of(stockDT));

        when(mockCountryRepository.findById(anyLong())).thenReturn(Optional.of(countryUS));

        when(mockHolidayRepository.findByCountryId(any())).thenReturn(
                Optional.of(Collections.singletonList(holiday))
        );

        ListingStockServiceImpl service = new ListingStockServiceImpl();
        service.setStockRepository(mockStockRepository);
        service.setCountryRepository(mockCountryRepository);
        service.setHolidayRepository(mockHolidayRepository);

        String result = service.getWorkingTimeById(testId);
        assertEquals("CLOSED", result);
    }

    @Test
    public void testGetWorkingTimeById_ClosedOutsideWorkingHours() {
        // Setup repositories and data. Mock current time to be outside working hours.

        // Assuming LocalDateTime.now(ZoneId) can be mocked
        LocalDateTime mockedCurrentTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)); // Outside typical working hours

        // Use Mockito or another method to set the system time

        ListingStockServiceImpl service = new ListingStockServiceImpl();
        service.setStockRepository(stockRepository);
        service.setCountryRepository(countryRepository);
        service.setHolidayRepository(holidayRepository);

        String result = service.getWorkingTimeById(testId);
        assertEquals("CLOSED ", result); // Note the space in "CLOSED "
    }

    @Test
    public void testGetWorkingTimeById_AfterHours() {


        ListingStockServiceImpl service = new ListingStockServiceImpl();
        service.setStockRepository(stockRepository);
        service.setCountryRepository(countryRepository);
        service.setHolidayRepository(holidayRepository);

        String result = service.getWorkingTimeById(testId);
        assertEquals("AFTER_HOURS", result);
    }

    @Test
    public void testGetWorkingTimeById_Opened() {
        // Setup repositories and mock current time to be within working hours

        ListingStockServiceImpl service = new ListingStockServiceImpl();
        service.setStockRepository(stockRepository);
        service.setCountryRepository(countryRepository);
        service.setHolidayRepository(holidayRepository);

        String result = service.getWorkingTimeById(testId);
        assertEquals("OPENED", result);
    }


    @Test
    public void addListingStockNotPresentTest(){
        when(listingStockService.findByTicker("AAPL")).thenReturn(Optional.empty());
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
        assertEquals(0, listingStockService.addAllListingStocks(stocks));
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

}
