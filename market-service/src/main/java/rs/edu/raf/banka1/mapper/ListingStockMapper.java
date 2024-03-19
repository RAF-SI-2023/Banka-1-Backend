package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingStock;

@Component
public class ListingStockMapper {
    public ListingStock listingStockCreate(String symbol,String name, Double price, Double high, Double low, Double priceChange, Integer volume,Integer outstandingShare, Double dividendYield, String exchange) {
        ListingStock stock = new ListingStock();
        stock.setTicker(symbol);
        stock.setName(name);
        stock.setListingType("Stock");
        stock.setPrice(price);
        stock.setHigh(high);
        stock.setLow(low);
        stock.setPriceChange(priceChange);
        stock.setVolume(volume);
        stock.setOutstandingShares(outstandingShare);
        stock.setDividendYield(dividendYield);
        stock.setExchange(exchange);
        stock.setLastRefresh((int) (System.currentTimeMillis() / 1000));
        return stock;
    }
    public void updatelistingStock(ListingStock stock, String name, String symbol, Double price, Double high, Double low, Double priceChange, Integer volume,Integer outstandingShare, Double dividendYield, String exchange) {
        stock.setTicker(symbol);
        stock.setLow(low);
        stock.setListingType("Stock");
        stock.setHigh(high);
        stock.setPrice(price);
        stock.setPriceChange(priceChange);
        stock.setVolume(volume);
        stock.setOutstandingShares(outstandingShare);
        stock.setDividendYield(dividendYield);
        stock.setName(name);
        stock.setExchange(exchange);
        stock.setLastRefresh((int) (System.currentTimeMillis() / 1000));

    }

}
