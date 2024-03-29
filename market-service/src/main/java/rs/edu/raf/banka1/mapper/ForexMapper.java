package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.dtos.ListingBaseDto;
import rs.edu.raf.banka1.model.dtos.ListingForexDto;

import java.time.Instant;

@Component
public class ForexMapper {
    public ListingForex createForex(String ticker, String name, String displayName) {
        String[] symbolArr = ticker.split("/");
        String baseCurrency = symbolArr[0];
        String quoteCurrency = symbolArr[1];
        Double exchangeRate = (double) 0;

        ListingForex forex = new ListingForex();
        forex.setTicker(ticker);
        forex.setName(name);
        forex.setExchangeName(displayName);
        forex.setBaseCurrency(baseCurrency);
        forex.setQuoteCurrency(quoteCurrency);
        forex.setListingType("forex");

//        Default is 0
        forex.setPrice(0.0);
        forex.setHigh(0.0);
        forex.setLow(0.0);
        forex.setVolume(0);
        forex.setPriceChange(0.0);

        int currentTime = (int) Instant.now().getEpochSecond();
        forex.setLastRefresh(currentTime);

        return forex;
    }

    public ListingForexDto toDto(ListingForex listingForex) {
        ListingForexDto dto = new ListingForexDto();
        settingFieldsForListingBaseDto(listingForex, dto);
        dto.setBaseCurrency(listingForex.getBaseCurrency());
        dto.setQuoteCurrency(listingForex.getQuoteCurrency());

        return dto;
    }

    public ListingBaseDto forexToListingBaseDto(ListingForex listingForex) {
        ListingBaseDto dto = new ListingBaseDto();
        settingFieldsForListingBaseDto(listingForex, dto);

        return dto;
    }

    public void settingFieldsForListingBaseDto(ListingForex listingForex, ListingBaseDto dto) {
        dto.setListingId(listingForex.getListingId());
        dto.setListingType(listingForex.getListingType());
        dto.setTicker(listingForex.getTicker());
        dto.setName(listingForex.getName());
        dto.setLastRefresh(listingForex.getLastRefresh());
        dto.setPrice(listingForex.getPrice());
        dto.setHigh(listingForex.getHigh());
        dto.setLow(listingForex.getLow());
        dto.setPriceChange(listingForex.getPriceChange());
        dto.setVolume(listingForex.getVolume());
    }

}
