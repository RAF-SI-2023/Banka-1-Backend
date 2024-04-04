package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.mapper.FutureMapper;
import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.repositories.FutureRepository;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FuturesServiceImplTest {
    @Mock
    private ListingHistoryRepository listingHistoryRepository;
    @Mock
    private FutureMapper futureMapper;
    @Mock
    private FutureRepository futureRepository;
    @Mock
    private ListingStockService listingStockService;
    @InjectMocks
    private FuturesServiceImpl futuresService;
    private ListingFuture future1;
    private ListingFuture future2;
    private List<ListingFuture> futures;

    @BeforeEach
    public void setUp(){
        // stock data
        future1 = new ListingFuture();
        future1.setTicker("ESM24");
        future1.setPrice(100.0);

        future2 = new ListingFuture();
        future2.setTicker("YMM24");
        future2.setPrice(200.0);

        futures = new ArrayList<>();
        futures.add(future1);
        futures.add(future2);
    }

    @Test
    public void addListingStockNotPresentTest(){
        when(futuresService.findByTicker("ESM24")).thenReturn(Optional.empty());
        assertEquals(true, futuresService.addFuture(future1));
    }

    @Test
    public void addListingStockPresentTest(){
        ListingFuture updateFuture = new ListingFuture();
        updateFuture.setTicker("ESM24");
        updateFuture.setPrice(101.0);
        when(futuresService.findByTicker("ESM24")).thenReturn(Optional.of(future1));
        assertEquals(false, futuresService.addFuture(updateFuture));
    }

    @Test
    public void addAllListingStocksPresentTests(){
        when(futuresService.findByTicker("ESM24")).thenReturn(Optional.of(future1));
        when(futuresService.findByTicker("YMM24")).thenReturn(Optional.of(future2));
        assertEquals(0, futuresService.addAllFutures(futures));
    }

    @Test
    public void addAllListingStocksNotPresentTests(){
        when(futuresService.findByTicker("ESM24")).thenReturn(Optional.empty());
        when(futuresService.findByTicker("YMM24")).thenReturn(Optional.empty());
        assertEquals(futures.size(), futuresService.addAllFutures(futures));
    }
}
