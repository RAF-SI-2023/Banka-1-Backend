package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingBase;
import rs.edu.raf.banka1.model.dtos.ListingBaseDto;

@Component
public class ListingBaseMapper {
    public static ListingBaseDto toDto(ListingBase listingBase) {
        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setListingId(listingBase.getListingId());
        listingBaseDto.setExchangeName(listingBase.getExchangeName());
        listingBaseDto.setListingType(listingBase.getListingType());
        listingBaseDto.setTicker(listingBase.getTicker());
        listingBaseDto.setName(listingBase.getName());
        listingBaseDto.setLastRefresh(listingBase.getLastRefresh());
        listingBaseDto.setPrice(listingBase.getPrice());
        listingBaseDto.setHigh(listingBase.getHigh());
        listingBaseDto.setLow(listingBase.getLow());
        listingBaseDto.setPriceChange(listingBase.getPriceChange());
        listingBaseDto.setVolume(listingBase.getVolume());
        return listingBaseDto;
    }
}
