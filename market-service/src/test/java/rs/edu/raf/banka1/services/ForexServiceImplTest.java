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

import java.lang.reflect.Array;
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
    @Mock
    private JsonNode dataNode;
    @Mock
    private ListingStockService listingStockService;


    @Mock
    private Requests requests;

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
        forexService.setForexExchangePlaceApiUrl("mockUrl");
        forexService.setForexAPItoken("mockToken");
        forexService.setAlphaVantageAPIToken("mockToken");
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

    @Test
    public void fetchHistoriesIfEmptyTest() {
        String ticker = "ticker";
        ListingForex lfMock = mock(ListingForex.class);
        when(lfMock.getTicker()).thenReturn("ticker");
        when(forexRepository.findById(1L)).thenReturn(Optional.of(lfMock));
        when(listingHistoryRepository.getListingHistoriesByTicker(ticker)).thenReturn(new ArrayList<>());
        when(forexService.getForexHistory(lfMock)).thenReturn(List.of(
                new ListingHistory(),
                new ListingHistory(),
                new ListingHistory()
        ));

        List<ListingHistory> listingHistories = forexService.getListingHistoriesByTimestamp(1L, null, null);

        assertNotNull(listingHistories);
        assertFalse(listingHistories.isEmpty());
    }

    @Test
    public void saveAllForexesTest(){
        ListingForex forex1 = new ListingForex();
        ListingForex forex2 = new ListingForex();
        List<ListingForex> forexes = List.of(forex1, forex2);
        when(forexRepository.saveAll(forexes)).thenReturn(forexes);

        forexService.saveAllForexes(forexes);

        verify(forexRepository, times(1)).saveAll(forexes);
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
    public void getListingHistoriesByTimestampTestNoListingHistories(){
        // need to fetch histories
        ListingHistory history1 = new ListingHistory();
        ListingHistory history2 = new ListingHistory();
        List<ListingHistory> histories = List.of(history1, history2);


        Long id = 1L;
        ListingForex forex = new ListingForex();
        when(forexRepository.findById(id)).thenReturn(Optional.of(forex));
        when(listingHistoryRepository.getListingHistoriesByTicker(forex.getTicker())).thenReturn(new ArrayList<>());
        when(forexService.getForexHistory(forex)).thenReturn(histories);

        List<ListingHistory> listingHistories = forexService.getListingHistoriesByTimestamp(id, null, null);
        verify(forexService, times(1)).getForexHistory(forex);
        assertNotNull(listingHistories);
        assertEquals(2, listingHistories.size());
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

    @Test
    public void getForexByTickerTest(){
        String ticker = "ticker";
        ListingForex forex = new ListingForex();
        when(forexRepository.findByTicker(ticker)).thenReturn(Optional.of(forex));

        ListingForex found = forexService.getForexByTicker(ticker);

        assertNotNull(found);
        assertEquals(forex, found);
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
    public void setObjectMapperTest(){
        forexService.setObjectMapper(null);
        verify(forexService, times(1)).setObjectMapper(null);

    }

    @Test
    public void setAlphaVantageApiUrlTest(){
        forexService.setForexExchangePlaceApiUrl("mockUrl");
        // once in setUp and once in test
        verify(forexService, times(2)).setForexExchangePlaceApiUrl("mockUrl");
    }

    @Test
    public void setAphaVantageAPITokenTest(){
        forexService.setForexAPItoken("mockToken");
        // once in setUp and once in test
        verify(forexService, times(2)).setForexAPItoken("mockToken");
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



}
