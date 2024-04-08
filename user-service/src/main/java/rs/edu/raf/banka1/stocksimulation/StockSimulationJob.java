package rs.edu.raf.banka1.stocksimulation;

import lombok.RequiredArgsConstructor;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.services.CapitalService;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;
import rs.edu.raf.banka1.services.TransactionService;

import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
public class StockSimulationJob implements Runnable {

    private final OrderService orderService;
    private final MarketService marketService;
    private final TransactionService transactionService;
    private final CapitalService capitalService;
    private final Long orderId;
    private final Random random = new Random();
    private final Double PERCENT = 0.1;
    private final String DEFAULT_CURRENCY = "RSD";

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

        final ListingBaseDto listingBaseDto = marketService.getStockById(order.getListingId());

        boolean processOrder = (order.getLimitValue() == null || processLimitOrder(order,listingBaseDto))
            && (order.getStopValue() == null || processStopOrder(order,listingBaseDto));

        if(!processOrder)
            return;

        Long processedNumber = (order.getAllOrNone() || order.getListingType().equals(ListingType.FUTURE) || order.getListingType().equals(ListingType.FOREX)) ? order.getContractSize() : Math.min(
            random.nextLong(order.getContractSize()) + 1,
            order.getContractSize() - order.getProcessedNumber()
        );

        createTransaction(order,listingBaseDto, processedNumber, DEFAULT_CURRENCY);
//        transaction.setBankAccount(order.); TODO bank account trebalo bi employee da radi u banci i prebaciti u neki servis

        if(order.getContractSize() == order.getProcessedNumber() + processedNumber) {
            orderService.finishOrder(orderId);
            return;
        }

        orderService.setProcessedNumber(orderId, order.getProcessedNumber() + processedNumber);
    }

    private boolean processLimitOrder(MarketOrder order, ListingBaseDto listingBaseDto){

        Double stockPrice = listingBaseDto.getPrice();
        Double change = random.nextDouble(stockPrice * PERCENT);
        boolean plus = random.nextBoolean();
        stockPrice = plus ? (stockPrice + change) : (stockPrice - change);

        return (order.getOrderType().equals(OrderType.BUY) && stockPrice < order.getLimitValue())
            || stockPrice > order.getLimitValue();
    }

    public Boolean processStopOrder(MarketOrder marketOrder, ListingBaseDto listingBase) {
        Double ask = listingBase.getHigh();
        Double bid = listingBase.getLow();

        Double changeAsk = random.nextDouble(ask * PERCENT);
        boolean plusAsk = random.nextBoolean();
        ask = plusAsk ? (ask + changeAsk) : (ask - changeAsk);
        Double changeBid = random.nextDouble(bid * PERCENT);
        boolean plusBid = random.nextBoolean();
        bid = plusBid ? (bid + changeBid) : (bid - changeBid);

        return (marketOrder.getOrderType().equals(OrderType.BUY) && ask > marketOrder.getStopValue())
            || (bid < marketOrder.getStopValue());
    }

    //todo treba da se radi sa currency i da se doda u listingdto exchangedto koji ce da ima i currency u sebi
    private void createTransaction(MarketOrder order, ListingBaseDto listingBaseDto, Long processedNum, String currencyCode){
        Capital bankAccountCapital = capitalService.getCapitalByCurrencyCode(currencyCode);
        Capital securityCapital = capitalService.getCapitalByListingIdAndType(listingBaseDto.getListingId(), listingBaseDto.getListingType());

        Double price = orderService.calculatePrice(order,listingBaseDto,processedNum);
        price = convertPrice(price,null,null);

        transactionService.createTransaction(bankAccountCapital, securityCapital, price, order, processedNum);
    }

    //todo zvati market servis da konvertuje
    private Double convertPrice(Double price, Currency currency, Currency currencyDest){
        return price * 100;
    }
}
