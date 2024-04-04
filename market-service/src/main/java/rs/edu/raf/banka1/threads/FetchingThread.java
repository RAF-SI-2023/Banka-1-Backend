package rs.edu.raf.banka1.threads;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tinylog.Logger;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.exceptions.APIException;
import rs.edu.raf.banka1.repositories.StockRepository;
import rs.edu.raf.banka1.utils.Requests;

import java.util.List;

public class FetchingThread implements Runnable  {
    private StockRepository stockRepository;
    private final ObjectMapper objectMapper;

    private String updateListingApiUrl;
    private String alphaVantageAPIToken;
    private Requests requests;

    private List<ListingStock> listingStocks;

    public FetchingThread(StockRepository stockRepository,
                          List<ListingStock> listingStocks,
                          Requests requests,
                          String updateListingApiUrl,
                          String alphaVantageAPIToken) {
        this.stockRepository = stockRepository;
        this.listingStocks = listingStocks;
        this.requests = requests;
        this.updateListingApiUrl = updateListingApiUrl;
        this.alphaVantageAPIToken = alphaVantageAPIToken;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void run() {
        valuesForConstantUpdating();
    }

    public void valuesForConstantUpdating() {
      //  System.out.println("[CHRON] USAO SAM OVDE " + this.updateListingApiUrl);
        try {
            for (ListingStock curr : this.listingStocks) {
                String response = requests.sendRequest(this.updateListingApiUrl +
                        curr.getTicker() +
                        "&apikey=" +
                        this.alphaVantageAPIToken);

                JsonNode rootNode = objectMapper.readTree(response);
                rootNode = rootNode.get("Global Quote");
                //prvo setovanje nekih vrednosti ako api vrati null da se ne bi u bazi cuvala null vrednost
                double high = curr.getHigh();
                double low = curr.getLow();
                double price = curr.getPrice();
                double change = curr.getPriceChange();
                int volume = curr.getVolume();

                if (rootNode.get("03. high") != null) {
                    high = rootNode.get("03. high").asDouble();
                }

                if (rootNode.get("04. low") != null) {
                    low = rootNode.get("04. low").asDouble();
                }

                if (rootNode.get("05. price") != null) {
                    price = rootNode.get("05. price").asDouble();
                }

                if (rootNode.get("06. volume") != null) {
                    volume = rootNode.get("06. volume").asInt();
                }

                if (rootNode.get("09. change") != null) {
                    change = rootNode.get("09. change").asDouble();
                }

                this.stockRepository.updateFreshValuesStock(high, low, price, volume, change, curr.getListingId(), (int) (System.currentTimeMillis() / 1000L));
            }
        } catch (APIException apiException) {
            Logger.error("Error occured when calling api " + apiException.getMessage());
        } catch (Exception e) {
            Logger.error("Error occured when calling valuesForConstantUpdating " + e.getMessage() + " Cause: " + e.getCause());
        }
    }


}
