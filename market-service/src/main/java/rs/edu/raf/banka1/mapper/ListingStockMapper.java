package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;

@Component
public class ListingStockMapper {
    public ListingStock createListingStock(String symbol, String name, String exchange, Double price, Double high, Double low, Double priceChange, Integer volume, Integer outstandingShare, Double dividendYield) {
        ListingStock stock = new ListingStock();
        stock.setTicker(symbol);
        stock.setName(name);
        stock.setExchange(exchange);
        stock.setListingType("Stock");
        stock.setPrice(price);
        stock.setHigh(high);
        stock.setLow(low);
        stock.setPriceChange(priceChange);
        stock.setVolume(volume);
        stock.setOutstandingShares(outstandingShare);
        stock.setDividendYield(dividendYield);
        stock.setLastRefresh((int) (System.currentTimeMillis() / 1000));
        return stock;
    }
    public void updateListingStock(ListingStock oldStock, ListingStock newStock) {
        oldStock.setLow(newStock.getLow());
        oldStock.setHigh(newStock.getHigh());
        oldStock.setPrice(newStock.getPrice());
        oldStock.setVolume(newStock.getVolume());
        oldStock.setOutstandingShares(newStock.getOutstandingShares());
        oldStock.setDividendYield(newStock.getDividendYield());
        oldStock.setName(newStock.getName());
        oldStock.setLastRefresh((int) (System.currentTimeMillis() / 1000));
    }
    public ListingHistory createListingHistoryModel(String ticker, long date, double price, double ask, double bid, double changed, int volume) {
        ListingHistory listingHistory = new ListingHistory();
        listingHistory.setTicker(ticker);
        listingHistory.setDate(date);
        listingHistory.setPrice(price);
        listingHistory.setAsk(ask);
        listingHistory.setBid(bid);
        listingHistory.setChanged(changed);
        listingHistory.setVolume(volume);
        return listingHistory;
    }

    public ListingHistory updateHistoryListingWithNewData(ListingHistory oldModel, ListingHistory newModel) {
        oldModel.setPrice(newModel.getPrice());
        oldModel.setAsk(newModel.getAsk());
        oldModel.setBid(newModel.getBid());
        oldModel.setChanged(newModel.getChanged());
        oldModel.setVolume(newModel.getVolume());

        return oldModel;
    }

}
