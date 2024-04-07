package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.market_service.*;
import rs.edu.raf.banka1.model.WorkingHoursStatus;

import java.util.List;

public interface MarketService {
    List<ListingStockDto> getAllStocks();
    List<ListingFutureDto> getAllFutures();
    List<ListingForexDto> getAllForex();

    ListingStockDto getStockById(Long stockId);
    ListingFutureDto getFutureById(Long futureId);
    ListingForexDto getForexById(Long forexId);
}
