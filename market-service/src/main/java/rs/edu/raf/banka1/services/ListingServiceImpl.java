package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.ListingMapper;
import rs.edu.raf.banka1.model.Listing;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;
import rs.edu.raf.banka1.repositories.ListingRepository;
import rs.edu.raf.banka1.utils.Constants;
import rs.edu.raf.banka1.utils.Requests;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class ListingServiceImpl implements ListingService{
    private ObjectMapper objectMapper;

    @Autowired
    private ListingMapper listingMapper;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ListingHistoryRepository listingHistoryRepository;

    @Value("${listingAPItoken}")
    private String listingAPItoken;

    @Value("${alphaVantageAPIToken}")
    private String alphaVantageAPIToken;

    @Value("${listingNameApiUrl}")
    private String listingNameApiUrl;

    @Value("${updateListingApiUrl}")
    private String updateListingApiUrl;

    public ListingServiceImpl() {
        objectMapper = new ObjectMapper();
        // we don't need all fields from response, so we can ignore them
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


//    NOTE: see what to do with this, as API isn't free (almost nothing from this API changes so it is okay to do it once and store it into json file)
//    NOTE: Maybe name/description of the company changes, so we should update it from time to time
    @Override
    public void initializeListings() {
        try {
            String sectorsEncoded = String.join("%20", Constants.sectors);

            String urlStr = listingNameApiUrl + sectorsEncoded + "&token=" + listingAPItoken;

            String response = Requests.sendRequest(urlStr);

            ArrayNode jsonArray = reformatNamesToJSON(response);

            // Save the new JSON array to a file
            File file = new File(Constants.listingsFilePath);
            objectMapper.writeValue(file, jsonArray);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //    loads listings from json file and updates them with trending data
    @Override
    public List<Listing> fetchListings() {
        List<Listing> listings = fetchListingsName();
        for (Listing listing : listings)
            updateValuesForListing(listing);

        return listings;
    }

    private List<Listing> fetchListingsName(){
        try {
            File file = new File(Constants.listingsFilePath);

            // Read JSON data from the file
            JsonNode rootNode = objectMapper.readTree(file);

            List<Listing> listings = new ArrayList<>();

            // Iterate over each element in the JSON array
            for (JsonNode node : rootNode) {
                Listing listing = new Listing();
                listing.setTicker(node.path("symbol").asText());
                listing.setName(node.path("companyName").asText());
                listing.setExchange(node.path("primaryExchange").asText());

                listing.setLastRefresh((int) (System.currentTimeMillis() / 1000));

                // Add the Listing object to the list
                listings.add(listing);
            }

            return listings;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void updateValuesForListing(Listing listing){
        try{
            // URL of the alphavantage API endpoint
            String symbol = listing.getTicker();
            String apiUrl = updateListingApiUrl + symbol + "&apikey=" + alphaVantageAPIToken;

            // Fetch JSON data from the API
            JsonNode rootNode = objectMapper.readTree(new URL(apiUrl));

            updatelistingModelFields(listing, rootNode);

        }catch (Exception e){
            System.out.println(listing.getTicker() + " not found on alphavantage");
        }
    }


    @Override
//    updates all listings with new data into database
    public void updateAllListingsDatabase(List<Listing> listings) {
        listingRepository.saveAll(listings);
    }

    @Override
    public List<ListingHistory> fetchAllListingsHistory() {
        try{
            List<Listing> listings = fetchListingsName();
            List<ListingHistory> listingHistories = new ArrayList<>();
            for (Listing lmodel : listings)
                listingHistories.addAll(fetchSingleListingHistory(lmodel.getTicker()));

            return listingHistories;
        }catch (Exception e) {
            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    @Override
    public List<ListingHistory> fetchSingleListingHistory(String ticker){
        try {

            String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + ticker + "&apikey=" + alphaVantageAPIToken;
            JsonNode rootNode = objectMapper.readTree(new URL(apiUrl));

            List<ListingHistory> listingHistories = new ArrayList<>();
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


    //    if we want to add listing to history for certain day, if we already have saved it, we should just update it
    @Override
    public int addListingToHistory(ListingHistory listingHistory) {
        Optional<ListingHistory> listingHistoryModelOptional = listingHistoryRepository.findByTickerAndDate(listingHistory.getTicker(), listingHistory.getDate());
        if (listingHistoryModelOptional.isPresent()) {
            ListingHistory lhm = listingMapper.updateHistoryListingWithNewData(listingHistoryModelOptional.get(), listingHistory);
            listingHistoryRepository.save(lhm);
            return 0;
        }else{
            listingHistoryRepository.save(listingHistory);
            return 1;
        }
    }

    //    call it at the end of the day (to save API calls, but other than that, you can call it whenever you want)
    @Override
    public int addAllListingsToHistory(List<ListingHistory> listingHistories) {

        return listingHistories.stream().mapToInt(this::addListingToHistory).sum();
    }

    @Override
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

    public ArrayNode reformatNamesToJSON(String response) throws Exception{
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

            if(i++ >= Constants.maxListings)
                break;
        }
        return newArray;
    }

    public void updatelistingModelFields(Listing listing, JsonNode rootNode){
        // Get the "Global Quote" node
        JsonNode globalQuoteNode = rootNode.get("Global Quote");

        // Parse data from the "Global Quote" node
        double high = globalQuoteNode.get("03. high").asDouble();
        double low = globalQuoteNode.get("04. low").asDouble();
        double price = globalQuoteNode.get("05. price").asDouble();
        int volume = globalQuoteNode.get("06. volume").asInt();
        double change = globalQuoteNode.get("09. change").asDouble();

        listingMapper.listingModelUpdate(listing, price, high, low, change, volume);
    }

    public ListingHistory createListingHistoryModelFromJson(JsonNode dataNode, String ticker, int unixTimestamp){
        // Get specific fields from each data node
        double open = dataNode.get("1. open").asDouble();
        double high = dataNode.get("2. high").asDouble();
        double low = dataNode.get("3. low").asDouble();
        double close = dataNode.get("4. close").asDouble();
        int volume = dataNode.get("5. volume").asInt();

        // make a new ListingHistoryModel
        ListingHistory listingHistory = listingMapper.createListingHistoryModel(ticker, unixTimestamp, close, high, low, close - open, volume);

        return listingHistory;
    }

}