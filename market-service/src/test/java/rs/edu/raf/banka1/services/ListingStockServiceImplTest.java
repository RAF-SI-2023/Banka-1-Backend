package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.mapper.ListingStockMapper;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;
import rs.edu.raf.banka1.repositories.StockRepository;

import java.sql.Date;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListingStockServiceImplTest {
    @Mock
    private ListingHistoryRepository listingHistoryRepository;
    @Mock
    private ListingStockMapper listingMapper;
    @Mock
    private StockRepository stockRepository;
    @InjectMocks
    private ListingStockServiceImpl listingStockService;
    private ListingStock stockAAPL;
    private ListingStock stockMSFT;
    private List<ListingStock> stocks;

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

        stocks = new ArrayList<>();
        stocks.add(stockAAPL);
        stocks.add(stockMSFT);

        // history data
        model1 = new ListingHistory();
        model1.setTicker("AAPL");
        model1.setDate(Date.valueOf("2021-01-01").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
        model1.setPrice(100.0);
        model1.setAsk(101.0);
        model1.setBid(99.0);
        model1.setChanged(0.0);
        model1.setVolume(1000);

        model2 = new ListingHistory();
        model2.setTicker("MSFT");
        model2.setDate(Date.valueOf("2021-01-01").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
        model2.setPrice(100.0);
        model2.setAsk(101.0);
        model2.setBid(99.0);
        model2.setChanged(0.0);
        model2.setVolume(1000);

        lst.add(model1);
        lst.add(model2);

        date = Date.valueOf("2021-01-01").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
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
        when(listingHistoryRepository.findByTickerAndDate("AAPL", date)).thenReturn(Optional.empty());
        assertEquals(1, listingStockService.addListingToHistory(model1));
    }

    @Test
    public void addListingToHistoryPresentTest(){
        ListingHistory listingHistory = new ListingHistory();
        listingHistory.setTicker("AAPL");
        listingHistory.setDate(date);
        listingHistory.setPrice(100.0);
        listingHistory.setAsk(101.0);
        listingHistory.setBid(99.0);
        listingHistory.setChanged(0.0);
        listingHistory.setVolume(1000);

        ListingHistory updateModel = new ListingHistory();
        updateModel.setTicker("AAPL");
        updateModel.setDate(date);
        updateModel.setPrice(700.0);
        updateModel.setAsk(105.0);
        updateModel.setBid(100.0);
        updateModel.setChanged(1.0);
        updateModel.setVolume(10000);

        when(listingHistoryRepository.findByTickerAndDate("AAPL", date)).thenReturn(Optional.of(listingHistory));
        assertEquals(0, listingStockService.addListingToHistory(updateModel));
    }

    @Test
    public void addAllListingsToHistoryEveryPresentTest(){
        when(listingHistoryRepository.findByTickerAndDate("AAPL", date)).thenReturn(Optional.of(model1));
        when(listingHistoryRepository.findByTickerAndDate("MSFT", date)).thenReturn(Optional.of(model2));
        assertEquals(0, listingStockService.addAllListingsToHistory(lst));
    }

    @Test
    public void addAllListingsToHistoryNothingPresentTest(){
        when(listingHistoryRepository.findByTickerAndDate("AAPL", date)).thenReturn(Optional.empty());
        when(listingHistoryRepository.findByTickerAndDate("MSFT", date)).thenReturn(Optional.empty());
        assertEquals(lst.size(), listingStockService.addAllListingsToHistory(lst));
    }

}
