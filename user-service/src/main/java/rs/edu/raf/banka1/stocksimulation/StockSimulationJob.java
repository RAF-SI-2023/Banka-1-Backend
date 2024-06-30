package rs.edu.raf.banka1.stocksimulation;

import lombok.RequiredArgsConstructor;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.exceptions.InvalidReservationAmountException;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.services.*;
import rs.edu.raf.banka1.utils.Constants;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class StockSimulationJob implements Runnable {

    private final OrderService orderService;
    private final MarketService marketService;
    private final TransactionService transactionService;
    private final CapitalService capitalService;
    private final BankAccountService bankAccountService;
    private final MarginTransactionService marginTransactionService;
    private final Long orderId;
    private final String bankAccountNumber;
    private final Random random = new Random();
    private final Double PERCENT = 0.1;

    @Override
    public void run() {
        processOrder(orderId);
    }

    private void processOrder(
            final Long orderId
    ){
        MarketOrder order = orderService.getOrderById(orderId);

        //Check for stock open hours
        if(order.getListingType().equals(ListingType.STOCK) && marketService.getWorkingHoursForStock(order.getListingId()) == WorkingHoursStatus.CLOSED)
            return;

        //Check for order status
        if(!order.getStatus().equals(OrderStatus.APPROVED))
            return;

        final ListingBaseDto listingBaseDto = orderService.getListingByOrder(order);

        boolean processOrder = (order.getLimitValue() == null || processLimitOrder(order,listingBaseDto))
            && (order.getStopValue() == null || processStopOrder(order,listingBaseDto));

        if(!processOrder)
            return;

        Long processedNumber = (order.getAllOrNone()
                || order.getListingType().equals(ListingType.FUTURE) || order.getListingType().equals(ListingType.FOREX) || order.getListingType().equals(ListingType.OPTIONS)) ? order.getContractSize() : Math.min(
            random.nextLong(order.getContractSize()) + 1,
            order.getContractSize() - order.getProcessedNumber()
        );

        createTransaction(order,listingBaseDto, processedNumber, bankAccountNumber);
        if(bankAccountNumber != null && order.getOwner() != null) {
            orderService.updateEmployeeLimit(order.getId());// Ovo je ovde jer bi u transaction servisu bio circular dependency. Trebalo bi promeniti kasnije
        }

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

        return order.getOrderType().equals(OrderType.BUY) ? stockPrice < order.getLimitValue() :
            stockPrice > order.getLimitValue();
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

        return marketOrder.getOrderType().equals(OrderType.BUY) ? ask > marketOrder.getStopValue() :
            (bid < marketOrder.getStopValue());
    }

    private void createTransaction(MarketOrder order, ListingBaseDto listingBaseDto, Long processedNum, String bankAccountNumber) {
        if(!order.getIsMargin()) {
            createCashTransaction(order, listingBaseDto, processedNum, bankAccountNumber);
            return;
        }
        createMarginTransaction(order, listingBaseDto, processedNum, bankAccountNumber);
    }

    private void createMarginTransaction(MarketOrder order, ListingBaseDto listingBaseDto, Long processedNum, String bankAccountNumber) {
        TransactionType transactionType;
        BankAccount bankAccount;
        if(bankAccountNumber == null) {
            bankAccount = bankAccountService.getDefaultBankAccount();
        }
        else{
            bankAccount = bankAccountService.findBankAccountByAccountNumber(bankAccountNumber);
        }
        Capital securityCapital = capitalService.getCapitalByListingIdAndTypeAndBankAccount(listingBaseDto.getListingId(), ListingType.valueOf(listingBaseDto.getListingType().toUpperCase()), bankAccount);

        if (order.getOrderType() == OrderType.BUY) {
            Double oldAmount = securityCapital.getTotal();
            Double oldAverageBuyingPrice = securityCapital.getAverageBuyingPrice();
            Double newTotalPrice = order.getPrice();
            Long newAmount = order.getCurrentAmount();
            Double newAverageBuyingPrice = (oldAmount * oldAverageBuyingPrice + newTotalPrice) / (oldAmount + newAmount);
            securityCapital.setAverageBuyingPrice(newAverageBuyingPrice);

            transactionType = TransactionType.DEPOSIT;
        } else {
            transactionType = TransactionType.WITHDRAWAL;
        }

        Double price = orderService.calculatePrice(order,listingBaseDto,processedNum);
        //price = convertPrice(price,null,null);

        String description;
        if(transactionType.equals(TransactionType.DEPOSIT)) {
            description = "Uplaćivanje sredstava na račun - Initial Margin";
        } else {
            description = "Isplata kamate";
        }

        try {
            marginTransactionService.createTransaction(order, bankAccount, bankAccount.getCurrency(), description, transactionType, price, (double)processedNum);
        } catch (InvalidReservationAmountException e) {
            orderService.cancelOrder(orderId);
        }
    }

    //todo treba da se radi sa currency i da se doda u listingdto exchangedto koji ce da ima i currency u sebi
    private void createCashTransaction(MarketOrder order, ListingBaseDto listingBaseDto, Long processedNum, String bankAccountNumber){
        BankAccount bankAccount;
        if(bankAccountNumber == null) {
            bankAccount = bankAccountService.getDefaultBankAccount();
        }
        else{
            bankAccount = bankAccountService.findBankAccountByAccountNumber(bankAccountNumber);
        }
        Capital securityCapital = capitalService.getCapitalByListingIdAndTypeAndBankAccount(listingBaseDto.getListingId(), ListingType.valueOf(listingBaseDto.getListingType().toUpperCase()), bankAccount);

        if (order.getOrderType() == OrderType.BUY) {
            Double oldAmount = securityCapital.getTotal();
            Double oldAverageBuyingPrice = securityCapital.getAverageBuyingPrice();
            Double newTotalPrice = order.getPrice();
            Long newAmount = order.getCurrentAmount();
            Double newAverageBuyingPrice = (oldAmount * oldAverageBuyingPrice + newTotalPrice) / (oldAmount + newAmount);
            securityCapital.setAverageBuyingPrice(newAverageBuyingPrice);
        }
        
        Double price = orderService.calculatePrice(order,listingBaseDto,processedNum);
        //price = convertPrice(price,null,null);
        try {
            transactionService.createTransaction(bankAccount, securityCapital, price, order, processedNum);
        } catch (InvalidReservationAmountException e) {
            orderService.cancelOrder(orderId);
        }
    }

    //todo zvati market servis da konvertuje
//    private Double convertPrice(Double price, Currency currency, Currency currencyDest){
//        return price * 100;
//    }
}
