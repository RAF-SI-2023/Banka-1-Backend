package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import rs.edu.raf.banka1.mapper.StockMapper;
import rs.edu.raf.banka1.model.ListingHistory;

import rs.edu.raf.banka1.model.ListingStock;

import rs.edu.raf.banka1.model.exceptions.APIException;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;
import rs.edu.raf.banka1.repositories.StockRepository;
import rs.edu.raf.banka1.threads.FetchingThread;
import rs.edu.raf.banka1.utils.Constants;
import rs.edu.raf.banka1.utils.Requests;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.Optional;

@Service
public class ListingStockServiceImpl implements ListingStockService {
    private final ObjectMapper objectMapper;

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private ListingHistoryRepository listingHistoryRepository;

    private Requests requests;
    @Value("${listingAPItoken}")
    private String listingAPItoken;

    @Value("${alphaVantageAPIToken}")
    private String alphaVantageAPIToken;

    @Value("${basicStockInfoApiUrl}")
    private String basicStockInfoApiUrl;

    @Value("${listingNameApiUrl}")
    private String listingNameApiUrl;

    @Value("${updateListingApiUrl}")
    private String updateListingApiUrl;

    @Value("${HistoryListingApiUrl}")
    private String historyListingApiUrl;


    public ListingStockServiceImpl() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void generateJSONSymbols() {
        try {
            StringBuilder responses = new StringBuilder();
            for (String sector : Constants.sectors) {
                String sectorsEncoded = String.join("%20", sector.split(" "));

                String urlStr = listingNameApiUrl + sectorsEncoded + "&token=" + listingAPItoken;

                String response = requests.sendRequest(urlStr);
                responses.append(response);

            }
            ArrayNode jsonArray = reformatNamesToJSON(responses.toString());
            // Save the new JSON array to a file
            File file = new File(Constants.listingsFilePath);
            try (FileWriter fileWriter = new FileWriter(file)) {
                // Convert jsonArray to JSON string
                String jsonString = jsonArray.toString();

                // Append JSON string to the file
                fileWriter.write(jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (APIException apiException){
            System.out.println(apiException.getMessage());
        } catch (Exception e){
            //e.printStackTrace();
            System.err.println("[generateJSONSymbols] Exception occured:" + e.getMessage());
        }

    }
    @Override
    public List<ListingStock> fetchNListingStocks(int n) {
        try {
            File file = new File(Constants.listingsFilePath);

            // Read JSON data from the file
            JsonNode rootNode = objectMapper.readTree(file);

            List<ListingStock> listingStocks = new ArrayList<>();

            int i = 1;
            // Iterate over n element in the listings JSON
            for (JsonNode node : rootNode) {
                String symbol = node.path("symbol").asText();
                ListingStock listingStock = createListingStock(symbol);
                if (listingStock != null) listingStocks.add(listingStock);
                if (i++ > n) break;
            }
            return listingStocks;

        } catch (Exception e){
            System.err.println("[populateNListingStocks] Exception occured:"+e.getMessage());
            return new ArrayList<>();
        }

    }

    private ListingStock createListingStock(String symbol) {
        try {
            String listingBaseUrl = updateListingApiUrl + symbol + "&apikey=" + alphaVantageAPIToken;
            String listingStockUrl = basicStockInfoApiUrl+symbol+"&apikey=" + alphaVantageAPIToken;

            String baseResponse = requests.sendRequest(listingBaseUrl);
            String stockResponse = requests.sendRequest(listingStockUrl);

            // Fetch JSON data from the API
            JsonNode rootNode = objectMapper.readTree(baseResponse);
            rootNode = rootNode.get("Global Quote");

            double high = rootNode.get("03. high").asDouble();
            double low = rootNode.get("04. low").asDouble();
            double price = rootNode.get("05. price").asDouble();
            int volume = rootNode.get("06. volume").asInt();
            double change = rootNode.get("09. change").asDouble();

            JsonNode jsonArray = objectMapper.readTree(stockResponse);
            String name = jsonArray.get("Name").asText();
            Double dividendYield=jsonArray.get("DividendYield").asDouble();
            Integer outstandingShares=jsonArray.get("SharesOutstanding").asInt();
            String exchange=jsonArray.get("Exchange").asText();

            return stockMapper.createListingStock(symbol,name,exchange,price,high,low,change,volume,outstandingShares,dividendYield);

        } catch (APIException apiException){
            System.out.println(apiException.getMessage());
        } catch (Exception e){
            System.out.println(symbol + " not found on alphavantage");
        }
        return null;
    }

    @Scheduled(fixedDelay = 900000)
    private void runFetchBackground(){
        Thread thread = new Thread(new FetchingThread(this.stockRepository,
                this.getAllStocks(), this.updateListingApiUrl, this.alphaVantageAPIToken));
        thread.run();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<ListingStock> getAllStocks(){
        return stockRepository.findAll();
    }

    public List<ListingStock> fetchNStocks(int n) {
        List<ListingStock> allStocks = getAllStocks();
        return allStocks.stream()
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public List<ListingHistory> fetchNListingsHistory(int n) {
        try{
            List<ListingStock> listingStocks = fetchNStocks(n);
            List<ListingHistory> listingHistories = new ArrayList<>();
            for (ListingStock stock : listingStocks){
                List<ListingHistory> singleStockHistory = fetchSingleListingHistory(stock.getTicker());
                listingHistories.addAll(singleStockHistory);
            }

            return listingHistories;
        }catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    @Override
    public int addAllListingStocks(List<ListingStock> listingStocks) {
        return listingStocks.stream().mapToInt(this::addListingStock).sum();
    }
    @Override
    public void saveAllListingStocks(List<ListingStock> listingStocks) {
        stockRepository.saveAll(listingStocks);
    }
    @Override
    public int addListingStock(ListingStock listingStock) {
        Optional<ListingStock> listingStockOptional = stockRepository.findByTicker(listingStock.getTicker());
        if (listingStockOptional.isPresent()) {
            var oldStock = listingStockOptional.get();
            stockMapper.updateListingStock(oldStock, listingStock);
            stockRepository.save(oldStock);
            return 0;
        }else{
            stockRepository.save(listingStock);
            return 1;
        }
    }


    @Override
    public List<ListingHistory> fetchSingleListingHistory(String ticker){
        try {
            String apiUrl = historyListingApiUrl + ticker + "&outputsize=compact&apikey=" + alphaVantageAPIToken;
            String response = requests.sendRequest(apiUrl);
            JsonNode rootNode = objectMapper.readTree(response);

            List<ListingHistory> listingHistories = new ArrayList<>();
            // Get the "Time Series (Daily)" node
            JsonNode timeSeriesNode = rootNode.get("Time Series (Daily)");
            if (timeSeriesNode != null) {
                Iterator<Map.Entry<String, JsonNode>> fields = timeSeriesNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();

                    String dateStr = entry.getKey();
                    LocalDate date = LocalDate.parse(dateStr); // Parse the date string to LocalDate
                    int unixTimestamp = (int) date.atStartOfDay(ZoneOffset.UTC).toEpochSecond(); // get beggining of the day

                    JsonNode dataNode = entry.getValue();

                    ListingHistory listingHistory = createListingHistoryModelFromJson(dataNode, ticker, unixTimestamp);

                    listingHistories.add(listingHistory);
                }
            }
            return listingHistories;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public int addListingToHistory(ListingHistory listingHistory) {
        Optional<ListingHistory> listingHistoryModelOptional = listingHistoryRepository.findByTickerAndDate(listingHistory.getTicker(), listingHistory.getDate());
        if (listingHistoryModelOptional.isPresent()) {
            ListingHistory lhm = stockMapper.updateHistoryListingWithNewData(listingHistoryModelOptional.get(), listingHistory);
            listingHistoryRepository.save(lhm);
            return 0;
        }else{
            listingHistoryRepository.save(listingHistory);
            return 1;
        }
    }

    @Override
    public int addAllListingsToHistory(List<ListingHistory> listingHistoriesModels) {
        return listingHistoriesModels.stream().mapToInt(this::addListingToHistory).sum();
    }

    private ListingHistory createListingHistoryModelFromJson(JsonNode dataNode, String ticker, int unixTimestamp){
        // Get specific fields from each data node
        double open = dataNode.get("1. open").asDouble();
        double high = dataNode.get("2. high").asDouble();
        double low = dataNode.get("3. low").asDouble();
        double close = dataNode.get("4. close").asDouble();
        int volume = dataNode.get("5. volume").asInt();

        // make a new ListingHistoryModel

        return stockMapper.createListingHistoryModel(ticker, unixTimestamp, close, high, low, close - open, volume);
    }

    public ArrayNode reformatNamesToJSON(String response) throws Exception {
        // Parse the JSON array string
        JsonNode jsonArray = objectMapper.readTree(response.toString());

        // Create a new JSON array to store selected fields
        ArrayNode newArray = objectMapper.createArrayNode();

        // Iterate through the JSON array
        for (JsonNode jsonNode : jsonArray) {
            // Extract selected fields
            String symbol = jsonNode.get("symbol").asText();
            String companyName = jsonNode.get("companyName").asText();
            // if primaryExchange is null, we should skip it
            if (jsonNode.get("primaryExchange") == null ) {
                continue;
            }

            // we need to skip those symbols because alphavantage doesn't support them
            if(symbol.contains("-"))
                continue;

            if(Constants.ListingsToIgnore.contains(symbol))
                continue;

            String primaryExchange = jsonNode.get("primaryExchange").asText();

            // Create a new JSON object with selected fields
            ObjectNode newObj = objectMapper.createObjectNode();
            newObj.put("symbol", symbol);
            newObj.put("companyName", companyName);
            newObj.put("primaryExchange", primaryExchange);

            // Add the new object to the new JSON array
            newArray.add(newObj);
        }
        return newArray;
    }

    @Override
    public Optional<ListingStock> findByTicker(String ticker) {
        return stockRepository.findByTicker(ticker);
    }

    @Override
    public Optional<ListingStock> findById(Long id) {
        return stockRepository.findById(id);
    }

    public List<ListingHistory> getListingHistoriesByTimestamp(String ticker, Integer from, Integer to) {
        List<ListingHistory> listingHistories = new ArrayList<>();
//        return all timestamps
        if(from == null && to == null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTicker(ticker);
        }
//        return all timestamps before given timestamp
        else if(from == null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateBefore(ticker, to);
        }
//        return all timestamps after given timestamp
        else if(to == null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateAfter(ticker, from);
        }
//        return all timestamps between two timestamps
        else{
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateBetween(ticker, from, to);
        }

        return listingHistories;
    }

    @Override
    public List<ListingHistory> getListingHistoriesByTimestamp(Long id, Integer from, Integer to) {
        List<ListingHistory> listingHistories = new ArrayList<>();
//        find stock in database
        ListingStock stock = stockRepository.findById(id).orElse(null);
        if(stock == null){
            return listingHistories;
        }
        String ticker = stock.getTicker();
//        return all timestamps
        if(from == null && to == null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTicker(ticker);
        }
//        return all timestamps before given timestamp
        else if(from == null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateBefore(ticker, to);
        }
//        return all timestamps after given timestamp
        else if(to == null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateAfter(ticker, from);
        }
//        return all timestamps between two timestamps
        else{
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateBetween(ticker, from, to);
        }

        return listingHistories;
    }
}
