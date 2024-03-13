package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.model.ListingModel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

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



}
