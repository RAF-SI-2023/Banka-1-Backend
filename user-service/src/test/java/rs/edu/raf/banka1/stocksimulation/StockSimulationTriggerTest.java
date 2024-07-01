package rs.edu.raf.banka1.stocksimulation;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TriggerContext;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockSimulationTriggerTest {
    @Mock
    private OrderService orderService;

    @Mock
    private MarketService marketService;

    private StockSimulationTrigger sut;

    @Test
    public void nextExecutionTest(){
        sut = new StockSimulationTrigger(orderService, marketService, 1L);
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setContractSize(10L);
        marketOrder.setProcessedNumber(5L);
        marketOrder.setListingType(ListingType.STOCK);
        marketOrder.setStatus(OrderStatus.PROCESSING);

        when(orderService.getOrderById(any())).thenReturn(marketOrder);
        ListingStockDto listingStockDto = new ListingStockDto();
        listingStockDto.setVolume(10);

        when(marketService.getStockById(any())).thenReturn(listingStockDto);

        when(marketService.getWorkingHoursForStock(any())).thenReturn(WorkingHoursStatus.OPENED);

        sut.nextExecution(new TriggerContext() {
            @Override
            public Instant lastScheduledExecution() {
                return null;
            }

            @Override
            public Instant lastActualExecution() {
                return null;
            }

            @Override
            public Instant lastCompletion() {
                return null;
            }
        });
    }
}
