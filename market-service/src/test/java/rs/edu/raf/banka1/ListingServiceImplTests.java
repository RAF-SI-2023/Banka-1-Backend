package rs.edu.raf.banka1;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import rs.edu.raf.banka1.mapper.ListingMapper;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.Listing;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;
import rs.edu.raf.banka1.services.ListingServiceImpl;
import rs.edu.raf.banka1.utils.Constants;

import java.sql.Date;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ListingServiceImplTests {

    @Mock
    private ListingHistoryRepository listingHistoryRepository;
    @Spy
    private ListingMapper listingMapper;

    @InjectMocks
    private ListingServiceImpl listingService;

    private List<ListingHistory> lst;
    private ListingHistory model1;
    private ListingHistory model2;
    private int date;
    List<String> validTickers;
    List<String> validCompanyNames;
    List<String> validPrimaryExchanges;

    @BeforeEach
    public void setUp(){
        model1 = new ListingHistory();
        model1.setTicker("AAPL");
        model1.setDate((int) Date.valueOf("2021-01-01").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
        model1.setPrice(100.0);
        model1.setAsk(101.0);
        model1.setBid(99.0);
        model1.setChanged(0.0);
        model1.setVolume(1000);

        model2 = new ListingHistory();
        model2.setTicker("MSFT");
        model2.setDate((int)Date.valueOf("2021-01-01").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
        model2.setPrice(100.0);
        model2.setAsk(101.0);
        model2.setBid(99.0);
        model2.setChanged(0.0);
        model2.setVolume(1000);

        lst = new ArrayList<>();
        lst.add(model1);
        lst.add(model2);

        date = (int) Date.valueOf("2021-01-01").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        validTickers = List.of("DT");
        validCompanyNames = List.of("Dynatrace Inc");
        validPrimaryExchanges = List.of("NEW YORK STOCK EXCHANGE, INC.");
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

    @Test
    public void reformatNamesToJSONTestAllValid(){
        List<String> validTickers = List.of("DT", "TTNUF");
        List<String> validCompanyNames = List.of("Dynatrace Inc", "Titanium Group Limited");
        List<String> validPrimaryExchanges = List.of("NEW YORK STOCK EXCHANGE, INC.", "CAVEAT EMPTOR");
        String response =   "[\n" +
                            "    {\n" +
                            "        \"symbol\": \"" + validTickers.get(0) + "\",\n" +
                            "        \"companyName\": \"" + validCompanyNames.get(0) + "\",\n" +
                            "        \"primaryExchange\": \"" + validPrimaryExchanges.get(0) + "\"\n" +
                            "    },\n" +
                            "    {\n" +
                            "        \"symbol\": \"" + validTickers.get(1) + "\",\n" +
                            "        \"companyName\": \"" + validCompanyNames.get(1) + "\",\n" +
                            "        \"primaryExchange\": \"" + validPrimaryExchanges.get(0) + "\"\n" +
                            "    }\n" +
                            "]";

        try {
            ArrayNode jsonArray = listingService.reformatNamesToJSON(response);
            assertEquals(jsonArray.size(), validTickers.size());
            for(int i = 0; i < jsonArray.size(); i++){
                assertEquals(jsonArray.get(i).get("symbol").asText(), validTickers.get(i));
                assertEquals(jsonArray.get(i).get("companyName").asText(), validCompanyNames.get(i));
                assertEquals(jsonArray.get(i).get("primaryExchange").asText(), validPrimaryExchanges.get(0));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void reformatNamesToJSONTestInvalidSymbols(){
        List<String> invalidTickers = List.of("to-be-removed");
        List<String> invalidCompanyNames = List.of("should not be here");
        List<String> invalidPrimaryExchanges = List.of("should not be here");

        String response =   "[\n" +
                            "    {\n" +
                            "        \"symbol\": \"" + validTickers.get(0) + "\",\n" +
                            "        \"companyName\": \"" + validCompanyNames.get(0) + "\",\n" +
                            "        \"primaryExchange\": \"" + validPrimaryExchanges.get(0) + "\"\n" +
                            "    },\n" +
                            "    {\n" +
                            "        \"symbol\": \"" + invalidTickers.get(0) + "\",\n" +
                            "        \"companyName\": \"" + invalidCompanyNames.get(0) + "\",\n" +
                            "        \"primaryExchange\": \"" + invalidPrimaryExchanges.get(0) + "\"\n" +
                            "    }\n" +
                            "]";

        try {
            ArrayNode jsonArray = listingService.reformatNamesToJSON(response);
            assertEquals(jsonArray.size(), validTickers.size());
            for(int i = 0; i < jsonArray.size(); i++){
                assertEquals(jsonArray.get(i).get("symbol").asText(), validTickers.get(i));
                assertEquals(jsonArray.get(i).get("companyName").asText(), validCompanyNames.get(i));
                assertEquals(jsonArray.get(i).get("primaryExchange").asText(), validPrimaryExchanges.get(0));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void reformatNamesToJSONTestSymbolsToIgnore(){
        List<String> ignoredTickers = List.of(Constants.ListingsToIgnore.get(0));
        List<String> ignoredCompanyNames = List.of("should not be here");
        List<String> ignoredPrimaryExchanges = List.of("should not be here");

        String response =   "[\n" +
                            "    {\n" +
                            "        \"symbol\": \"" + validTickers.get(0) + "\",\n" +
                            "        \"companyName\": \"" + validCompanyNames.get(0) + "\",\n" +
                            "        \"primaryExchange\": \"" + validPrimaryExchanges.get(0) + "\"\n" +
                            "    },\n" +
                            "    {\n" +
                            "        \"symbol\": \"" + ignoredTickers.get(0) + "\",\n" +
                            "        \"companyName\": \"" + ignoredCompanyNames.get(0) + "\",\n" +
                            "        \"primaryExchange\": \"" + ignoredPrimaryExchanges.get(0) + "\"\n" +
                            "    }\n" +
                            "]";

        try {
            ArrayNode jsonArray = listingService.reformatNamesToJSON(response);
            assertEquals(jsonArray.size(), validTickers.size());
            for(int i = 0; i < jsonArray.size(); i++){
                assertEquals(jsonArray.get(i).get("symbol").asText(), validTickers.get(i));
                assertEquals(jsonArray.get(i).get("companyName").asText(), validCompanyNames.get(i));
                assertEquals(jsonArray.get(i).get("primaryExchange").asText(), validPrimaryExchanges.get(0));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void reformatNamesToJSONTestEmptyResponse(){
        String response = "[]";
        try {
            ArrayNode jsonArray = listingService.reformatNamesToJSON(response);
            assertEquals(jsonArray.size(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void updatelistingModelFieldsTest() {
        Listing listing = new Listing();
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode globalQuoteNode = mock(JsonNode.class);

        double high = 100.0;
        double low = 50.0;
        double price = 75.0;
        int volume = 1000;
        double change = 25.0;

        try (MockedStatic<JsonNode> jsonNodeMockedStatic = mockStatic(JsonNode.class)) {
            // Mocking behavior of rootNode and its child
            jsonNodeMockedStatic.when(() -> rootNode.get("Global Quote")).thenReturn(globalQuoteNode);
            when(globalQuoteNode.get("03. high")).thenReturn(mock(JsonNode.class));
            when(globalQuoteNode.get("03. high").asDouble()).thenReturn(high);
            when(globalQuoteNode.get("04. low")).thenReturn(mock(JsonNode.class));
            when(globalQuoteNode.get("04. low").asDouble()).thenReturn(low);
            when(globalQuoteNode.get("05. price")).thenReturn(mock(JsonNode.class));
            when(globalQuoteNode.get("05. price").asDouble()).thenReturn(price);
            when(globalQuoteNode.get("06. volume")).thenReturn(mock(JsonNode.class));
            when(globalQuoteNode.get("06. volume").asInt()).thenReturn(volume);
            when(globalQuoteNode.get("09. change")).thenReturn(mock(JsonNode.class));
            when(globalQuoteNode.get("09. change").asDouble()).thenReturn(change);

            listingService.updatelistingModelFields(listing, rootNode);

            verify(listingMapper).listingModelUpdate(any(), eq(price), eq(high), eq(low), eq(change), eq(volume));


            // Assertion
            assertEquals(price, listing.getPrice());
            assertEquals(low, listing.getBid());
            assertEquals(high, listing.getAsk());
            assertEquals(change, listing.getChanged());
            assertEquals(volume, listing.getVolume());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void createListingHistoryModelFromJsonTest(){
        // Mock necessary objects
        JsonNode dataNode = mock(JsonNode.class);
        int unixTimestamp = 1612137600;

        double open = 50.0;
        double close = 45.0;
        double high = 50.0;
        double low = 40.0;
        int volume = 1000;
        String ticker = "AAPL";


        // Stubbing behavior for dataNode.get calls
        when(dataNode.get("1. open")).thenReturn(mock(JsonNode.class));
        when(dataNode.get("1. open").asDouble()).thenReturn(open);
        when(dataNode.get("2. high")).thenReturn(mock(JsonNode.class));
        when(dataNode.get("2. high").asDouble()).thenReturn(high);
        when(dataNode.get("3. low")).thenReturn(mock(JsonNode.class));
        when(dataNode.get("3. low").asDouble()).thenReturn(low);
        when(dataNode.get("4. close")).thenReturn(mock(JsonNode.class));
        when(dataNode.get("4. close").asDouble()).thenReturn(close);
        when(dataNode.get("5. volume")).thenReturn(mock(JsonNode.class));
        when(dataNode.get("5. volume").asInt()).thenReturn(volume);

        // Call the method
        ListingHistory actualModel = listingService.createListingHistoryModelFromJson(dataNode, ticker, unixTimestamp);

        // Assertions
        assertEquals(close, actualModel.getPrice());
        assertEquals(low, actualModel.getBid());
        assertEquals(high, actualModel.getAsk());
        assertEquals(close - open, actualModel.getChanged());
        assertEquals(volume, actualModel.getVolume());
        assertEquals(unixTimestamp, actualModel.getDate());
        assertEquals(ticker, actualModel.getTicker());


    }


}
