package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.dtos.ListingStockDto;
import rs.edu.raf.banka1.model.entities.Exchange;

@Component
public class StockMapper {

    public ListingStock createListingStock(String symbol, String name, Exchange exchange, Double price, Double high, Double low, Double priceChange, Integer volume, Integer outstandingShare, Double dividendYield) {
        ListingStock stock = new ListingStock();
        stock.setTicker(symbol);
        stock.setName(name);
        stock.setExchange(exchange);
        stock.setExchangeName(exchange.getExchangeName());
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
        oldStock.setExchange(newStock.getExchange());
        oldStock.setExchangeName(newStock.getExchangeName());
        oldStock.setLastRefresh((int) (System.currentTimeMillis() / 1000));
    }
    public ListingHistory createListingHistoryModel(String ticker, Integer date, double price, double ask, double bid, double changed, int volume) {
        ListingHistory listingHistory = new ListingHistory();
        listingHistory.setTicker(ticker);
        listingHistory.setDate(date);
        listingHistory.setPrice(price);
        listingHistory.setHigh(ask);
        listingHistory.setLow(bid);
        listingHistory.setChanged(changed);
        listingHistory.setVolume(volume);
        return listingHistory;
    }

    public ListingHistory updateHistoryListingWithNewData(ListingHistory oldModel, ListingHistory newModel) {
        oldModel.setPrice(newModel.getPrice());
        oldModel.setHigh(newModel.getHigh());
        oldModel.setLow(newModel.getLow());
        oldModel.setChanged(newModel.getChanged());
        oldModel.setVolume(newModel.getVolume());

        return oldModel;
    }

    public ListingStockDto stockDto(ListingStock stock) {
        ListingStockDto dto = new ListingStockDto();
        dto.setListingId(stock.getListingId());
        dto.setTicker(stock.getTicker());
        dto.setName(stock.getName());
        dto.setListingType(stock.getListingType());
        dto.setPrice(stock.getPrice());
        dto.setHigh(stock.getHigh());
        dto.setLow(stock.getLow());
        dto.setPriceChange(stock.getPriceChange());
        dto.setVolume(stock.getVolume());
        dto.setExchange(stock.getExchange());
        dto.setExchangeName(stock.getExchangeName());
        dto.setOutstandingShares(stock.getOutstandingShares());
        dto.setDividendYield(stock.getDividendYield());
        dto.setLastRefresh(stock.getLastRefresh());
        return dto;
    }

}
