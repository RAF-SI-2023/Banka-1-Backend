package rs.edu.raf.banka1.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.services.OrderService;

import java.time.Instant;
import java.util.List;

@EnableScheduling
@Service
@RequiredArgsConstructor
public class StockSimulationScheduler {

    private final OrderService orderService;

    private final int INACTIVE_ORDER_THRESHOLD = 3600; // After 1 hour assume that the simulation failed, take over and run again

    @Scheduled(fixedRate = INACTIVE_ORDER_THRESHOLD * 1000)
    void handleInactiveOrderSimulations() {
        List<MarketOrder> orders = orderService.getInactiveOrders(Instant.now().minusSeconds(INACTIVE_ORDER_THRESHOLD));
        orders.forEach((MarketOrder order) -> {
            orderService.startOrderSimulation(order.getId(), order.getBankAccountNumber());
        });
    }
}
