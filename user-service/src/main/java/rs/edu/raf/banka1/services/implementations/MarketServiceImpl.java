package rs.edu.raf.banka1.services.implementations;

import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.ListingBaseDto;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.services.MarketService;

@Service
public class MarketServiceImpl implements MarketService {
    @Override
    public ListingBaseDto getStock(final Long stockId) {
        //Mock
        ListingBaseDto listingStockDto = new ListingBaseDto();
        listingStockDto.setPrice(99.99);
        listingStockDto.setHigh(100.00);
        listingStockDto.setLow(98.98);
        listingStockDto.setVolume(100);
        return listingStockDto;
    }

    @Override
    public WorkingHoursStatus getWorkingHours(final Long stockId) {
        return null;
    }
}
