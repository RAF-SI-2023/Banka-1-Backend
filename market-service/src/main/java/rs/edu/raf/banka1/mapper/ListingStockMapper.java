package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingStock;

@Component
public class ListingStockMapper {
    public ListingStock listingStockCreate(String symbol, double price, double high, double low, double change, int volume) {
        ListingStock stock = new ListingStock();
        stock.setTicker(symbol);
        stock.setListingType("Stock");
        stock.setPrice(price);
        stock.setHigh(high);
        stock.setLow(low);
        stock.setPriceChange(change);
        stock.setVolume(volume);
        stock.setLastRefresh((int) (System.currentTimeMillis() / 1000));
        return stock;
    }
    public void updatelistingStock(ListingStock stock, String name, Integer outstandingShare, Double dividendYield, String exchange) {
        stock.setOutstandingShares(outstandingShare);
        stock.setDividendYield(dividendYield);
        stock.setName(name);
        stock.setExchange(exchange);
        stock.setLastRefresh((int) (System.currentTimeMillis() / 1000));

    }

}
