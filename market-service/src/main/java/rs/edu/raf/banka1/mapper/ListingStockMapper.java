package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingModel;
import rs.edu.raf.banka1.model.entities.ListingStock;

@Component
public class ListingStockMapper {
    public ListingStock listingStockCreate(String symbol,double price, double high, double low, double change, int volume) {
        ListingStock stock = new ListingStock();
        stock.setTicker(symbol);
        stock.setPrice(price);
        stock.setAsk(high);
        stock.setBid(low);
        stock.setChanged(change);
        stock.setVolume(volume);
        stock.setLastRefresh((int) (System.currentTimeMillis() / 1000));
        return stock;
    }
    public void updatelistingStock(ListingStock stock,Integer outstandingShare,Double dividendYield) {
        stock.setOutstandingShares(outstandingShare);
        stock.setDividendYield(dividendYield);

    }

}
