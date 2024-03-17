package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.Listing;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
public class ListingMapper {
    public ListingHistory listingModelToListongHistoryModel(Listing listing) {
        ListingHistory listingHistory = new ListingHistory();
        listingHistory.setTicker(listing.getTicker());

        // Convert the Unix timestamp to LocalDate
        LocalDate localDate = Instant.ofEpochSecond(listing.getLastRefresh()).atZone(ZoneOffset.UTC).toLocalDate();

        // Get the Unix timestamp for the beginning of the day
        long beginningOfDayUnixTimestamp = localDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        listingHistory.setDate(beginningOfDayUnixTimestamp);

        listingHistory.setPrice(listing.getPrice());
        listingHistory.setAsk(listing.getAsk());
        listingHistory.setBid(listing.getBid());
        listingHistory.setChanged(listing.getChanged());
        listingHistory.setVolume(listing.getVolume());
        return listingHistory;
    }

    public void listingModelUpdate(Listing listing, double price, double high, double low, double change, int volume) {
        listing.setPrice(price);
        listing.setAsk(high);
        listing.setBid(low);
        listing.setChanged(change);
        listing.setVolume(volume);
        listing.setLastRefresh((int) (System.currentTimeMillis() / 1000));
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
