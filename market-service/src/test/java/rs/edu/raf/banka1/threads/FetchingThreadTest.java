package rs.edu.raf.banka1.threads;
/*
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import rs.edu.raf.banka1.mapper.StockMapper;
import rs.edu.raf.banka1.model.ListingStock;;
import rs.edu.raf.banka1.repositories.StockRepository;
import rs.edu.raf.banka1.services.ListingStockServiceImpl;
import rs.edu.raf.banka1.utils.Requests;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


class FetchingThreadTest {

    @Mock
    private StockRepository stockRepository;
    @Mock
    private JsonNode dataNode;
    @Mock
    private Requests requests;
    private String DTresponseMockGood= "{\n" +
            "    \"Global Quote\": {\n" +
            "        \"01. symbol\": \"DT\",\n" +
            "        \"02. open\": \"46.4500\",\n" +
            "        \"03. high\": \"46.6900\",\n" +
            "        \"04. low\": \"45.9500\",\n" +
            "        \"05. price\": \"46.3300\",\n" +
            "        \"06. volume\": \"2370338\",\n" +
            "        \"07. latest trading day\": \"2024-03-22\",\n" +
            "        \"08. previous close\": \"46.4400\",\n" +
            "        \"09. change\": \"-0.1100\",\n" +
            "        \"10. change percent\": \"-0.2369%\"\n" +
            "    }\n" +
            "}";


    @InjectMocks
    private FetchingThread fetchingThread;
    @Spy
    private ListingStockServiceImpl stockService;

    @BeforeEach
    public void setUp() {
        StockMapper stockMapper = new StockMapper();
        stockService.setStockMapper(stockMapper);
        stockService.setStockRepository(stockRepository);
        fetchingThread = new FetchingThread(stockRepository, null, requests,"https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=", "OF6BVKZOCXWHD9NS");
    }
    @Test
    public void testValuesForConstantUpdating() throws Exception {
        double price = 135.0;
        double high = 138.0;
        double low = 134.0;
        double change = 140.0;
        int volume = 9;

        // Mock ListingStock instance
        ListingStock oldlistingStock = new ListingStock();
        oldlistingStock.setHigh(high);
        oldlistingStock.setLow(low);
        oldlistingStock.setVolume(volume);
        oldlistingStock.setPriceChange(change);
        oldlistingStock.setPrice(price);

      //  when(Requests.sendRequest(anyString())).thenReturn(DTresponseMockGood);
        // Call the method to be tested
        fetchingThread.valuesForConstantUpdating();

        // Verify that requests.sendRequest() was called with the correct URL
        verify(requests, times(1)).sendRequest(anyString());

        // Verify that stockRepository.updateFreshValuesStock() was called with the correct arguments
        verify(stockRepository, times(1)).updateFreshValuesStock(eq(160.0), eq(140.0), eq(155.0), eq(100000), eq(5.0), anyLong(), anyInt());
    }
}

*/