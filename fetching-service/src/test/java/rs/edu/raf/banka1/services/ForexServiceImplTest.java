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
import rs.edu.raf.banka1.utils.Requests;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForexServiceImplTest {
    @Spy
    private ForexServiceImpl forexService;
    private ListingHistoryRepository listingHistoryRepository;
    private ForexRepository forexRepository;
    @Mock
    private JsonNode dataNode;
    @Mock
    private ListingStockService listingStockService;

    @BeforeEach
    public void setUp() {
        this.forexRepository = mock(ForexRepository.class);
        this.listingHistoryRepository = mock(ListingHistoryRepository.class);
        ForexMapper forexMapper = new ForexMapper();
        ListingHistoryMapper listingHistoryMapper = new ListingHistoryMapper();
        forexService.setListingHistoryRepository(listingHistoryRepository);
        forexService.setForexMapper(forexMapper);
        forexService.setListingHistoryMapper(listingHistoryMapper);
        forexService.setForexRepository(forexRepository);
        forexService.setListingStockService(listingStockService);
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
    public void setObjectMapperTest(){
        forexService.setObjectMapper(null);
        verify(forexService, times(1)).setObjectMapper(null);

    }

    @Test
    public void setAlphaVantageApiUrlTest(){
        forexService.setForexExchangePlaceApiUrl("mockUrl");
        verify(forexService, times(1)).setForexExchangePlaceApiUrl("mockUrl");
    }

    @Test
    public void setAphaVantageAPITokenTest(){
        forexService.setForexAPItoken("mockToken");
        verify(forexService, times(1)).setForexAPItoken("mockToken");
    }

    @Test
    public void setForexSymbolsApiUrlTest(){
        forexService.setForexSymbolsApiUrl("mockUrl");
        verify(forexService, times(1)).setForexSymbolsApiUrl("mockUrl");
    }

    @Test
    public void setForexExchangeRateApiUrlTest(){
        forexService.setForexExchangeRateApiUrl("mockUrl");
        verify(forexService, times(1)).setForexExchangeRateApiUrl("mockUrl");
    }

    @Test
    public void setForexDailyApiUrlTest(){
        forexService.setForexDailyApiUrl("mockUrl");
        verify(forexService, times(1)).setForexDailyApiUrl("mockUrl");
    }


    @Test
    public void getUpdatedForexTest() {

        ListingForex forex = new ListingForex();
        forex.setPrice(135.0);
        forex.setHigh(138.0);
        forex.setLow(134.0);

        double newPrice = 140.0;
        double newHigh = 142.0;
        double newLow = 138.0;

        try (MockedStatic<Requests> req = Mockito.mockStatic(Requests.class)) {
            req.when(() -> Requests.sendRequest(any())).thenReturn("{\n" +
                    "    \"Realtime Currency Exchange Rate\": {\n" +
                    "        \"1. From_Currency Code\": \"USD\",\n" +
                    "        \"2. From_Currency Name\": \"United States Dollar\",\n" +
                    "        \"3. To_Currency Code\": \"JPY\",\n" +
                    "        \"4. To_Currency Name\": \"Japanese Yen\",\n" +
                    "        \"5. Exchange Rate\": \"" + newPrice + "\",\n" +
                    "        \"6. Last Refreshed\": \"2024-04-12 20:25:33\",\n" +
                    "        \"7. Time Zone\": \"UTC\",\n" +
                    "        \"8. Bid Price\": \"" + newLow + "\",\n" +
                    "        \"9. Ask Price\": \" " + newHigh + "\"\n" +
                    "    }\n" +
                    "}");
            ListingForex updatedForex = forexService.getUpdatedForex(forex);
            assertEquals(newPrice, updatedForex.getPrice());
            assertEquals(newHigh, updatedForex.getHigh());
            assertEquals(newLow, updatedForex.getLow());
        }
    }

    @Test
    public void initializeForexTest_JsonNull() {
        // test for initilizeForex method

        List<ListingForex> forexes = new ArrayList<>();

        //mock static method Requests.sendRequest()
        try(MockedStatic<Requests> req = Mockito.mockStatic(Requests.class)){
            req.when(() -> Requests.sendRequest(any())).thenReturn("response");
            forexes = forexService.initializeForex();
        }catch (NullPointerException e){
            assertEquals(0, forexes.size());
        }
    }

    @Test
    public void initializeForexTest_Success(){
        // test for initializeForex method

        ListingForex forex1 = new ListingForex();
        ListingForex forex2 = new ListingForex();

        List<ListingForex> target = List.of(forex1, forex2);


        // mock static method Requests.sendRequest()
        try (MockedStatic<Requests> req = Mockito.mockStatic(Requests.class)) {
            req.when(() -> Requests.sendRequest(any())).thenReturn("[\"oanda\"]");
            when(forexService.fetchAllForexPairs(any())).thenReturn(target);

            List<ListingForex> forexes = forexService.initializeForex();
            assertEquals(target, forexes);
        }
    }

    @Test
    public void fetchAllForexPairs_SuccessTWO(){
        // test for fetchAllForexPairs method

        try (MockedStatic<Requests> req = Mockito.mockStatic(Requests.class)) {
            req.when(() -> Requests.sendRequest(any())).thenReturn("[{\"description\":\"Oanda CHF/HKD\",\"displaySymbol\":\"CHF/HKD\",\"symbol\":\"OANDA:CHF_HKD\"},{\"description\":\"Oanda Silver/CHF\",\"displaySymbol\":\"XAG/CHF\",\"symbol\":\"OANDA:XAG_CHF\"}]");

            List<ListingForex> forexes = forexService.fetchAllForexPairs("oanda");
            assertEquals(2, forexes.size());
        }
    }

    @Test
    public void fetchAllForexPairs_SuccessONE(){
        // test for fetchAllForexPairs method

        try (MockedStatic<Requests> req = Mockito.mockStatic(Requests.class)) {
            req.when(() -> Requests.sendRequest(any())).thenReturn("[{\"description\":\"Oanda CHF/HKD\",\"displaySymbol\":\"CHF/HKD\",\"symbol\":\"OANDA:CHF_HKD\"},{\"description\":\"Oanda Silver/CHF\",\"displaySymbol\":\"shouldFail\",\"symbol\":\"OANDA:XAG_CHF\"}]");

            List<ListingForex> forexes = forexService.fetchAllForexPairs("oanda");
            assertEquals(1, forexes.size());
        }
    }

    @Test
    public void getAllForexHistoriesTest(){
        ListingHistory history1 = new ListingHistory();
        ListingHistory history2 = new ListingHistory();
        List<ListingHistory> histories = List.of(history1, history2);
        ListingForex forex1 = new ListingForex();
        when(forexService.getForexHistory(forex1)).thenReturn(histories);

        List<ListingHistory> allHistories = forexService.getAllForexHistories(List.of(forex1));

        assertNotNull(allHistories);
        assertEquals(2, allHistories.size());

    }
}
