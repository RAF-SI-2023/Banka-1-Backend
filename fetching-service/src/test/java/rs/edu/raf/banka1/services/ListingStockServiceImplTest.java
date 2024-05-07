package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.mapper.StockMapper;
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
    private StockMapper listingMapper;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private StockMapper stockMapper;
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
