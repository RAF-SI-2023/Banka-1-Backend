package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.mapper.ForexMapper;
import rs.edu.raf.banka1.mapper.ListingHistoryMapper;
import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.repositories.ForexRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ForexServiceImplTest {
    @Spy
    private ForexServiceImpl forexService;

    @Mock
    private JsonNode dataNode;

    @BeforeEach
    public void setUp() {
        ForexRepository forexRepository = mock(ForexRepository.class);
        ForexMapper forexMapper = new ForexMapper();
        ListingHistoryMapper listingHistoryMapper = new ListingHistoryMapper();
        forexService.setForexMapper(forexMapper);
        forexService.setListingHistoryMapper(listingHistoryMapper);
        forexService.setForexRepository(forexRepository);
    }
    @Test
    public void parseHistoryTest() {
        String ticker = "EUR/USD";
        int date = 20220318;
        double open = 135.0;
        double high = 138.0;
        double low = 134.0;
        double close = 137.0;
        int volume = 0;
        JsonNode openNode = createMockNode(open);
        JsonNode highNode = createMockNode(high);
        JsonNode lowNode = createMockNode(low);
        JsonNode closeNode = createMockNode(close);

        // Mocking behavior of dataNode.get(...) calls and asDouble() calls
        when(dataNode.get("1. open")).thenReturn(openNode);
        when(dataNode.get("2. high")).thenReturn(highNode);
        when(dataNode.get("3. low")).thenReturn(lowNode);
        when(dataNode.get("4. close")).thenReturn(closeNode);

        ListingHistory history = forexService.parseHistory(ticker, date, dataNode);

//      Assertions
        assertEquals(ticker, history.getTicker());
        assertEquals(date, history.getDate());
        assertEquals(close, history.getPrice());
        assertEquals(high, history.getHigh());
        assertEquals(low, history.getLow());
        assertEquals(close - open, history.getChanged());
        assertEquals(volume, history.getVolume());
    }

    // Helper method to create a mock JsonNode with given double value
    private JsonNode createMockNode(double value) {
        JsonNode node = org.mockito.Mockito.mock(JsonNode.class);
        when(node.asDouble()).thenReturn(value);
        return node;
    }

    @Test
    public void updatePricesTest() {
        String ticker = "EUR/USD";
        double price = 135.0;
        double high = 138.0;
        double low = 134.0;
        double newPrice = 140.0;
        ListingForex oldForex = new ListingForex();
        oldForex.setPrice(price);
        oldForex.setHigh(high);
        oldForex.setLow(low);

        ListingForex updatedForex = forexService.updatePrices(oldForex, newPrice, high, low);

        assertEquals(newPrice, updatedForex.getPrice());
        assertEquals(high, updatedForex.getHigh());
        assertEquals(low, updatedForex.getLow());
        assertEquals(newPrice - price, updatedForex.getPriceChange());
    }

    @Test
    public void updateAllPricesTest() {
        ListingForex oldForex1 = new ListingForex();
        ListingForex oldForex2 = new ListingForex();

        ListingForex updated = new ListingForex();

        when(forexService.getUpdatedForex(oldForex1)).thenReturn(updated);
        when(forexService.getUpdatedForex(oldForex2)).thenReturn(null);

        List<ListingForex> updatedList = forexService.updateAllPrices(List.of(oldForex1, oldForex2));
        assertEquals(1, updatedList.size());

    }
}
