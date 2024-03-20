package rs.edu.raf.banka1;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import rs.edu.raf.banka1.mapper.ListingStockMapper;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;

import rs.edu.raf.banka1.services.ListingStockServiceImpl;

import java.sql.Date;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ListingServiceImplTests {

    @Mock
    private ListingHistoryRepository listingHistoryRepository;
    @Mock
    private ListingStockMapper listingMapper;

    @InjectMocks
    private ListingStockServiceImpl listingService;

    private List<ListingHistory> lst = new ArrayList<>();
    private ListingHistory model1;
    private ListingHistory model2;
    private long date;

    @BeforeEach
    public void setUp(){
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
    public void addListingToHistoryNotPresentTest(){

        when(listingHistoryRepository.findByTickerAndDate("AAPL", date)).thenReturn(Optional.empty());

        assertEquals(1, listingService.addListingToHistory(model1));

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

        assertEquals(0, listingService.addListingToHistory(updateModel));
    }

    @Test
    public void addAllListingsToHistoryEveryPresentTest(){
        when(listingHistoryRepository.findByTickerAndDate("AAPL", date)).thenReturn(Optional.of(model1));
        when(listingHistoryRepository.findByTickerAndDate("MSFT", date)).thenReturn(Optional.of(model2));

        assertEquals(0, listingService.addAllListingsToHistory(lst));
    }

    @Test
    public void addAllListingsToHistoryNothingPresentTest(){
        when(listingHistoryRepository.findByTickerAndDate("AAPL", date)).thenReturn(Optional.empty());
        when(listingHistoryRepository.findByTickerAndDate("MSFT", date)).thenReturn(Optional.empty());

        assertEquals(lst.size(), listingService.addAllListingsToHistory(lst));

    }
}
