package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.ListingBaseDto;
import rs.edu.raf.banka1.model.WorkingHoursStatus;

public interface MarketService {
    ListingBaseDto getStock(final Long stockId);
    WorkingHoursStatus getWorkingHours(final Long stockId);
}
