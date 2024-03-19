package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import rs.edu.raf.banka1.mapper.ListingStockMapper;
import rs.edu.raf.banka1.model.ListingHistoryModel;

import rs.edu.raf.banka1.model.ListingStock;

import rs.edu.raf.banka1.model.exceptions.APIException;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;
import rs.edu.raf.banka1.repositories.StockRepository;
import rs.edu.raf.banka1.utils.Constants;


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
    private ObjectMapper objectMapper;

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ListingStockMapper stockMapper;
    @Autowired
    private ListingHistoryRepository listingHistoryRepository;

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

                String response = sendRequest(urlStr);
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
    public void populateListingStocks() {
        try {
            File file = new File(Constants.listingsFilePath);

            // Read JSON data from the file
            JsonNode rootNode = objectMapper.readTree(file);

            // Iterate over each element in the JSON array
            for (JsonNode node : rootNode) {
                ListingStock listingStock = new ListingStock();
                listingStock.setTicker(node.path("symbol").asText());
                listingStock.setName(node.path("companyName").asText());
                listingStock.setExchange(node.path("primaryExchange").asText());
                updateValuesForListingStock(listingStock);

            }

        }catch (Exception e){
            //e.printStackTrace();
            System.err.println("[populateListinStocks] Exception occured:"+e.getMessage());
        }

    }
    @Override
    public List<ListingStock> getAllStocks(){
       return stockRepository.findAll();
    }

    public void updateValuesForListingStock(ListingStock listingStock) {
        try{
            String symbol = listingStock.getTicker();
            String listingBaseUrl = updateListingApiUrl + symbol + "&apikey=" + alphaVantageAPIToken;
            String listingStockUrl = basicStockInfoApiUrl+symbol+"&apikey=" + alphaVantageAPIToken;

            String baseResponse = sendRequest(listingBaseUrl);
            String stockResponse = sendRequest(listingStockUrl);

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


            listingStock.setLastRefresh((int) (System.currentTimeMillis() / 1000));
            stockMapper.updatelistingStock(listingStock,symbol,name,price,high,low,change,volume,outstandingShares,dividendYield,exchange);
            stockRepository.save(listingStock);

        } catch (APIException apiException){
            System.out.println(apiException.getMessage());
        } catch (Exception e){
            System.out.println(listingStock.getTicker() + " not found on alphavantage");
        }
    }

    public List<ListingStock> fetchTenStocks() {
        List<ListingStock> allStocks = getAllStocks();

        // Use Java streams to limit the number of stocks to 10 or less
        List<ListingStock> tenStocks = allStocks.stream()
                .limit(10)
                .collect(Collectors.toList());

        return tenStocks;
    }

    /*
    @Override
    public List<ListingHistoryModel> fetchAllListingsHistory() {
        try{
            List<ListingStock> listingStocks = fetchTenStocks();
            List<ListingHistoryModel> listingHistoryModels = new ArrayList<>();
            for (ListingStock stock : listingStocks)
                listingHistoryModels.addAll(fetchSingleListingHistory(stock.getTicker()));

            return listingHistoryModels;
        }catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    *
     */

    //------!!!-------Ovo vraca za 10 stocks StockHistory
    @Override
    public List<ListingHistoryModel> fetchAllListingsHistory() {
        try{
            List<ListingStock> listingStocks = fetchTenStocks();
            List<ListingHistoryModel> listingHistoryModels = new ArrayList<>();
            for (ListingStock stock : listingStocks){
                List<ListingHistoryModel> singleStockHistory = fetchSingleListingHistory(stock.getTicker());
                listingHistoryModels.addAll(singleStockHistory);
            }

            return listingHistoryModels;
        }catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    @Override
    public List<ListingHistoryModel> fetchSingleListingHistory(String ticker){
        try {
            String apiUrl = historyListingApiUrl + ticker + "&outputsize=compact&apikey=" + alphaVantageAPIToken;
            String response = sendRequest(apiUrl);
            JsonNode rootNode = objectMapper.readTree(response);

            List<ListingHistoryModel> listingHistoryModels = new ArrayList<>();
            // Get the "Time Series (Daily)" node
            JsonNode timeSeriesNode = rootNode.get("Time Series (Daily)");
            if (timeSeriesNode != null) {
                Iterator<Map.Entry<String, JsonNode>> fields = timeSeriesNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();

                    String dateStr = entry.getKey();
                    LocalDate date = LocalDate.parse(dateStr); // Parse the date string to LocalDate
                    int unixTimestamp = (int) date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000;

                    JsonNode dataNode = entry.getValue();

                    ListingHistoryModel listingHistoryModel = createListingHistoryModelFromJson(dataNode, ticker, unixTimestamp);

                    listingHistoryModels.add(listingHistoryModel);
                }
            }
            return listingHistoryModels;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public int addListingToHistory(ListingHistoryModel listingHistory) {
        Optional<ListingHistoryModel> listingHistoryModelOptional = listingHistoryRepository.findByTickerAndDate(listingHistory.getTicker(), listingHistory.getDate());
        if (listingHistoryModelOptional.isPresent()) {
            ListingHistoryModel lhm = stockMapper.updateHistoryListingWithNewData(listingHistoryModelOptional.get(), listingHistory);
            listingHistoryRepository.save(lhm);
            return 0;
        }else{
            listingHistoryRepository.save(listingHistory);
            return 1;
        }
    }

    @Override
    public int addAllListingsToHistory(List<ListingHistoryModel> listingHistoriesModels) {
        return listingHistoriesModels.stream().mapToInt(this::addListingToHistory).sum();
    }

    private ListingHistoryModel createListingHistoryModelFromJson(JsonNode dataNode, String ticker, int unixTimestamp){
        // Get specific fields from each data node
        double open = dataNode.get("1. open").asDouble();
        double high = dataNode.get("2. high").asDouble();
        double low = dataNode.get("3. low").asDouble();
        double close = dataNode.get("4. close").asDouble();
        int volume = dataNode.get("5. volume").asInt();

        // make a new ListingHistoryModel
        ListingHistoryModel listingHistoryModel = stockMapper.createListingHistoryModel(ticker, unixTimestamp, close, high, low, close - open, volume);

        return listingHistoryModel;
    }

    private String sendRequest(String urlStr) throws Exception,APIException {
        URL url = new URL(urlStr);

        // Open a connection to the URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to GET
        connection.setRequestMethod("GET");

        // Set request headers if needed
        connection.setRequestProperty("Content-Type", "application/json");

        // Get the response code
        int responseCode = connection.getResponseCode();

        switch (responseCode){
            case 402:
                throw new APIException("[Send request] Error occured because API requires payment");
            case 500:
                throw new APIException("[Send request] Error occured because API returned internal server error");
        }

        // Read the response body
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Close the connection
        connection.disconnect();

        return response.toString();
    }

    public ArrayNode reformatNamesToJSON(String response) throws Exception {
        // Parse the JSON array string
        JsonNode jsonArray = objectMapper.readTree(response.toString());

        // Create a new JSON array to store selected fields
        ArrayNode newArray = objectMapper.createArrayNode();

        int i = 0;

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

            if(i++ >= Constants.maxStockListings)
                break;
        }
        return newArray;
    }



}
