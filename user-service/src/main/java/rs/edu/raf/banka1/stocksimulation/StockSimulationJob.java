package rs.edu.raf.banka1.stocksimulation;

import lombok.RequiredArgsConstructor;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.model.OrderType;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;

import java.util.Random;

@RequiredArgsConstructor
public class StockSimulationJob implements Runnable {

    private final OrderService orderService;
    private final MarketService marketService;
    private final Long orderId;
    private final Random random = new Random();
    private final Double PERCENT = 0.1;

    @Override
    public void run() {
        processOrder(orderId);
    }

    private void processOrder(
            final Long orderId
    ){
        if(marketService.getWorkingHoursForStock(orderId) == WorkingHoursStatus.CLOSED)
            return;

        MarketOrder order = orderService.getOrderById(orderId);

        if(order.getStatus().equals(OrderStatus.DONE) || !order.getStatus().equals(OrderStatus.APPROVED))
            return;

        boolean processOrder = order.getLimitValue() == null || processOrder(order);

        if(!processOrder)
            return;


        if(order.getAllOrNone()){
            orderService.finishOrder(orderId);
            return;
        }

        Long processedNumber = random.nextLong(order.getContractSize()) + 1;

        if(order.getContractSize() <= order.getProcessedNumber() + processedNumber) {
            orderService.finishOrder(orderId);
            return;
        }

        orderService.setProcessedNumber(orderId, order.getProcessedNumber() + processedNumber);
    }

    private boolean processOrder(MarketOrder order){
        final ListingBaseDto listingBaseDto = marketService.getStockById(order.getStockId());

        Double stockPrice = listingBaseDto.getPrice();
        Double change = random.nextDouble(stockPrice*PERCENT);
        boolean plus = random.nextBoolean();
        stockPrice = plus ? (stockPrice + change) : (stockPrice - change);

        return (order.getOrderType().equals(OrderType.BUY) && stockPrice < order.getLimitValue())
            || stockPrice > order.getLimitValue();
    }
}
