package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.model.dtos.ListingBaseDto;
import rs.edu.raf.banka1.model.dtos.ListingFutureDto;
@Component
public class FutureMapper {
    public ListingFuture updateFuture(ListingFuture oldFuture, ListingFuture newFuture) {
        oldFuture.setLow(newFuture.getLow());
        oldFuture.setHigh(newFuture.getHigh());
        oldFuture.setPrice(newFuture.getPrice());
        oldFuture.setVolume(newFuture.getVolume());
        oldFuture.setName(newFuture.getName());
        oldFuture.setLastRefresh((int) (System.currentTimeMillis() / 1000));
        return oldFuture;
    }

    public ListingFuture futureDtoToFutureModel(ListingFutureDto futureDto) {
        ListingFuture listingFuture = new ListingFuture();
        listingFuture.setVolume(futureDto.getVolume());
        listingFuture.setExchangeName(futureDto.getExchangeName());
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
        listingFuture.setAlternativeTicker(futureDto.getAlternativeTicker());
        listingFuture.setListingType("Future");
        listingFuture.setLastRefresh((int) (System.currentTimeMillis() / 1000));
        return listingFuture;
    }
}
