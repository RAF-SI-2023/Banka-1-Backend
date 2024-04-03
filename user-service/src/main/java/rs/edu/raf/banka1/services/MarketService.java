package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.ListingStockDto;
import rs.edu.raf.banka1.model.WorkingHoursStatus;

public interface MarketService {
    ListingStockDto getStock(final Long stockId);
    WorkingHoursStatus getWorkingHours(final Long stockId);
}
