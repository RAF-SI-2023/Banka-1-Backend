package rs.edu.raf.banka1.services;

import org.springframework.http.HttpMethod;
import rs.edu.raf.banka1.dtos.market_service.*;
import rs.edu.raf.banka1.model.WorkingHoursStatus;

import java.util.List;

public interface MarketService {
    List<ListingStockDto> getAllStocks();
    List<ListingFutureDto> getAllFutures();
    List<ListingForexDto> getAllForex();
    List<OptionsDto> getAllOptions();
    ListingStockDto getStockById(Long stockId);
    ListingFutureDto getFutureById(Long futureId);
    ListingForexDto getForexById(Long forexId);
    OptionsDto getOptionsById(Long optionsId);
    OptionsDto getCallOptionById(Long optionsId);
    OptionsDto getPutOptionById(Long optionsId);
    WorkingHoursStatus getWorkingHoursForStock(Long stockId);

}
