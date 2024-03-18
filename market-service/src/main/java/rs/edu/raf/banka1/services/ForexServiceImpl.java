package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.ForexMapper;
import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.repositories.ForexRepository;
import rs.edu.raf.banka1.utils.Requests;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ForexServiceImpl implements ForexService {
    private ObjectMapper objectMapper;

    @Autowired
    private ForexMapper forexMapper;

    @Autowired
    private ForexRepository forexRepository;

    @Value("${alphaVantageAPIToken}")
    private String alphaVantageAPIToken;

    @Value("${forexAPItoken}")
    private String forexAPItoken;

    @Value("${forexExchangePlaceApiUrl}")
    private String forexExchangePlaceApiUrl;

    @Value("${forexSymbolsApiUrl}")
    private String forexSymbolsApiUrl;

    @Value("${forexExchangeRateApiUrl}")
    private String forexExchangeRateApiUrl;


    public ForexServiceImpl() {
        this.objectMapper = new ObjectMapper();
    }

    // Run only once to get all forex-pairs names (from diferent forex places)
    @Override
    public List<ListingForex> initializeForex() {
        try {
            String urlStr = forexExchangePlaceApiUrl + forexAPItoken;
            String response = Requests.sendRequest(urlStr);

            // Parse the response
            JsonNode jsonArray = objectMapper.readTree(response);

            List<ListingForex> listingForexList = new ArrayList<>();

            // Iterate through array elements
            for(JsonNode element: jsonArray)
                listingForexList.addAll(fetchAllForexPairs(element.asText()));

            return listingForexList;
        }catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
     }

    @Override
    public List<ListingForex> fetchAllForexPairs(String forex_place) {
        try {
            String urlStr = forexSymbolsApiUrl + forex_place + "&token=" + forexAPItoken;
            String response = Requests.sendRequest(urlStr);

            // Parse the response
            JsonNode jsonArray = objectMapper.readTree(response);

            List<ListingForex> listingForexList = new ArrayList<>();

            // Iterate through array elements
            for(JsonNode element: jsonArray){
                String displaySymbol = element.get("displaySymbol").asText();
                if(!displaySymbol.contains("/"))
                    continue;
                String left = displaySymbol.split("/")[0];
                String right = displaySymbol.split("/")[1];

                String name = element.get("description").asText();

                ListingForex listingForex = forexMapper.createForex(displaySymbol, name, forex_place);
                listingForexList.add(listingForex);
            }

            return listingForexList;
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println();
            return new ArrayList<>();
        }
    }

    @Override
    public List<ListingForex> updateAllPrices(List<ListingForex> listingForexList) {
//        return only values that are not null
        return listingForexList.stream().map(this::getUpdatedForex).filter(Objects::nonNull).toList();
    }

    @Override
    public ListingForex getUpdatedForex(ListingForex listingForex) {
        String response = "";
        try {
            String urlStr = forexExchangeRateApiUrl + "&from_currency=" + listingForex.getBaseCurrency()
                                                    + "&to_currency=" + listingForex.getQuoteCurrency()
                                                    + "&apikey=" + alphaVantageAPIToken;

            response = Requests.sendRequest(urlStr);

            // Parse the response
            double price = objectMapper.readTree(response).get("Realtime Currency Exchange Rate").get("5. Exchange Rate").asDouble();
            double high = objectMapper.readTree(response).get("Realtime Currency Exchange Rate").get("9. Ask Price").asDouble();
            double low = objectMapper.readTree(response).get("Realtime Currency Exchange Rate").get("8. Bid Price").asDouble();

            ListingForex updatedForex = forexMapper.updatePrices(listingForex, price, high, low);
            return updatedForex;
        }catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Response: " + response);
//            this currency pair is not supported by the API
            System.out.println("BaseCurrency: " + listingForex.getBaseCurrency() + ", QuoteCurrency: " + listingForex.getQuoteCurrency() + " are not awailable on the API");
            return null;
        }
    }

    @Override
    public void saveAllForexes(List<ListingForex> listingForexList) {
        forexRepository.saveAll(listingForexList);
    }


}
