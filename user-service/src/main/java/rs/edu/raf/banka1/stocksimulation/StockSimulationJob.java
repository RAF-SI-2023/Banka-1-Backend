package rs.edu.raf.banka1.stocksimulation;

import lombok.RequiredArgsConstructor;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.TransactionRepository;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;

import java.util.Random;

@RequiredArgsConstructor
public class StockSimulationJob implements Runnable {

    private final OrderService orderService;
    private final MarketService marketService;
    private final TransactionRepository transactionRepository;
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

        final ListingBaseDto listingBaseDto = marketService.getStockById(order.getStockId());

        boolean processOrder = order.getLimitValue() == null || processOrder(order,listingBaseDto);

        if(!processOrder)
            return;


        if(order.getAllOrNone()){
            orderService.finishOrder(orderId);
            return;
        }

        Long processedNumber = Math.min(
            random.nextLong(order.getContractSize()) + 1,
            order.getContractSize() - order.getProcessedNumber()
        );
        Double price = processedNumber * listingBaseDto.getPrice();
        Transaction transaction = new Transaction();
        if(order.getOrderType().equals(OrderType.BUY)){
            transaction.setBuy(price);
            transaction.getBankAccount().setBalance(transaction.getBankAccount().getBalance() - price);
        } else{
            transaction.setSell(price);
            transaction.getBankAccount().setBalance(transaction.getBankAccount().getBalance() + price);
        }
        transaction.setEmployee(order.getOwner());
//        transaction.setBankAccount(order.); TODO bank account trebalo bi employee da radi u banci i prebaciti u neki servis
        transactionRepository.save(transaction);

        if(order.getContractSize() == order.getProcessedNumber() + processedNumber) {
            orderService.finishOrder(orderId);
            return;
        }

        orderService.setProcessedNumber(orderId, order.getProcessedNumber() + processedNumber);
    }

    private boolean processOrder(MarketOrder order, ListingBaseDto listingBaseDto){

        Double stockPrice = listingBaseDto.getPrice();
        Double change = random.nextDouble(stockPrice*PERCENT);
        boolean plus = random.nextBoolean();
        stockPrice = plus ? (stockPrice + change) : (stockPrice - change);

        return (order.getOrderType().equals(OrderType.BUY) && stockPrice < order.getLimitValue())
            || stockPrice > order.getLimitValue();
    }
}
