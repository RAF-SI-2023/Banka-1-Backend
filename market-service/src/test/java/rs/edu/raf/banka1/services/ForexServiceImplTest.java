package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.mapper.ForexMapper;
import rs.edu.raf.banka1.mapper.ListingHistoryMapper;
import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.repositories.ForexRepository;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForexServiceImplTest {
    @Spy
    private ForexServiceImpl forexService;
    private ListingHistoryRepository listingHistoryRepository;
    private ForexRepository forexRepository;

    @BeforeEach
    public void setUp() {
        this.forexRepository = mock(ForexRepository.class);
        this.listingHistoryRepository = mock(ListingHistoryRepository.class);
        forexService.setListingHistoryRepository(listingHistoryRepository);
        forexService.setForexRepository(forexRepository);
    }

    @Test
    public void findByIdTest() {
        Long id = 1L;
        ListingForex forex = new ListingForex();
        when(forexRepository.findById(id)).thenReturn(Optional.of(forex));

        Optional<ListingForex> found = forexService.findById(id);

        assertTrue(found.isPresent());
        assertEquals(forex, found.get());
    }

    @Test
    public void getListingHistoriesByTimestampTestForexNull(){
        Long id = 1L;
        when(forexRepository.findById(id)).thenReturn(Optional.empty());
        List<ListingHistory> listingHistories = forexService.getListingHistoriesByTimestamp(id, null, null);
        assertNotNull(listingHistories);
        assertTrue(listingHistories.isEmpty());
    }



    @Test
    public void getListingHistoriesByTimestampTestFromTimestamp(){
        ListingHistory history1 = new ListingHistory();
        history1.setDate(20220318);
        ListingHistory history2 = new ListingHistory();
        history2.setDate(20220319);
        List<ListingHistory> histories = List.of(history1, history2);

        Long id = 1L;
        ListingForex forex = new ListingForex();
        forex.setTicker("ticker");
        when(forexRepository.findById(id)).thenReturn(Optional.of(forex));
        when(listingHistoryRepository.getListingHistoriesByTicker(forex.getTicker())).thenReturn(histories);
        when(listingHistoryRepository.getListingHistoriesByTickerAndDateAfter(eq(forex.getTicker()), any())).thenReturn(List.of(history2));

        List<ListingHistory> listingHistories = forexService.getListingHistoriesByTimestamp(id, 20220319, null);
        assertNotNull(listingHistories);
        verify(listingHistoryRepository, times(1)).getListingHistoriesByTickerAndDateAfter(eq(forex.getTicker()), any());
        assertEquals(1, listingHistories.size());
        assertEquals(history2, listingHistories.get(0));
    }

    @Test
    public void getListingHistoriesByTimestampTestToTimestamp(){
        ListingHistory history1 = new ListingHistory();
        history1.setDate(20220318);
        ListingHistory history2 = new ListingHistory();
        history2.setDate(20220319);
        List<ListingHistory> histories = List.of(history1, history2);

        Long id = 1L;
        ListingForex forex = new ListingForex();
        forex.setTicker("ticker");
        when(forexRepository.findById(id)).thenReturn(Optional.of(forex));
        when(listingHistoryRepository.getListingHistoriesByTicker(forex.getTicker())).thenReturn(histories);
        when(listingHistoryRepository.getListingHistoriesByTickerAndDateBefore(eq(forex.getTicker()), eq(20220318))).thenReturn(List.of(history1));

        List<ListingHistory> listingHistories = forexService.getListingHistoriesByTimestamp(id, null, 20220318);
        assertNotNull(listingHistories);
        verify(listingHistoryRepository, times(1)).getListingHistoriesByTickerAndDateBefore(eq(forex.getTicker()), any());
        assertEquals(1, listingHistories.size());
        assertEquals(history1, listingHistories.get(0));

    }

    @Test
    public void getListingHistoriesByTimestampTestFromToTimestamp(){
        ListingHistory history1 = new ListingHistory();
        history1.setDate(20220318);
        ListingHistory history2 = new ListingHistory();
        history2.setDate(20220319);
        List<ListingHistory> histories = List.of(history1, history2);

        Long id = 1L;
        ListingForex forex = new ListingForex();
        forex.setTicker("ticker");
        when(forexRepository.findById(id)).thenReturn(Optional.of(forex));
        when(listingHistoryRepository.getListingHistoriesByTicker(forex.getTicker())).thenReturn(histories);
        when(listingHistoryRepository.getListingHistoriesByTickerAndDateBetween(eq(forex.getTicker()), eq(20220318), eq(20220319))).thenReturn(List.of(history1, history2));

        List<ListingHistory> listingHistories = forexService.getListingHistoriesByTimestamp(id, 20220318, 20220319);
        assertNotNull(listingHistories);
        verify(listingHistoryRepository, times(1)).getListingHistoriesByTickerAndDateBetween(eq(forex.getTicker()), eq(20220318), eq(20220319));
        assertEquals(2, listingHistories.size());

    }

    @Test
    public void getAllForexesTest(){
        ListingForex forex1 = new ListingForex();
        ListingForex forex2 = new ListingForex();
        List<ListingForex> forexes = List.of(forex1, forex2);
        when(forexRepository.findAll()).thenReturn(forexes);

        List<ListingForex> allForexes = forexService.getAllForexes();

        assertNotNull(allForexes);
        assertEquals(2, allForexes.size());
    }

    @Test
    public void getForexByTickerTest(){
        String ticker = "ticker";
        ListingForex forex = new ListingForex();
        when(forexRepository.findByTicker(ticker)).thenReturn(Optional.of(forex));

        ListingForex found = forexService.getForexByTicker(ticker);

        assertNotNull(found);
        assertEquals(forex, found);
    }
}
