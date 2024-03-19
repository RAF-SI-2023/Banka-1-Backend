package rs.edu.raf.banka1.mapper;
/*
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.model.ListingModel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
/*
@Component
public class ListingMapper {
    public ListingHistoryModel listingModelToListongHistoryModel(ListingModel listingModel) {
        ListingHistoryModel listingHistoryModel = new ListingHistoryModel();
        listingHistoryModel.setTicker(listingModel.getTicker());

        // Convert the Unix timestamp to LocalDate
        LocalDate localDate = Instant.ofEpochSecond(listingModel.getLastRefresh()).atZone(ZoneOffset.UTC).toLocalDate();

        // Get the Unix timestamp for the beginning of the day
        long beginningOfDayUnixTimestamp = localDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        listingHistoryModel.setDate(beginningOfDayUnixTimestamp);

        listingHistoryModel.setPrice(listingModel.getPrice());
        listingHistoryModel.setAsk(listingModel.getAsk());
        listingHistoryModel.setBid(listingModel.getBid());
        listingHistoryModel.setChanged(listingModel.getChanged());
        listingHistoryModel.setVolume(listingModel.getVolume());
        return listingHistoryModel;
    }

    public void listingModelUpdate(ListingModel listingModel, double price, double high, double low, double change, int volume) {
        listingModel.setPrice(price);
        listingModel.setAsk(high);
        listingModel.setBid(low);
        listingModel.setChanged(change);
        listingModel.setVolume(volume);
        listingModel.setLastRefresh((int) (System.currentTimeMillis() / 1000));
    }

    public ListingHistoryModel createListingHistoryModel(String ticker, long date, double price, double ask, double bid, double changed, int volume) {
        ListingHistoryModel listingHistoryModel = new ListingHistoryModel();
        listingHistoryModel.setTicker(ticker);
        listingHistoryModel.setDate(date);
        listingHistoryModel.setPrice(price);
        listingHistoryModel.setAsk(ask);
        listingHistoryModel.setBid(bid);
        listingHistoryModel.setChanged(changed);
        listingHistoryModel.setVolume(volume);
        return listingHistoryModel;
    }

    public ListingHistoryModel updateHistoryListingWithNewData(ListingHistoryModel oldModel, ListingHistoryModel newModel) {
        oldModel.setPrice(newModel.getPrice());
        oldModel.setAsk(newModel.getAsk());
        oldModel.setBid(newModel.getBid());
        oldModel.setChanged(newModel.getChanged());
        oldModel.setVolume(newModel.getVolume());

        return oldModel;
    }



}


 */