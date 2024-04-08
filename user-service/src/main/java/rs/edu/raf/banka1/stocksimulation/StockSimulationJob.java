package rs.edu.raf.banka1.stocksimulation;

import lombok.RequiredArgsConstructor;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CapitalRepository;
import rs.edu.raf.banka1.repositories.TransactionRepository;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;

import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
public class StockSimulationJob implements Runnable {

    private final OrderService orderService;
    private final MarketService marketService;
    private final TransactionRepository transactionRepository;
    private final CapitalRepository capitalRepository;
    private final BankAccountRepository bankAccountRepository;
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

        final ListingBaseDto listingBaseDto = marketService.getStockById(order.getListingId());

        boolean processOrder = (order.getLimitValue() == null || processLimitOrder(order,listingBaseDto))
            && (order.getStopValue() == null || processStopOrder(order,listingBaseDto));

        if(!processOrder)
            return;

        Long processedNumber = (order.getAllOrNone() || order.getListingType().equals(ListingType.FUTURE) || order.getListingType().equals(ListingType.FOREX)) ? order.getContractSize() : Math.min(
            random.nextLong(order.getContractSize()) + 1,
            order.getContractSize() - order.getProcessedNumber()
        );

        createTransaction(order,listingBaseDto, processedNumber, "RSD");
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

//        if(marketOrder.getOrderType().equals(OrderType.BUY) && ask > marketOrder.getStopValue()) {
//
//            if(marketOrder.getLimitValue() == null) {
//                marketOrder.setPrice(calculatePrice(ask, marketOrder.getContractSize()));
//            } else {
//                marketOrder.setPrice(calculatePriceForLimitOrder(
//                    marketOrder.getOrderType(),
//                    marketOrder.getContractSize(),
//                    marketOrder.getLimitValue(),
//                    ask));
//            }
//            marketOrder.setFee(calculateFee(marketOrder.getLimitValue(), marketOrder.getPrice()));
//
//        } else if (bid < marketOrder.getStopValue()){ // SELL
//            if(marketOrder.getLimitValue() == null) {
//                marketOrder.setPrice(calculatePrice(bid, marketOrder.getContractSize()));
//            } else {
//                marketOrder.setPrice(calculatePriceForLimitOrder(
//                    marketOrder.getOrderType(),
//                    marketOrder.getContractSize(),
//                    marketOrder.getLimitValue(),
//                    bid));
//            }
//            marketOrder.setFee(calculateFee(marketOrder.getLimitValue(), marketOrder.getPrice()));
//            return true;
//        }
    }

    //todo treba da se radi sa currency i da se doda u listingdto exchangedto koji ce da ima i currency u sebi
    private void createTransaction(MarketOrder order, ListingBaseDto listingBaseDto, Long processedNum, String currencyCode){
        Double price = orderService.calculatePrice(order,listingBaseDto,processedNum);
        price = convertPrice(price,null,null);
        Capital capitalBankAccount = capitalRepository.getCapitalByCurrency_CurrencyCode(currencyCode); // vraca bank account u odredjenoj valuti
        // uvek racun u Dinarima
        BankAccount bankAccount = capitalBankAccount.getBankAccount();
        Capital capitalStock = capitalRepository.getCapitalByListingIdAndListingType(order.getListingId(),ListingType.STOCK);

        Transaction transaction = new Transaction();
        transaction.setCurrency(capitalBankAccount.getCurrency());
        transaction.setBankAccount(bankAccount);

        if(order.getOrderType().equals(OrderType.BUY)){
            transaction.setBuy(price);
            capitalStock.setTotal( capitalStock.getTotal() + processedNum);
            bankAccount.setBalance(bankAccount.getBalance() - price);
        } else{
            transaction.setSell(price);
            capitalStock.setTotal( capitalStock.getTotal() - processedNum);
            bankAccount.setBalance( bankAccount.getBalance() + price );
        }
        transaction.setEmployee(order.getOwner());

        transactionRepository.save(transaction);
        capitalRepository.save(capitalStock);
        capitalRepository.save(capitalBankAccount);
        bankAccountRepository.save(bankAccount);

    }

    //todo zvati market servis da konvertuje
    private Double convertPrice(Double price, Currency currency, Currency currencyDest){
        return price * 100;
    }
}
