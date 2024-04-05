package rs.edu.raf.banka1.stocksimulation;

import lombok.RequiredArgsConstructor;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.services.OrderService;

import java.time.Instant;
import java.util.Random;

@RequiredArgsConstructor
public class StockSimulationJob implements Runnable {

    private final OrderService orderService;
    private final Long orderId;
    private final Random random = new Random();

    @Override
    public void run() {
        processOrder(orderId, WorkingHoursStatus.OPENED);
    }

    private void processOrder(
            final Long orderId,
            final WorkingHoursStatus workingHours
    ){
        if(workingHours==WorkingHoursStatus.CLOSED)
            return;

        MarketOrder marketOrder = orderService.getOrderById(orderId);

        if(!marketOrder.getStatus().equals(OrderStatus.APPROVED)) return;

        if(marketOrder.getAllOrNone()){
            orderService.finishOrder(orderId);
            return;
        }

        Long processedNumber = random.nextLong(marketOrder.getContractSize()) + 1;

        if(marketOrder.getContractSize() <= marketOrder.getProcessedNumber() + processedNumber) {
            orderService.finishOrder(orderId);
            return;
        }

        orderService.setProcessedNumber(orderId, marketOrder.getProcessedNumber() + processedNumber);
    }
}
