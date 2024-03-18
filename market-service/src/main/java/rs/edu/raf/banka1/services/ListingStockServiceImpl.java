package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rs.edu.raf.banka1.mapper.ListingStockMapper;
import rs.edu.raf.banka1.model.ListingModel;
import rs.edu.raf.banka1.model.entities.ListingStock;
import rs.edu.raf.banka1.repositories.StockRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ListingStockServiceImpl implements ListingStockService{

    private ObjectMapper objectMapper;

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ListingService listingService;

    @Autowired
    private ListingStockMapper stockMapper;
    @Value("${alphaVantageAPIToken}")
    private String alphaVantageAPIToken;

    @Value("${basicStockInfoApiUrl}")
    private String basicStockInfoApiUrl;

    public ListingStockServiceImpl(){
        objectMapper= new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void initializeStock(){
        return;
    }

    public void getStockData(String symbol) {
        try {
            String url=basicStockInfoApiUrl+symbol+"&apikey=" + alphaVantageAPIToken;

            String response = sendRequest(url);

            // Parse the response
            JsonNode jsonArray = objectMapper.readTree(response);
            Double dividendYield=jsonArray.get("DividendYield").asDouble();
            Integer outstandingShares=jsonArray.get("SharesOutstanding").asInt();

            ListingStock stock = stockRepository.findById(symbol).get();

            stockMapper.updatelistingStock(stock,outstandingShares,dividendYield);
            stockRepository.save(stock);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String sendRequest(String urlStr) throws Exception{
        URL url = new URL(urlStr);

        // Open a connection to the URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to GET
        connection.setRequestMethod("GET");

        // Set request headers if needed
        connection.setRequestProperty("Content-Type", "application/json");

        // Get the response code
        int responseCode = connection.getResponseCode();

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
}
