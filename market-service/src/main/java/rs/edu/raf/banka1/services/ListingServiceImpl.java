package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.model.ListingModel;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;
import rs.edu.raf.banka1.repositories.ListingRepository;
import rs.edu.raf.banka1.utils.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ListingServiceImpl implements ListingService{
    private ObjectMapper objectMapper = new ObjectMapper();

    public ListingServiceImpl() {
        // we don't need all fields from response, so we can ignore them
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private ListingHistoryRepository listingHistoryRepository;


//    TODO: see what to do with this, as API isn't free also
    @Override
    public void initializeListings() {
        try {
            List<String> sectors = List.of("Electronic", "Technology");
            String sectorsEncoded = String.join("%20", sectors);

            String urlStr = "https://api.iex.cloud/v1/data/core/stock_collection/sector?collectionName=" + sectorsEncoded + "&token=" + Constants.listingAPItoken;

            URL url = new URL(urlStr);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set request headers if needed
            connection.setRequestProperty("Content-Type", "application/json");

            // Get the response code
            int responseCode = connection.getResponseCode();
//            System.out.println("Response Code: " + responseCode);

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

            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

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
                if (jsonNode.get("primaryExchange") == null) {
                    continue;
                }

                String primaryExchange = jsonNode.get("primaryExchange").asText();

                // Create a new JSON object with selected fields
                ObjectNode newObj = objectMapper.createObjectNode();
                newObj.put("symbol", symbol);
                newObj.put("companyName", companyName);
                newObj.put("primaryExchange", primaryExchange);

                // Add the new object to the new JSON array
                newArray.add(newObj);

            }
                // Save the new JSON array to a file
                File f = new File("./market-service/src/main/resources/listings.json");
                objectMapper.writeValue(f, newArray);
                System.out.println(f.getAbsolutePath());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public List<ListingModel> fetchListings() {
        try {
            List<String> sectors = List.of("Electronic", "Technology");
            String sectorsEncoded = String.join("%20", sectors);

            String urlStr = "https://api.iex.cloud/v1/data/core/stock_collection/sector?collectionName=" + sectorsEncoded + "&token=" + Constants.listingAPItoken;

            URL url = new URL(urlStr);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set request headers if needed
            connection.setRequestProperty("Content-Type", "application/json");

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

        // Convert JSON response to list of ListingModel objects
        List<ListingModel> listings = objectMapper.readValue(response.toString(), new TypeReference<List<ListingModel>>() {});

        // set lastRefresh to current time
        listings.forEach(listing -> listing.setLastRefresh(java.time.LocalDateTime.now()));

        return listings;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void updateAllListings(List<ListingModel> listings) {
        listingRepository.saveAll(listings);
    }

//    if we want to add listing to history for certain day, if we already have saved it, we should just update it
    @Override
    public int addListingToHistory(ListingHistoryModel listingHistoryModel) {
        Optional<ListingHistoryModel> listingHistoryModelOptional = listingHistoryRepository.findByTickerAndDate(listingHistoryModel.getTicker(), listingHistoryModel.getDate());
        if (listingHistoryModelOptional.isPresent()) {
            ListingHistoryModel listingHistoryModel1 = listingHistoryModelOptional.get();
            listingHistoryModel1.setPrice(listingHistoryModel.getPrice());
            listingHistoryModel1.setAsk(listingHistoryModel.getAsk());
            listingHistoryModel1.setBid(listingHistoryModel.getBid());
            listingHistoryModel1.setChanged(listingHistoryModel.getChanged());
            listingHistoryModel1.setVolume(listingHistoryModel.getVolume());

            listingHistoryRepository.save(listingHistoryModel1);
            return 0;
        }else{
            listingHistoryRepository.save(listingHistoryModel);
            return 1;
        }
    }

//    call it at the end of the day (to save API calls, but other than that, you can call it whenever you want)
    @Override
    public int addAllListingsToHistory(List<ListingHistoryModel> listingHistoryModels) {

        return listingHistoryModels.stream().mapToInt(this::addListingToHistory).sum();
    }


}
