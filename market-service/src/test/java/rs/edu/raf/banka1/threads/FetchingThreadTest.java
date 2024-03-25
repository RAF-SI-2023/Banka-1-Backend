package rs.edu.raf.banka1.threads;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.exceptions.APIException;
import rs.edu.raf.banka1.repositories.StockRepository;
import rs.edu.raf.banka1.services.ListingStockServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import rs.edu.raf.banka1.utils.Requests;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class FetchingThreadTest {

    @Mock
    private StockRepository stockRepository;
    @Mock
    private JsonNode dataNode;

    private ObjectMapper objectMapper;
    @Mock
    private Requests requests;
    private String DTresponseMockGood= "{\n" +
            "    \"Global Quote\": {\n" +
            "        \"01. symbol\": \"DT\",\n" +
            "        \"02. open\": \"46.4500\",\n" +
            "        \"03. high\": \"46.4800\",\n" +
            "        \"04. low\": \"45.9500\",\n" +
            "        \"05. price\": \"46.3300\",\n" +
            "        \"06. volume\": \"2370338\",\n" +
            "        \"07. latest trading day\": \"2024-03-22\",\n" +
            "        \"08. previous close\": \"46.4400\",\n" +
            "        \"09. change\": \"-0.1100\",\n" +
            "        \"10. change percent\": \"-0.2369%\"\n" +
            "    }\n" +
            "}";


    private String updateListingApiUrl = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=";
    private String alphaVantageAPIToken = "OF6BVKZOCXWHD9NS";
    private List<ListingStock> listingStocks;
    @InjectMocks
    private ListingStockServiceImpl stockService;
    @InjectMocks
    private FetchingThread fetchingThread;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        listingStocks = new ArrayList<>();
        fetchingThread = new FetchingThread(stockRepository, listingStocks, requests,updateListingApiUrl, alphaVantageAPIToken);
    }

  @Test
  public void happyTest() throws Exception,APIException {
      try(MockedStatic<Requests> requests = Mockito.mockStatic(Requests.class)) {
          requests.when(() -> Requests.sendRequest(any())).thenReturn(DTresponseMockGood);
          ListingStock stock = new ListingStock();
          stock.setTicker("DT");
          stock.setHigh(48.6900);
          stock.setLow(48.9500);
          stock.setPrice(48.3300);
          stock.setVolume(2370338);
          stock.setPriceChange(-0.1100);
          listingStocks.add(stock);

          fetchingThread.valuesForConstantUpdating();

          verify(stockRepository).updateFreshValuesStock(eq(46.4800),eq(45.9500),eq(46.3300),eq(2370338),eq(-0.1100),any(),anyInt());

      }


  }
}

