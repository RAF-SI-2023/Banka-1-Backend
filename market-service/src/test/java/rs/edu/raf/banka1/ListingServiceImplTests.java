package rs.edu.raf.banka1;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;
import rs.edu.raf.banka1.services.ListingServiceImpl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ListingServiceImplTests {

    @Mock
    private ListingHistoryRepository listingHistoryRepository;


    @InjectMocks
    private ListingServiceImpl listingService;

    private List<ListingHistoryModel> lst = new ArrayList<>();
    private ListingHistoryModel model1;
    private ListingHistoryModel model2;

    @BeforeEach
    public void setUp(){
        model1 = new ListingHistoryModel();
        model1.setTicker("AAPL");
        model1.setDate(Date.valueOf("2021-01-01").toLocalDate());
        model1.setPrice(100.0);
        model1.setAsk(101.0);
        model1.setBid(99.0);
        model1.setChanged(0.0);
        model1.setVolume(1000);

        model2 = new ListingHistoryModel();
        model2.setTicker("MSFT");
        model2.setDate(Date.valueOf("2021-01-01").toLocalDate());
        model2.setPrice(100.0);
        model2.setAsk(101.0);
        model2.setBid(99.0);
        model2.setChanged(0.0);
        model2.setVolume(1000);

        lst.add(model1);
        lst.add(model2);
    }



    @Test
    public void addListingToHistoryNotPresentTest(){

        when(listingHistoryRepository.findByTickerAndDate("AAPL", Date.valueOf("2021-01-01").toLocalDate())).thenReturn(Optional.empty());

        assertEquals(1, listingService.addListingToHistory(model1));

    }

    @Test
    public void addListingToHistoryPresentTest(){
        ListingHistoryModel listingHistoryModel = new ListingHistoryModel();
        listingHistoryModel.setTicker("AAPL");
        listingHistoryModel.setDate(Date.valueOf("2021-01-01").toLocalDate());
        listingHistoryModel.setPrice(100.0);
        listingHistoryModel.setAsk(101.0);
        listingHistoryModel.setBid(99.0);
        listingHistoryModel.setChanged(0.0);
        listingHistoryModel.setVolume(1000);

        ListingHistoryModel updateModel = new ListingHistoryModel();
        updateModel.setTicker("AAPL");
        updateModel.setDate(Date.valueOf("2021-01-01").toLocalDate());
        updateModel.setPrice(700.0);
        updateModel.setAsk(105.0);
        updateModel.setBid(100.0);
        updateModel.setChanged(1.0);
        updateModel.setVolume(10000);

        when(listingHistoryRepository.findByTickerAndDate("AAPL", Date.valueOf("2021-01-01").toLocalDate())).thenReturn(Optional.of(listingHistoryModel));

        assertEquals(0, listingService.addListingToHistory(updateModel));
    }

    @Test
    public void addAllListingsToHistoryEveryPresentTest(){
        when(listingHistoryRepository.findByTickerAndDate("AAPL", Date.valueOf("2021-01-01").toLocalDate())).thenReturn(Optional.of(model1));
        when(listingHistoryRepository.findByTickerAndDate("MSFT", Date.valueOf("2021-01-01").toLocalDate())).thenReturn(Optional.of(model2));

        assertEquals(0, listingService.addAllListingsToHistory(lst));
    }

    @Test
    public void addAllListingsToHistoryNothingPresentTest(){
        when(listingHistoryRepository.findByTickerAndDate("AAPL", Date.valueOf("2021-01-01").toLocalDate())).thenReturn(Optional.empty());
        when(listingHistoryRepository.findByTickerAndDate("MSFT", Date.valueOf("2021-01-01").toLocalDate())).thenReturn(Optional.empty());

        assertEquals(lst.size(), listingService.addAllListingsToHistory(lst));

    }
}
