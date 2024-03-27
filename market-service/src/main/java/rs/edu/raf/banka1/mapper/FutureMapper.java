package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.OptionsModel;
import rs.edu.raf.banka1.model.dtos.ListingFutureDto;
import rs.edu.raf.banka1.model.dtos.OptionsDto;
@Component
public class FutureMapper {
    public void updateFuture(ListingFuture oldFuture, ListingFuture newFuture) {
        oldFuture.setLow(newFuture.getLow());
        oldFuture.setHigh(newFuture.getHigh());
        oldFuture.setPrice(newFuture.getPrice());
        oldFuture.setVolume(newFuture.getVolume());
        oldFuture.setName(newFuture.getName());
        oldFuture.setLastRefresh((int) (System.currentTimeMillis() / 1000));
    }

    public ListingFuture futureDtoToFutureModel(ListingFutureDto futureDto) {
        ListingFuture listingFuture = new ListingFuture();
        listingFuture.setVolume(futureDto.getVolume());
        listingFuture.setExchange(futureDto.getExchange());
        listingFuture.setHigh(futureDto.getHigh());
        listingFuture.setLow(futureDto.getLow());
        listingFuture.setTicker(futureDto.getTicker());
        listingFuture.setContractSize(futureDto.getContractSize());
        listingFuture.setContractUnit(futureDto.getContractUnit());
        listingFuture.setSettlementDate(futureDto.getSettlementDate());
        listingFuture.setOpenInterest(futureDto.getOpenInterest());
        listingFuture.setPrice(futureDto.getPrice());
        listingFuture.setPriceChange(futureDto.getPriceChange());
        listingFuture.setName(futureDto.getName());
        listingFuture.setLastRefresh((int) (System.currentTimeMillis() / 1000));
        return listingFuture;
    }
}
