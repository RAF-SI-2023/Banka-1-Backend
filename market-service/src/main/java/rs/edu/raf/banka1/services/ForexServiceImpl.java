package rs.edu.raf.banka1.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.ForexMapper;
import rs.edu.raf.banka1.mapper.ListingHistoryMapper;
import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.repositories.ForexRepository;
import rs.edu.raf.banka1.repositories.ListingHistoryRepository;
import rs.edu.raf.banka1.utils.Requests;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Service
@Setter
public class ForexServiceImpl implements ForexService {
    private ObjectMapper objectMapper;


    @Autowired
    private ForexMapper forexMapper;

    @Autowired
    private ListingHistoryMapper listingHistoryMapper;
    @Autowired
    private ListingHistoryRepository listingHistoryRepository;

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

    @Value("${forexDailyApiUrl}")
    private String forexDailyApiUrl;


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
            for (JsonNode element: jsonArray) {
                listingForexList.addAll(fetchAllForexPairs(element.asText()));
            }

            return listingForexList;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while initializing forex returning empty list ");
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
            for (JsonNode element: jsonArray) {
                String displaySymbol = element.get("displaySymbol").asText();
                if (!displaySymbol.contains("/")) {
                    continue;
                }
                String left = displaySymbol.split("/")[0];
                String right = displaySymbol.split("/")[1];

                String name = element.get("description").asText();
                String symbol = element.get("symbol").asText();

                ListingForex listingForex = forexMapper.createForex(displaySymbol, name, symbol);
                listingForexList.add(listingForex);
            }

            return listingForexList;
        } catch (Exception e) {
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

            ListingForex updatedForex = updatePrices(listingForex, price, high, low);
            return updatedForex;
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Response: " + response);
//            this currency pair is not supported by the API (or maybe we are sending too much requests)
            System.out.println("BaseCurrency: " + listingForex.getBaseCurrency() + ", QuoteCurrency: "
                    + listingForex.getQuoteCurrency() + " are not available on the alphavantae API");
            return null;
        }
    }

    @Override
    public void saveAllForexes(List<ListingForex> listingForexList) {
        forexRepository.saveAll(listingForexList);
    }

    @Override
    public List<ListingForex> getAllForexes() {
        return forexRepository.findAll();
    }

    @Override
    public List<ListingHistory> getForexHistory(ListingForex listingForex) {
        String response = "";
        try {
            String apiUrl = forexDailyApiUrl + "&from_symbol=" + listingForex.getBaseCurrency()
                    + "&to_symbol=" + listingForex.getQuoteCurrency()
                    + "&apikey=" + alphaVantageAPIToken;

            response = Requests.sendRequest(apiUrl);

            JsonNode rootNode = objectMapper.readTree(response);

            List<ListingHistory> listingHistories = new ArrayList<>();

            // Get the "Time Series FX (Daily)" node
            JsonNode timeSeriesNode = rootNode.get("Time Series FX (Daily)");
            if (timeSeriesNode != null) {
                Iterator<Map.Entry<String, JsonNode>> fields = timeSeriesNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();

                    String dateStr = entry.getKey();
                    LocalDate date = LocalDate.parse(dateStr); // Parse the date string to LocalDate
                    int unixTimestamp = (int) date.atStartOfDay(ZoneOffset.UTC).toEpochSecond(); // get beggining of the day

                    JsonNode dataNode = entry.getValue();

                    ListingHistory listingHistory = parseHistory(listingForex.getTicker(), unixTimestamp, dataNode);

                    listingHistories.add(listingHistory);
                }
            }
            return listingHistories;
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.printf("Error while getting forex history, response is: " + response);
            return new ArrayList<>();
        }
    }

    @Override
    public List<ListingHistory> getAllForexHistories(List<ListingForex> listingForexList) {
        return listingForexList.stream().map(this::getForexHistory).flatMap(List::stream).toList();
    }

    @Override
    public ListingForex getForexByTicker(String ticker) {
        return forexRepository.findByTicker(ticker).orElse(null);
    }

    @Override
    public List<ListingHistory> getListingHistoriesByTimestamp(Long id, Integer from, Integer to) {
        List<ListingHistory> listingHistories = new ArrayList<>();
//        Find forex in database
        ListingForex forex = forexRepository.findById(id).orElse(null);
        if(forex == null){
            return listingHistories;
        }

        String ticker = forex.getTicker();
        listingHistories = listingHistoryRepository.getListingHistoriesByTicker(ticker);
        if(listingHistories.isEmpty()) {
            listingHistories = getForexHistory(forex);
            listingHistoryRepository.saveAll(listingHistories);
        }

//        return all timestamps before given timestamp
        if(from == null && to != null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateBefore(ticker, to);
        }
//        return all timestamps after given timestamp
        else if(from != null && to == null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateAfter(ticker, from);
        }
//        return all timestamps between two timestamps
        else if(from != null && to != null){
            listingHistories = listingHistoryRepository.getListingHistoriesByTickerAndDateBetween(ticker, from, to);
        }

        return listingHistories;
    }

    @Override
    public Optional<ListingForex> findById(Long id) {
        return forexRepository.findById(id);
    }

    public ListingHistory parseHistory(String ticker, int date, JsonNode dataNode) {
        double open = dataNode.get("1. open").asDouble();
        double high = dataNode.get("2. high").asDouble();
        double low = dataNode.get("3. low").asDouble();
        double close = dataNode.get("4. close").asDouble();
        int volume = 0;

        return listingHistoryMapper.createHistory(ticker, date, open, high, low, close, volume);
    }

    public ListingForex updatePrices(ListingForex listingForex, Double price, Double high, Double low) {
        Double previousPrice = listingForex.getPrice();
        listingForex.setPrice(price);
        listingForex.setHigh(high);
        listingForex.setLow(low);
        listingForex.setLastRefresh((int) Instant.now().getEpochSecond());
        listingForex.setPriceChange(price - previousPrice);
        return listingForex;
    }

}
