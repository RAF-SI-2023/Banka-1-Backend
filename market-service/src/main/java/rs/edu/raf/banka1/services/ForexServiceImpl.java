package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.ForexMapper;
import rs.edu.raf.banka1.model.Forex;
import rs.edu.raf.banka1.repositories.ForexRepository;
import rs.edu.raf.banka1.utils.Constants;
import rs.edu.raf.banka1.utils.Requests;

import java.util.ArrayList;
import java.util.List;

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
    public List<Forex> initializeForex() {
        try {
            String urlStr = forexExchangePlaceApiUrl + forexAPItoken;
            String response = Requests.sendRequest(urlStr);

            // Parse the response
            JsonNode jsonArray = objectMapper.readTree(response);

            List<Forex> forexList = new ArrayList<>();

            // Iterate through array elements
            for(JsonNode element: jsonArray)
                forexList.addAll(fetchAllForexPairs(element.asText()));

            return forexList;
        }catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
     }

    @Override
    public List<Forex> fetchAllForexPairs(String forex_place) {
        try {
            String urlStr = forexSymbolsApiUrl + forex_place + "&token=" + forexAPItoken;
            String response = Requests.sendRequest(urlStr);

            // Parse the response
            JsonNode jsonArray = objectMapper.readTree(response);

            List<Forex> forexList = new ArrayList<>();

            // Iterate through array elements
            for(JsonNode element: jsonArray){
                String displaySymbol = element.get("displaySymbol").asText();
                if(!displaySymbol.contains("/"))
                    continue;
                String left = displaySymbol.split("/")[0];
                String right = displaySymbol.split("/")[1];

                Forex forex = forexMapper.createForex(element.get("displaySymbol").asText());
                forexList.add(forex);
            }

            return forexList;
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Forex> fetchAllExchangeRates(List<Forex> forexList) {
        for(Forex forex: forexList){
            double exchangeRate = fetchExchangeRate(forex.getBaseCurrency(), forex.getQuoteCurrency());
            forex.setExchangeRate(exchangeRate);
        }
            return forexList;
    }

    @Override
    public double fetchExchangeRate(String baseCurrency, String quoteCurrency) {
        String response = "";
        try {
            String urlStr = forexExchangeRateApiUrl + "&from_currency=" + baseCurrency
                                                    + "&to_currency=" + quoteCurrency
                                                    + "&apikey=" + alphaVantageAPIToken;

            response = Requests.sendRequest(urlStr);

            // Parse the response
            double exchangeRate = objectMapper.readTree(response).get("Realtime Currency Exchange Rate").get("5. Exchange Rate").asDouble();

            return exchangeRate;
        }catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Response: " + response);
//            this currency pair is not supported by the API
            System.out.println("BaseCurrency: " + baseCurrency + ", QuoteCurrency: " + quoteCurrency);
            return 0;
        }
    }

    @Override
    public void saveAllForexes(List<Forex> forexList) {
        forexRepository.saveAll(forexList);
    }


}
