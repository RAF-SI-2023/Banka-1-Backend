package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.ListingStockMapper;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.repositories.StockRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class ListingStockServiceImpl implements ListingStockService {
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

    public ListingStockServiceImpl() {
        objectMapper= new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Override
    public void initializeStock() {

        stockRepository.findAll().forEach(stock -> getStockData(stock.getTicker()));

    }

    public void getStockData(String symbol) {
        try {
            String url=basicStockInfoApiUrl+symbol+"&apikey=" + alphaVantageAPIToken;

            String response = sendRequest(url);

            // Parse the response
            JsonNode jsonArray = objectMapper.readTree(response);
            String name = jsonArray.get("Name").asText();
            Double dividendYield=jsonArray.get("DividendYield").asDouble();
            Integer outstandingShares=jsonArray.get("SharesOutstanding").asInt();
            String exchange=jsonArray.get("Exchange").asText();

            //ListingStock stock = stockRepository.findById(symbol).get();
            ListingStock stock = stockRepository.findByTicker(symbol);
                // Update the stock fields// Only set if the "Exchange" field is present in the JSON response
            stockMapper.updatelistingStock(stock,name,outstandingShares,dividendYield,exchange);
            stockRepository.save(stock);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String sendRequest(String urlStr) throws Exception {
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
