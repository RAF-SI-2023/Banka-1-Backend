package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingHistoryModel;
import rs.edu.raf.banka1.model.ListingModel;

import java.time.ZoneId;
import java.util.Date;

@Component
public class ListingMapper {
    public ListingHistoryModel listingModelToListongHistoryModel(ListingModel listingModel) {
        ListingHistoryModel listingHistoryModel = new ListingHistoryModel();
        listingHistoryModel.setTicker(listingModel.getTicker());

        listingHistoryModel.setDate(listingModel.getLastRefresh().toLocalDate());

        listingHistoryModel.setPrice(listingModel.getPrice());
        listingHistoryModel.setAsk(listingModel.getAsk());
        listingHistoryModel.setBid(listingModel.getBid());
        listingHistoryModel.setChanged(listingModel.getChanged());
        listingHistoryModel.setVolume(listingModel.getVolume());
        return listingHistoryModel;
    }



}
