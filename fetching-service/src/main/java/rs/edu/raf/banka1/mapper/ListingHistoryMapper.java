package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.dtos.ListingHistoryDto;

@Component
public class ListingHistoryMapper {
    public ListingHistory createHistory(String ticker, int date, double open, double high, double low, double close, int volume) {
        ListingHistory history = new ListingHistory();
        history.setTicker(ticker);
        history.setDate(date);
        history.setPrice(close);
        history.setHigh(high);
        history.setLow(low);
        history.setChanged(close - open);
        history.setVolume(volume);

        return history;
    }
}
