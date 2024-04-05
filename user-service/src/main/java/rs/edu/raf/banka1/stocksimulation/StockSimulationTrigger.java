package rs.edu.raf.banka1.stocksimulation;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;

import java.time.Instant;
import java.util.Random;

@RequiredArgsConstructor
public class StockSimulationTrigger implements Trigger {
    private final OrderService orderService;
    private final MarketService marketService;
    private final Long orderId;
    private final Random random = new Random();

    @Override
    public Instant nextExecution(TriggerContext triggerContext) {
        MarketOrder marketOrder = orderService.getOrderById(orderId);

        final ListingBaseDto listingBaseDto = marketService.getStockById(marketOrder.getStockId());
        final double volume = listingBaseDto.getVolume();

        long remainingQuantity = marketOrder.getContractSize() - marketOrder.getProcessedNumber();
        long timeInterval = (long)random.nextDouble((24 * 60) / ( volume / remainingQuantity ) * 1000);

        timeInterval = marketService.getWorkingHoursForStock(listingBaseDto.getListingId()).equals(WorkingHoursStatus.AFTER_HOURS) ? timeInterval + 30*60 * 1000 : timeInterval;
        return Instant.now().plusMillis(timeInterval);
    }
}
