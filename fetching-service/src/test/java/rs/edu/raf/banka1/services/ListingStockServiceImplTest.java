package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import  org.springframework.core.io.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import rs.edu.raf.banka1.mapper.StockMapper;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.entities.Country;
import rs.edu.raf.banka1.model.entities.Exchange;
import rs.edu.raf.banka1.model.entities.Holiday;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.utils.Requests;


import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ListingStockServiceImplTest {

    private ListingHistoryRepository listingHistoryRepository;

    private StockRepository stockRepository;

    private ExchangeRepository exchangeRepository;

    private StockMapper stockMapper;

    private ObjectMapper objectMapper;

    private String updateListingApiUrl;
    private String listingAPItoken;
    private String alphaVantageAPIToken;
    private String listingNameApiUrl;
    private String basicStockInfoApiUrl;
    private String HistoryListingApiUrl;
    @Mock
    private Resource resource;

    @Mock
    private JsonNode jsonNode;


    private ListingStockServiceImpl listingStockService;

    private ListingStock stockAAPL;
    private ListingStock stockMSFT;
    private ListingStock stockDT;
    private List<ListingStock> stocks;
    private Exchange exchangeDT;
    private Country countryUS;

    private List<ListingHistory> lst = new ArrayList<>();
    private ListingHistory model1;
    private ListingHistory model2;
    private long date;

    @BeforeEach
    public void setUp(){

        listingHistoryRepository = mock(ListingHistoryRepository.class);
        stockRepository = mock(StockRepository.class);
        exchangeRepository = mock(ExchangeRepository.class);
        stockMapper = mock(StockMapper.class);
        listingStockService = new ListingStockServiceImpl();
        listingStockService.setStockRepository(stockRepository);
        listingStockService.setListingHistoryRepository(listingHistoryRepository);
        listingStockService.setExchangeRepository(exchangeRepository);
        listingStockService.setStockMapper(stockMapper);

        objectMapper = new ObjectMapper();

        // stock data
        stockAAPL = new ListingStock();
        stockAAPL.setTicker("AAPL");
        stockAAPL.setPrice(100.0);

        stockMSFT = new ListingStock();
        stockMSFT.setTicker("MSFT");
        stockMSFT.setPrice(200.0);

        SimpleDateFormat hoursDateFormat = new SimpleDateFormat("HH:mm:ss");
        countryUS = new Country();
        countryUS.setISOCode("US");
        countryUS.setId(113L);
        countryUS.setTimezoneOffset(0);
        try {
            countryUS.setOpenTime(new java.util.Date(hoursDateFormat.parse("08:00:00").getTime()));
            countryUS.setCloseTime(new java.util.Date(hoursDateFormat.parse("18:00:00").getTime()));

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        exchangeDT = new Exchange();
        exchangeDT.setId(2295L);
        exchangeDT.setExchangeName("NEW YORK STOCK EXCHANGE, INC.");
        exchangeDT.setMicCode("XNYS");
        exchangeDT.setCountry(countryUS);

        stockDT = new ListingStock();
        stockDT.setTicker("DT");
        stockDT.setName("Dynatrace Holdings LLC");
        stockDT.setListingType("Stock");
        stockDT.setExchangeName("NEW YORK STOCK EXCHANGE, INC.");
        stockDT.setListingId(1L);
        stockDT.setExchange(exchangeDT);
        stockDT.setVolume(2566374);
        stockDT.setPriceChange(-0.705);
        stockDT.setPrice(45.735);
        stockDT.setOutstandingShares(295999000);
        stockDT.setLow(45.31);
        stockDT.setLastRefresh(1712005154);
        stockDT.setHigh(46.29);
        stockDT.setDividendYield((double) 0);

        stocks = new ArrayList<>();
        stocks.add(stockAAPL);
        stocks.add(stockMSFT);
        stocks.add(stockDT);

        // history data
        model1 = new ListingHistory();
        model1.setTicker("AAPL");
        model1.setDate((int) Date.valueOf("2021-01-01").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
        model1.setPrice(100.0);
        model1.setChanged(2.0);
        model1.setChanged(0.0);
        model1.setVolume(1000);

        model2 = new ListingHistory();
        model2.setTicker("MSFT");
        model2.setDate((int) Date.valueOf("2021-01-01").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
        model2.setPrice(100.0);
        model2.setChanged(2.0);
        model2.setChanged(0.0);
        model2.setVolume(1000);

        lst.add(model1);
        lst.add(model2);

        date = Date.valueOf("2021-01-01").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        listingNameApiUrl="https://api.iex.cloud/v1/data/core/stock_collection/sector?collectionName=";
        updateListingApiUrl="https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=";
        basicStockInfoApiUrl="https://www.alphavantage.co/query?function=OVERVIEW&symbol=";
        HistoryListingApiUrl="https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=";

        listingAPItoken="pk_f87286e075c94cc484405da70691c030";
        alphaVantageAPIToken="OF6BVKZOCXWHD9NS";

        listingStockService.setAlphaVantageAPIToken(alphaVantageAPIToken);
        listingStockService.setListingAPItoken(listingAPItoken);
        listingStockService.setListingNameApiUrl(listingNameApiUrl);
        listingStockService.setUpdateListingApiUrl(updateListingApiUrl);
        listingStockService.setBasicStockInfoApiUrl(basicStockInfoApiUrl);
        listingStockService.setHistoryListingApiUrl(HistoryListingApiUrl);
    }

    @Test
    public void addListingToHistoryNotPresentTest(){
        when(listingHistoryRepository.findByTickerAndDate("AAPL", model1.getDate())).thenReturn(Optional.empty());
        assertEquals(1, listingStockService.addListingToHistory(model1));
    }

    @Test
    public void addListingToHistoryPresentTest(){
        ListingHistory listingHistory = new ListingHistory();
        listingHistory.setTicker("AAPL");
        listingHistory.setDate(date);
        listingHistory.setPrice(100.0);
        listingHistory.setChanged(2.0);
        listingHistory.setChanged(0.0);
        listingHistory.setVolume(1000);

        ListingHistory updateModel = new ListingHistory();
        updateModel.setTicker("AAPL");
        updateModel.setDate(date);
        updateModel.setPrice(700.0);
        listingHistory.setChanged(2.0);
        updateModel.setChanged(1.0);
        updateModel.setVolume(10000);

        when(listingHistoryRepository.findByTickerAndDate("AAPL", date)).thenReturn(Optional.of(listingHistory));
        assertEquals(0, listingStockService.addListingToHistory(updateModel));
    }

    @Test
    public void addAllListingsToHistoryEveryPresentTest(){
        when(listingHistoryRepository.findByTickerAndDate("AAPL", model1.getDate())).thenReturn(Optional.of(model1));
        when(listingHistoryRepository.findByTickerAndDate("MSFT", model2.getDate())).thenReturn(Optional.of(model2));
        assertEquals(0, listingStockService.addAllListingsToHistory(lst));
    }

    @Test
    public void addAllListingsToHistoryNothingPresentTest(){
        when(listingHistoryRepository.findByTickerAndDate("AAPL", model1.getDate())).thenReturn(Optional.empty());
        when(listingHistoryRepository.findByTickerAndDate("MSFT", model2.getDate())).thenReturn(Optional.empty());
        assertEquals(lst.size(), listingStockService.addAllListingsToHistory(lst));
    }


    @Test
    public void testFetchSingleListingHistory() {
        // Arrange
        String ticker = "AAPL";
        String apiUrl = listingStockService.getHistoryListingApiUrl() + ticker + "&outputsize=compact&apikey=" + listingStockService.getListingAPItoken();
        String response = "{\n" +
                "    \"Meta Data\": {\n" +
                "        \"1. Information\": \"Daily Prices (open, high, low, close) and Volumes\",\n" +
                "        \"2. Symbol\": \"AAPL\",\n" +
                "        \"3. Last Refreshed\": \"2024-04-12\",\n" +
                "        \"4. Output Size\": \"Compact\",\n" +
                "        \"5. Time Zone\": \"US/Eastern\"\n" +
                "    },\n" +
                "    \"Time Series (Daily)\": {\n" +
                "        \"2024-04-12\": {\n" +
                "            \"1. open\": \"174.2600\",\n" +
                "            \"2. high\": \"178.3600\",\n" +
                "            \"3. low\": \"174.2100\",\n" +
                "            \"4. close\": \"176.5500\",\n" +
                "            \"5. volume\": \"101670886\"\n" +
                "        },\n" +
                "        \"2024-04-11\": {\n" +
                "            \"1. open\": \"168.3400\",\n" +
                "            \"2. high\": \"175.4600\",\n" +
                "            \"3. low\": \"168.1600\",\n" +
                "            \"4. close\": \"175.0400\",\n" +
                "            \"5. volume\": \"91070275\"\n" +
                "        }}}";

        try(MockedStatic<Requests> req = Mockito.mockStatic(Requests.class)) {
            req.when(() -> Requests.sendRequest(any())).thenReturn(response);
            List<ListingHistory> result = listingStockService.fetchSingleListingHistory(ticker);

            assertEquals(2, result.size());
        }

    }

    @Test
    public void addListingStockNotPresentTest(){
        when(stockRepository.findByTicker("AAPL")).thenReturn(Optional.empty());
        assertEquals(1, listingStockService.addListingStock(stockAAPL));
    }

    @Test
    public void addListingStockPresentTest(){
        ListingStock updateStock = new ListingStock();
        updateStock.setTicker("AAPL");
        updateStock.setPrice(101.0);
        when(listingStockService.findByTicker("AAPL")).thenReturn(Optional.of(stockAAPL));
        assertEquals(0, listingStockService.addListingStock(updateStock));
    }

    @Test
    public void addAllListingStocksPresentTests(){
        when(listingStockService.findByTicker("AAPL")).thenReturn(Optional.of(stockAAPL));
        when(listingStockService.findByTicker("MSFT")).thenReturn(Optional.of(stockMSFT));
        assertEquals(1, listingStockService.addAllListingStocks(stocks));
    }

    @Test
    public void fetchNListingStocks() {
        String baseResponse = "{\n" +
                "    \"Global Quote\": {\n" +
                "        \"01. symbol\": \"CDLX\",\n" +
                "        \"02. open\": \"14.1800\",\n" +
                "        \"03. high\": \"14.2300\",\n" +
                "        \"04. low\": \"13.5500\",\n" +
                "        \"05. price\": \"13.5700\",\n" +
                "        \"06. volume\": \"650480\",\n" +
                "        \"07. latest trading day\": \"2024-04-12\",\n" +
                "        \"08. previous close\": \"14.3800\",\n" +
                "        \"09. change\": \"-0.8100\",\n" +
                "        \"10. change percent\": \"-5.6328%\"\n" +
                "    }\n" +
                "}";
        String updateResponse = "{\n" +
                "    \"Symbol\": \"CDLX\",\n" +
                "    \"AssetType\": \"Common Stock\",\n" +
                "    \"Name\": \"Cardlytics Inc\",\n" +
                "    \"Description\": \"Cardlytics, Inc. operates an advertising platform within financial institutions' digital channels including online, mobile, email, and various real-time notifications in the United States and the United Kingdom. The company is headquartered in Atlanta, Georgia.\",\n" +
                "    \"CIK\": \"1666071\",\n" +
                "    \"Exchange\": \"NASDAQ\",\n" +
                "    \"Currency\": \"USD\",\n" +
                "    \"Country\": \"USA\",\n" +
                "    \"Sector\": \"TECHNOLOGY\",\n" +
                "    \"Industry\": \"SERVICES-COMPUTER PROGRAMMING, DATA PROCESSING, ETC.\",\n" +
                "    \"Address\": \"675 PONCE DE LEON AVENUE, NE, SUITE 6000, ATLANTA, GA, US\",\n" +
                "    \"FiscalYearEnd\": \"December\",\n" +
                "    \"LatestQuarter\": \"2023-12-31\",\n" +
                "    \"MarketCapitalization\": \"598560000\",\n" +
                "    \"EBITDA\": \"-37209000\",\n" +
                "    \"PERatio\": \"None\",\n" +
                "    \"PEGRatio\": \"None\",\n" +
                "    \"BookValue\": \"3.393\",\n" +
                "    \"DividendPerShare\": \"None\",\n" +
                "    \"DividendYield\": \"None\",\n" +
                "    \"EPS\": \"-3.69\",\n" +
                "    \"RevenuePerShareTTM\": \"8.47\",\n" +
                "    \"ProfitMargin\": \"-0.436\",\n" +
                "    \"OperatingMarginTTM\": \"-0.0867\",\n" +
                "    \"ReturnOnAssetsTTM\": \"-0.0629\",\n" +
                "    \"ReturnOnEquityTTM\": \"-0.778\",\n" +
                "    \"RevenueTTM\": \"309204000\",\n" +
                "    \"GrossProfitTTM\": \"115415000\",\n" +
                "    \"DilutedEPSTTM\": \"-3.69\",\n" +
                "    \"QuarterlyEarningsGrowthYOY\": \"-0.567\",\n" +
                "    \"QuarterlyRevenueGrowthYOY\": \"0.081\",\n" +
                "    \"AnalystTargetPrice\": \"17.67\",\n" +
                "    \"AnalystRatingStrongBuy\": \"0\",\n" +
                "    \"AnalystRatingBuy\": \"2\",\n" +
                "    \"AnalystRatingHold\": \"1\",\n" +
                "    \"AnalystRatingSell\": \"0\",\n" +
                "    \"AnalystRatingStrongSell\": \"0\",\n" +
                "    \"TrailingPE\": \"-\",\n" +
                "    \"ForwardPE\": \"-\",\n" +
                "    \"PriceToSalesRatioTTM\": \"1.936\",\n" +
                "    \"PriceToBookRatio\": \"4.44\",\n" +
                "    \"EVToRevenue\": \"2.499\",\n" +
                "    \"EVToEBITDA\": \"-7.81\",\n" +
                "    \"Beta\": \"1.466\",\n" +
                "    \"52WeekHigh\": \"20.52\",\n" +
                "    \"52WeekLow\": \"4.94\",\n" +
                "    \"50DayMovingAverage\": \"10.15\",\n" +
                "    \"200DayMovingAverage\": \"10.65\",\n" +
                "    \"SharesOutstanding\": \"44109100\",\n" +
                "    \"DividendDate\": \"None\",\n" +
                "    \"ExDividendDate\": \"None\"\n" +
                "}";
        String baseUrl = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=CDLX&apikey=OF6BVKZOCXWHD9NS";
        String updateUrl = "https://www.alphavantage.co/query?function=OVERVIEW&symbol=CDLX&apikey=OF6BVKZOCXWHD9NS";

        ObjectMapper objectMapper1 = new ObjectMapper();
        listingStockService.setObjectMapper(objectMapper1);

        StockMapper stockMapper1 = new StockMapper();
        listingStockService.setStockMapper(stockMapper1);
        int n = 3;


        try (MockedStatic<Requests> req = Mockito.mockStatic(Requests.class)) {
            req.when(() -> Requests.sendRequest(startsWith("https://www.alphavantage.co/query?function=GLOBAL_QUOTE"))).thenReturn(baseResponse);
            req.when(() -> Requests.sendRequest(startsWith("https://www.alphavantage.co/query?function=OVERVIEW"))).thenReturn(updateResponse);

            when(exchangeRepository.findByExchangeName(any())).thenReturn(exchangeDT);
            when(exchangeRepository.findByExchangeName(any())).thenReturn(exchangeDT);
            when(exchangeRepository.findByExchangeName(any())).thenReturn(exchangeDT);

            List<ListingStock> stocks = listingStockService.fetchNListingStocks(n);
            assertEquals(3, stocks.size());
        }
    }

    @Test
    public void testFetchNStocks() {
        List<ListingStock> allStocks = new ArrayList<>();
        allStocks.add(new ListingStock());
        allStocks.add(new ListingStock());

        when(listingStockService.getAllStocks()).thenReturn(allStocks);

        int n = 2;
        List<ListingStock> fetchedStocks = listingStockService.fetchNStocks(n);

        assertEquals(n, fetchedStocks.size());
    }

    @Test
    public void testCreateListingStock() {
        // Arrange
        String baseResponse = "{\n" +
                "    \"Global Quote\": {\n" +
                "        \"01. symbol\": \"CDLX\",\n" +
                "        \"02. open\": \"14.1800\",\n" +
                "        \"03. high\": \"14.2300\",\n" +
                "        \"04. low\": \"13.5500\",\n" +
                "        \"05. price\": \"13.5700\",\n" +
                "        \"06. volume\": \"650480\",\n" +
                "        \"07. latest trading day\": \"2024-04-12\",\n" +
                "        \"08. previous close\": \"14.3800\",\n" +
                "        \"09. change\": \"-0.8100\",\n" +
                "        \"10. change percent\": \"-5.6328%\"\n" +
                "    }\n" +
                "}";
        String updateResponse = "{\n" +
                "    \"Symbol\": \"CDLX\",\n" +
                "    \"AssetType\": \"Common Stock\",\n" +
                "    \"Name\": \"Cardlytics Inc\",\n" +
                "    \"Description\": \"Cardlytics, Inc. operates an advertising platform within financial institutions' digital channels including online, mobile, email, and various real-time notifications in the United States and the United Kingdom. The company is headquartered in Atlanta, Georgia.\",\n" +
                "    \"CIK\": \"1666071\",\n" +
                "    \"Exchange\": \"NASDAQ\",\n" +
                "    \"Currency\": \"USD\",\n" +
                "    \"Country\": \"USA\",\n" +
                "    \"Sector\": \"TECHNOLOGY\",\n" +
                "    \"Industry\": \"SERVICES-COMPUTER PROGRAMMING, DATA PROCESSING, ETC.\",\n" +
                "    \"Address\": \"675 PONCE DE LEON AVENUE, NE, SUITE 6000, ATLANTA, GA, US\",\n" +
                "    \"FiscalYearEnd\": \"December\",\n" +
                "    \"LatestQuarter\": \"2023-12-31\",\n" +
                "    \"MarketCapitalization\": \"598560000\",\n" +
                "    \"EBITDA\": \"-37209000\",\n" +
                "    \"PERatio\": \"None\",\n" +
                "    \"PEGRatio\": \"None\",\n" +
                "    \"BookValue\": \"3.393\",\n" +
                "    \"DividendPerShare\": \"None\",\n" +
                "    \"DividendYield\": \"None\",\n" +
                "    \"EPS\": \"-3.69\",\n" +
                "    \"RevenuePerShareTTM\": \"8.47\",\n" +
                "    \"ProfitMargin\": \"-0.436\",\n" +
                "    \"OperatingMarginTTM\": \"-0.0867\",\n" +
                "    \"ReturnOnAssetsTTM\": \"-0.0629\",\n" +
                "    \"ReturnOnEquityTTM\": \"-0.778\",\n" +
                "    \"RevenueTTM\": \"309204000\",\n" +
                "    \"GrossProfitTTM\": \"115415000\",\n" +
                "    \"DilutedEPSTTM\": \"-3.69\",\n" +
                "    \"QuarterlyEarningsGrowthYOY\": \"-0.567\",\n" +
                "    \"QuarterlyRevenueGrowthYOY\": \"0.081\",\n" +
                "    \"AnalystTargetPrice\": \"17.67\",\n" +
                "    \"AnalystRatingStrongBuy\": \"0\",\n" +
                "    \"AnalystRatingBuy\": \"2\",\n" +
                "    \"AnalystRatingHold\": \"1\",\n" +
                "    \"AnalystRatingSell\": \"0\",\n" +
                "    \"AnalystRatingStrongSell\": \"0\",\n" +
                "    \"TrailingPE\": \"-\",\n" +
                "    \"ForwardPE\": \"-\",\n" +
                "    \"PriceToSalesRatioTTM\": \"1.936\",\n" +
                "    \"PriceToBookRatio\": \"4.44\",\n" +
                "    \"EVToRevenue\": \"2.499\",\n" +
                "    \"EVToEBITDA\": \"-7.81\",\n" +
                "    \"Beta\": \"1.466\",\n" +
                "    \"52WeekHigh\": \"20.52\",\n" +
                "    \"52WeekLow\": \"4.94\",\n" +
                "    \"50DayMovingAverage\": \"10.15\",\n" +
                "    \"200DayMovingAverage\": \"10.65\",\n" +
                "    \"SharesOutstanding\": \"44109100\",\n" +
                "    \"DividendDate\": \"None\",\n" +
                "    \"ExDividendDate\": \"None\"\n" +
                "}";
        String baseUrl = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=CDLX&apikey=OF6BVKZOCXWHD9NS";
        String updateUrl = "https://www.alphavantage.co/query?function=OVERVIEW&symbol=CDLX&apikey=OF6BVKZOCXWHD9NS";
        ListingStock stock = new ListingStock();
        String symbol = "CDLX";
        String companyName = "Cardlytics Inc";
        String primaryExchange = "EURONEXT  UK - REPORTING SERVICES";
        stock.setTicker(symbol);
        stock.setName(companyName);
        stock.setExchangeName(primaryExchange);
        stock.setListingId(90L);
        Double high = 200.0;
        stock.setHigh(high);
        stock.setLow(100.0);
        stock.setPrice(150.0);
        stock.setPriceChange(10.0);
        stock.setVolume(100);
        stock.setDividendYield(90.0);
        stock.setOutstandingShares(90);



        try (MockedStatic<Requests> req = Mockito.mockStatic(Requests.class)) {
            req.when(() -> Requests.sendRequest(eq(baseUrl))).thenReturn(baseResponse);
            req.when(() -> Requests.sendRequest(eq(updateUrl))).thenReturn(updateResponse);

            when(exchangeRepository.findByExchangeName(anyString())).thenReturn(exchangeDT);
            when(stockMapper.createListingStock(anyString(),anyString(),any(),anyDouble(),anyDouble(),anyDouble(),anyDouble(),anyInt(),anyInt(),anyDouble())).thenReturn(stock);
            ListingStock result = listingStockService.createListingStock(symbol, companyName, primaryExchange);

            // Assert
            verify(stockMapper,times(1)).createListingStock( anyString(),anyString(),any(),anyDouble(),anyDouble(),anyDouble(),anyDouble(),anyInt(),anyInt(),anyDouble());
            assertEquals(high,200);
            assertEquals(symbol, result.getTicker());
            assertEquals(companyName, result.getName());
            assertEquals(primaryExchange, result.getExchangeName());

        }

    }

    @Test
    public void testReformatNamesToJSON() throws Exception {
        // Arrange
        String response = "[{\"symbol\":\"AAPL\",\"companyName\":\"Apple Inc.\",\"primaryExchange\":\"NASDAQ\"}," +
                "{\"symbol\":\"MSFT\",\"companyName\":\"Microsoft Corporation\",\"primaryExchange\":\"NASDAQ\"}]";

        // Act
        ArrayNode result = listingStockService.reformatNamesToJSON(response);

        // Assert
        assertEquals(2, result.size());

        JsonNode firstNode = result.get(0);
        assertEquals("AAPL", firstNode.get("symbol").asText());
        assertEquals("Apple Inc.", firstNode.get("companyName").asText());
        assertEquals("NASDAQ", firstNode.get("primaryExchange").asText());

        JsonNode secondNode = result.get(1);
        assertEquals("MSFT", secondNode.get("symbol").asText());
        assertEquals("Microsoft Corporation", secondNode.get("companyName").asText());
        assertEquals("NASDAQ", secondNode.get("primaryExchange").asText());
    }

    @Test
    public void fetchNListingsHistoryTest(){
        ListingStock stock1 = new ListingStock();
        stock1.setListingId(9L);
        stock1.setTicker("IBM");
        ListingStock stock2 = new ListingStock();
        stock2.setListingId(10L);
        stock2.setTicker("IBM2");
        List<ListingStock> stocks = new ArrayList<>();
        stocks.add(stock1);
        stocks.add(stock2);
        int n = 2;

        ObjectMapper objectMapper1 = new ObjectMapper();
        listingStockService.setObjectMapper(objectMapper1);

        String response1 = "{\n" +
                "    \"Meta Data\": {\n" +
                "        \"1. Information\": \"Daily Prices (open, high, low, close) and Volumes\",\n" +
                "        \"2. Symbol\": \"IBM\",\n" +
                "        \"3. Last Refreshed\": \"2024-04-15\",\n" +
                "        \"4. Output Size\": \"Compact\",\n" +
                "        \"5. Time Zone\": \"US/Eastern\"\n" +
                "    },\n" +
                "    \"Time Series (Daily)\": {\n" +
                "        \"2024-04-15\": {\n" +
                "            \"1. open\": \"185.5000\",\n" +
                "            \"2. high\": \"187.4800\",\n" +
                "            \"3. low\": \"180.8800\",\n" +
                "            \"4. close\": \"181.2500\",\n" +
                "            \"5. volume\": \"3527613\"\n" +
                "        },\n" +
                "        \"2024-04-12\": {\n" +
                "            \"1. open\": \"184.0000\",\n" +
                "            \"2. high\": \"185.1699\",\n" +
                "            \"3. low\": \"181.6850\",\n" +
                "            \"4. close\": \"182.2700\",\n" +
                "            \"5. volume\": \"3547378\"\n" +
                "        }}}";

        when(stockRepository.findAll()).thenReturn(stocks);
        try (MockedStatic<Requests> req = Mockito.mockStatic(Requests.class)) {
            req.when(() -> Requests.sendRequest(any())).thenReturn(response1);

            List<ListingHistory> listingHistories = listingStockService.fetchNListingsHistory(n);

            assertEquals(4,listingHistories.size());
        }

    }


    @Test
    public void fetchNListingsHistory_EmptyStockList() {
        when(stockRepository.findAll()).thenReturn(Collections.emptyList());
        List<ListingHistory> listingHistories = listingStockService.fetchNListingsHistory(2);
        assertEquals(0, listingHistories.size());
    }


    @Test
    public void testGetRequest(){
        Requests result = new Requests();
        listingStockService.setRequests(result);
        assertEquals(listingStockService.getRequests(),result);
    }

    @Test
    public void testCreateListingStock_APIException() throws Exception {
        String symbol = "INVALID";
        String companyName = "Invalid Company";
        String primaryExchange = "INVALID_EXCHANGE";

        ListingStock actualListingStock = listingStockService.createListingStock(symbol, companyName, primaryExchange);

        assertNull(actualListingStock);
    }


    @Test
    public void testSaveAllListingStocks() {
        List<ListingStock> listingStocks = new ArrayList<>();
        ListingStock stock1 = new ListingStock();
        stock1.setTicker("AAPL");
        stock1.setPrice(150.00);

        listingStockService.saveAllListingStocks(listingStocks);

        verify(stockRepository).saveAll(listingStocks);
    }

    @Test
    public void testGetStockRepository() {
        StockRepository result = listingStockService.getStockRepository();
        assertEquals(stockRepository, result);
    }
    @Test
    public void testGetExchangeRepository() {
        ExchangeRepository result = listingStockService.getExchangeRepository();
        assertEquals(exchangeRepository, result);
    }
    @Test
    public void testGetCountryRepository() {
        CountryRepository result = Mockito.mock(CountryRepository.class);
        listingStockService.setCountryRepository(result);
        assertEquals(listingStockService.getCountryRepository(), result);
    }

    @Test
    public void testGetListingHolidayRepository(){
        HolidayRepository result =Mockito.mock(HolidayRepository.class);
        listingStockService.setHolidayRepository(result);
        assertEquals(listingStockService.getHolidayRepository(),result);
    }

    @Test
    public void testGetStockMapper(){
        StockMapper result = Mockito.mock(StockMapper.class);
        listingStockService.setStockMapper(result);
        assertEquals( listingStockService.getStockMapper(),result);
    }

    @Test
    public void testGetListingHistoryRepository(){
        ListingHistoryRepository result =Mockito.mock(ListingHistoryRepository.class);
        listingStockService.setListingHistoryRepository(result);
        assertEquals(listingStockService.getListingHistoryRepository(),result);
    }

    @Test
    public void testFetchNListingsHistory_Exception() {
       when(listingStockService.fetchNStocks(5)).thenThrow(new RuntimeException("Error fetching stocks"));

        List<ListingHistory> listingHistories = listingStockService.fetchNListingsHistory(5);
        //     assertTrue(listingHistories.isEmpty());

        assertEquals(0,listingHistories.size());
    }

}
