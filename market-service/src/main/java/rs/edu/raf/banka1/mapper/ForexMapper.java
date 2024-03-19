package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingHistory;

import java.time.Instant;

@Component
public class ForexMapper {
    public ListingForex createForex(String ticker, String name, String exchange){
        String[] symbolArr = ticker.split("/");
        String baseCurrency = symbolArr[0];
        String quoteCurrency = symbolArr[1];
        Double exchangeRate = (double) 0;

        ListingForex forex = new ListingForex();
        forex.setTicker(ticker);
        forex.setName(name);
        forex.setExchange(exchange);
        forex.setBaseCurrency(baseCurrency);
        forex.setQuoteCurrency(quoteCurrency);
        forex.setListingType("forex");

//        Default is 0
        forex.setPrice(0.0);
        forex.setHigh(0.0);
        forex.setLow(0.0);
        forex.setVolume(0);
        forex.setPriceChange(0.0);

        int currentTime = (int) Instant.now().getEpochSecond();
        forex.setLastRefresh(currentTime);

        return forex;
    }

    public ListingForex updatePrices(ListingForex listingForex, Double price, Double high, Double low){
        Double previousPrice = listingForex.getPrice();
        listingForex.setPrice(price);
        listingForex.setHigh(high);
        listingForex.setLow(low);
        listingForex.setLastRefresh((int) Instant.now().getEpochSecond());
        listingForex.setPriceChange(price - previousPrice);
        return listingForex;
    }

}
