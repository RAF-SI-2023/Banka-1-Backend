package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.model.ListingModel;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;
import rs.edu.raf.banka1.repositories.ListingRepository;
import rs.edu.raf.banka1.utils.Constants;

import java.io.BufferedReader;
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

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

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
            System.out.println("Data size: " + listings.size());

            // for every listing, we should set lastRefreshed to current time
            listings.forEach(listingModel -> listingModel.setLastRefresh(java.time.LocalDateTime.now()));
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

    @Override
    public int addAllListingsToHistory(List<ListingHistoryModel> listingHistoryModels) {

        return listingHistoryModels.stream().mapToInt(this::addListingToHistory).sum();
    }


}
