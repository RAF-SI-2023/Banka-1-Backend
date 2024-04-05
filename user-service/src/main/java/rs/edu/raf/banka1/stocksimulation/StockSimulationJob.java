package rs.edu.raf.banka1.stocksimulation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.ListingBaseDto;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;

import java.util.Random;

@Component
public class StockSimulationJob implements Runnable {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MarketService marketService;

    private final Random random;

    private final Long orderId;

    public StockSimulationJob(final Long orderId) {
        this.orderId = orderId;
        this.random = new Random();
    }

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

        //final ListingBaseDto listingBaseDto = marketService.getStock(marketOrder.getStockId());

        Long processedNumber = random.nextLong(marketOrder.getContractSize()) + 1;

        if(marketOrder.getContractSize() <= marketOrder.getProcessedNumber() + processedNumber) {
            orderService.finishOrder(orderId);
            return;
        }

        orderService.setProcessedNumber(orderId, marketOrder.getProcessedNumber() + processedNumber);

        //System.out.println(marketOrder);

//        final Long volume = Long.valueOf(listingBaseDto.getVolume());
//        Long remainingQuantity = marketOrder.getContractSize() - marketOrder.getProcessedNumber();
//
//        long timeInterval = random.nextLong(24*60/(volume/remainingQuantity));
//        timeInterval = workingHours.equals(WorkingHoursStatus.AFTER_HOURS) ? timeInterval + 30*60 : timeInterval;

        //executorService.schedule(() -> processOrder(orderId, workingHours), timeInterval, TimeUnit.SECONDS);
    }
}
