package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.model.ListingStock;
import rs.edu.raf.banka1.model.OptionsModel;
import rs.edu.raf.banka1.model.dtos.ListingBaseDto;
import rs.edu.raf.banka1.model.dtos.ListingForexDto;
import rs.edu.raf.banka1.model.dtos.ListingFutureDto;
import rs.edu.raf.banka1.model.dtos.OptionsDto;
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

    public ListingFutureDto toDto(ListingFuture listingFuture) {
        ListingFutureDto dto = new ListingFutureDto();
        settingFieldsForListingBaseDto(listingFuture, dto);
        dto.setOpenInterest(listingFuture.getOpenInterest());
        dto.setSettlementDate(listingFuture.getSettlementDate());
        dto.setContractSize(listingFuture.getContractSize());
        dto.setContractUnit(listingFuture.getContractUnit());
        dto.setAlternativeTicker(listingFuture.getAlternativeTicker());
        dto.setLastPrice(listingFuture.getPrice() - listingFuture.getPriceChange());
        return dto;
    }

    public void settingFieldsForListingBaseDto(ListingFuture listingFuture, ListingBaseDto dto) {
        dto.setListingId(listingFuture.getListingId());
        dto.setListingType(listingFuture.getListingType());
        dto.setTicker(listingFuture.getTicker());
        dto.setName(listingFuture.getName());
        dto.setExchangeName(listingFuture.getExchangeName());
        dto.setLastRefresh(listingFuture.getLastRefresh());
        dto.setExchangeName(listingFuture.getExchangeName());
        dto.setPrice(listingFuture.getPrice());
        dto.setHigh(listingFuture.getHigh());
        dto.setLow(listingFuture.getLow());
        dto.setPriceChange(listingFuture.getPriceChange());
        dto.setVolume(listingFuture.getVolume());
    }
}
