package rs.edu.raf.banka1.services.implementations;


import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.exceptions.InvalidOrderListingAmountException;
import rs.edu.raf.banka1.exceptions.NotEnoughCapitalAvailableException;
import rs.edu.raf.banka1.exceptions.OrderListingNotFoundByIdException;
import rs.edu.raf.banka1.exceptions.OrderNotFoundByIdException;
import rs.edu.raf.banka1.exceptions.ForbiddenException;
import rs.edu.raf.banka1.exceptions.*;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.*;
import rs.edu.raf.banka1.stocksimulation.StockSimulationJob;
import rs.edu.raf.banka1.stocksimulation.StockSimulationTrigger;
import rs.edu.raf.banka1.utils.Constants;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final MarketService marketService;

    private final TransactionService transactionService;
    private final CapitalService capitalService;
    private final BankAccountService bankAccountService;

    private final EmployeeRepository employeeRepository;
    private final MarginTransactionService marginTransactionService;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();
    private final TaskScheduler taskScheduler;
    private final Double PERCENT = 0.1;

    //Map for storing ScheduledFuture instances for every order - used for task cancellation (kills finished simulation threads).
    //Used only for orders simulated by this one server instance.
    private final Map<Long, ScheduledFuture<?>> scheduledFutureMap;

    public OrderServiceImpl(
        final OrderMapper orderMapper,
        final OrderRepository orderRepository,
        final MarketService marketService,
        final TaskScheduler taskScheduler,
        final TransactionService transactionService,
        final CapitalService capitalService,
        final BankAccountService bankAccountService,
        EmployeeRepository employeeRepository,
        MarginTransactionService marginTransactionService) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.marketService = marketService;
        this.taskScheduler = taskScheduler;
        this.transactionService = transactionService;
        this.capitalService = capitalService;
        this.employeeRepository = employeeRepository;
        this.bankAccountService = bankAccountService;
        this.marginTransactionService = marginTransactionService;

        scheduledFutureMap = new ConcurrentHashMap<>();
    }

    @Override
    public void createOrder(final CreateOrderRequest request, final User currentAuth, String bankAccountNumber) {
        MarketOrder order = orderMapper.requestToMarketOrder(request, currentAuth);
        if(currentAuth instanceof Customer) {
            BankAccount bankAccount = bankAccountService.findBankAccountByAccountNumber(bankAccountNumber);
            if(!((Customer)currentAuth).getAccountIds().contains(bankAccount)){
                //mozda ovde treba drugaciji exception?
                throw new ForbiddenException();
            }
            if(bankAccount.getCompany() == null && !order.getListingType().equals(ListingType.STOCK)){
                throw new ForbiddenException();
            }
            order.setCustomer((Customer)currentAuth);
            order.setBankAccountNumber(bankAccountNumber);
        }
        else if(currentAuth instanceof Employee){
            if(request.getIsMargin() != null && request.getIsMargin()) {
                //Employee cannot create a margin order
                throw new ForbiddenException();
            }
            order.setOwner((Employee)currentAuth);
        }
        if(order.getContractSize() <= 0) throw new InvalidOrderListingAmountException();
        ListingBaseDto listingBaseDto = getListingByOrder(order);

        if(listingBaseDto == null) throw new OrderListingNotFoundByIdException(order.getListingId());

        order.setPrice(calculatePrice(order,listingBaseDto,order.getContractSize()));
        order.setFee(calculateFee(request.getLimitValue(), order.getPrice()));
        order.setIsMargin(false);
        if(currentAuth instanceof Employee) {
            order.setOwner((Employee)currentAuth);
        }
        else{
            order.setCustomer((Customer)currentAuth);
        }
        order.setProcessedNumber(0L);

        if(!capitalService.hasEnoughCapitalForOrder(order))
            throw new NotEnoughCapitalAvailableException();

        if(currentAuth instanceof Employee) {
            if (adjustAgentLimit((Employee)currentAuth, order.getPrice())) {
                order.setStatus(OrderStatus.PROCESSING);
            } else {
                order.setStatus(OrderStatus.APPROVED);
            }
        }
        else if(currentAuth instanceof Customer){
            order.setStatus(OrderStatus.APPROVED);
        }

        orderRepository.save(order);

        //Will automatically throw an exception if there is insufficient capital to create order
        if(order.getStatus().equals(OrderStatus.APPROVED)) {
            reserveStockCapital(order);
        }

        startOrderSimulation(order.getId(), bankAccountNumber);
    }

    @Override
    public void startOrderSimulation(Long orderId, String bankAccountNumber) {
        orderRepository.updateUpdatedAtById(Instant.now(), orderId); // Update updatedAt to ensure multiple instances don't run the same simulation
        //Start simulation
        ScheduledFuture<?> future = taskScheduler.schedule(
            new StockSimulationJob(
                this,
                marketService,
                transactionService,
                capitalService,
                bankAccountService,
                    marginTransactionService,
                orderId,
                    bankAccountNumber
            ),
            new StockSimulationTrigger(
                this,
                marketService,
                orderId
            )
        );
        this.scheduledFutureMap.put(orderId, future);
    }

    @Override
    public ListingBaseDto getListingByOrder(MarketOrder order) {
        if(order.getListingType().equals(ListingType.STOCK)) {
            return marketService.getStockById(order.getListingId());
        } else if(order.getListingType().equals(ListingType.FOREX)) {
            return marketService.getForexById(order.getListingId());
        }else if(order.getListingType().equals(ListingType.OPTIONS) && order.getOrderType().equals(OrderType.BUY)){
            return marketService.getCallOptionById(order.getListingId());
        }else if(order.getListingType().equals(ListingType.OPTIONS) && order.getOrderType().equals(OrderType.SELL)){
            return marketService.getPutOptionById(order.getListingId());
        }
        return marketService.getFutureById(order.getListingId());

    }

    @Override
    public List<OrderDto> getAllOrdersForEmployee(Employee currentAuth) {
        return orderRepository.getAllByOwner(currentAuth).stream().map(orderMapper::marketOrderToOrderDto).collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream().map(orderMapper::marketOrderToOrderDto).collect(Collectors.toList());
    }

    @Override
    public void cancelOrder(Long orderId) {
        orderRepository.cancelOrder(OrderStatus.CANCELLED, orderId);
    }

    @Override
    public DecideOrderResponse decideOrder(Long orderId, String status, Employee currentAuth) {
        Optional<MarketOrder> marketOrderOpt = this.orderRepository.fetchById(orderId);
        if(marketOrderOpt.isEmpty()) {
            return DecideOrderResponse.NOT_POSSIBLE;
        }
        MarketOrder marketOrder = marketOrderOpt.get();
        if(!marketOrder.getStatus().equals(OrderStatus.PROCESSING)) return DecideOrderResponse.NOT_POSSIBLE;
        Employee ownerEmployee = marketOrder.getOwner();

        if(status.toUpperCase().equals(OrderStatus.APPROVED.name()) ||
            status.toUpperCase().equals(OrderStatus.DENIED.name())) {
            marketOrder.setStatus(OrderStatus.valueOf(status.toUpperCase()));
            marketOrder.setUpdatedAt(Instant.now());
            if(status.toUpperCase().equals(OrderStatus.APPROVED.name())){
                marketOrder.setApprovedBy(currentAuth);
                if(ownerEmployee.getOrderlimit() != null && ownerEmployee.getLimitNow() != null) {
//                    ownerEmployee.setLimitNow(Math.min(ownerEmployee.getLimitNow() + marketOrder.getPrice(), ownerEmployee.getOrderlimit()));
                    ownerEmployee.setLimitNow(ownerEmployee.getLimitNow() + marketOrder.getPrice()); // Voditi racuna na frontu da available ne ode u minus
                    this.employeeRepository.save(marketOrder.getOwner());
                }
                reserveStockCapital(marketOrder);
            }
//            if(status.toUpperCase().equals(OrderStatus.DENIED.name())){
//                if(ownerEmployee.getOrderlimit() != null && ownerEmployee.getLimitNow() != null) {
//                    ownerEmployee.setLimitNow(Math.max(ownerEmployee.getLimitNow() - marketOrder.getPrice(), 0));
//                    this.employeeRepository.save(marketOrder.getOwner());
//                }
//                reserveStockCapital(marketOrder);
//            }
            this.orderRepository.save(marketOrder);
            return DecideOrderResponse.valueOf(status.toUpperCase());
        }

        return DecideOrderResponse.NOT_POSSIBLE;
    }

    @Override
    public MarketOrder getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundByIdException(orderId));
    }

    @Override
    public void finishOrder(Long orderId) {
        this.orderRepository.finishOrder(orderId, OrderStatus.DONE);
        releaseLeftoverReservedFunds(orderId);
//        updateLimit(orderId);
        this.scheduledFutureMap.get(orderId).cancel(false);
    }

    @Override
    public void setProcessedNumber(Long orderId, Long processedNumber) {
        this.orderRepository.changeProcessedNumber(orderId, processedNumber);
    }

    @Override
    public List<MarketOrder> getInactiveOrders(Instant timeThreshold) {
        return orderRepository.findByStatusAndUpdatedAtLessThanEqual(OrderStatus.APPROVED, timeThreshold);
    }

    @Override
    public Double calculatePrice(
        final MarketOrder order,
        final ListingBaseDto listingBaseDto,
        final long proccessNum
    ) {
        if(order.getOrderType().equals(OrderType.BUY)) {
            // ako je market order processednumber* price
            // ako je limit order processednumber *
            return proccessNum * (order.getLimitValue() != null ?
                Math.min(listingBaseDto.getHigh(), order.getLimitValue()) :
                order.getStopValue() !=null ? listingBaseDto.getHigh() :
                listingBaseDto.getPrice()) * 100;
        } else {
            return proccessNum * (order.getLimitValue() != null ?
                Math.max(listingBaseDto.getLow(), order.getLimitValue()) :
                order.getStopValue() !=null ? listingBaseDto.getHigh() :
                listingBaseDto.getPrice()) * 100;
        }
    }

    public Double calculateFee(final Double limitValue, final Double price) {
        return limitValue == null ?
                Math.min(0.14 * price, 7) : Math.min(0.24 * price, 12);
    }

    boolean adjustAgentLimit(final Employee currentAuth, Double orderPrice) {
        if(!currentAuth.getPosition().equalsIgnoreCase(Constants.AGENT)){
            return false;
        }
        boolean exceedLimit = currentAuth.getOrderlimit() != null && currentAuth.getLimitNow() != null &&
            currentAuth.getLimitNow() + orderPrice >= currentAuth.getOrderlimit();
        if(!exceedLimit && currentAuth.getLimitNow() != null){
            currentAuth.setLimitNow(currentAuth.getLimitNow() + orderPrice);
            employeeRepository.save(currentAuth);
        }
        return currentAuth.getRequireApproval() ||
            (currentAuth.getOrderlimit() != null && currentAuth.getLimitNow() != null &&
                currentAuth.getLimitNow() + orderPrice >= currentAuth.getOrderlimit());
    }

    void reserveStockCapital(MarketOrder order) {
        BankAccount bankAccount = bankAccountService.getDefaultBankAccount();
        if(order.getOrderType().equals(OrderType.BUY)) {
            bankAccountService.reserveBalance(bankAccount, order.getPrice());
        } else {
            Capital securityCapital = capitalService.getCapitalByListingIdAndTypeAndBankAccount(order.getListingId(), order.getListingType(), bankAccount);
            capitalService.reserveBalance(securityCapital.getListingId(), securityCapital.getListingType(), bankAccount, (double)order.getContractSize());
        }

    }

    void updateLimit(Long orderId) {
//        List<TransactionDto> transactionsForOrder=this.transactionService.getTransactionsForOrderId(orderId);
        MarketOrder order = this.orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundByIdException(orderId));
        Employee owner = order.getOwner();
        double spentMoney = 0;
        if(order.getOrderType().equals(OrderType.BUY)) {
           spentMoney = transactionService.getActualBuyPriceForOrder(order);
        } else {
            spentMoney = transactionService.getActualSellPriceForOrder(order);
        }

        double difference = spentMoney - order.getPrice();

        double newLimit = owner.getLimitNow() + Math.max(difference, 0) - Math.min(difference, 0);

//        if(newLimit > owner.getOrderlimit()) {
//            //Limit je predjen, stavi order na processing
//            order.setStatus(OrderStatus.PROCESSING);
//            this.orderRepository.save(order);
//        }

        owner.setLimitNow(newLimit);
        this.employeeRepository.save(owner);
    }

    @Override
    public void updateEmployeeLimit(Long orderId) {
        MarketOrder order = this.orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundByIdException(orderId));

        updateLimitOnTransaction(order);

        if(order.getContractSize().equals(order.getProcessedNumber()))
            updateLimitOnFinish(order);
    }

    @Override
    public List<OrderDto> getAllOrdersForLegalPerson(Customer customer) {
        return orderRepository.getAllByCustomer(customer).stream().map(orderMapper::marketOrderToOrderDto).collect(Collectors.toList());
    }

    private Double getSpentMoneyForOrder(MarketOrder order) {
        double spentMoney = 0;
        if(order.getOrderType().equals(OrderType.BUY)) {
            spentMoney = transactionService.getActualBuyPriceForOrder(order);
        } else {
            spentMoney = transactionService.getActualSellPriceForOrder(order);
        }

        return spentMoney;
    }

    private void releaseLeftoverReservedFunds(Long orderId) {
        MarketOrder order = getOrderById(orderId);
        double spentMoney = getSpentMoneyForOrder(order);
        BankAccount bankAccount = bankAccountService.getDefaultBankAccount();

        if(order.getOrderType().equals(OrderType.BUY)) {
            double leftOver = order.getPrice() - spentMoney;
            bankAccountService.releaseReserved(bankAccount, leftOver);
        }
    }

    private void updateLimitOnTransaction(MarketOrder order) {
        Employee owner = order.getOwner();

        double spentMoney = getSpentMoneyForOrder(order);
        if(spentMoney > order.getPrice()) {
            double lastTransactionValue = transactionService.getLastTransactionValueForOrder(order);
            double previousDifference = Math.max(spentMoney - lastTransactionValue - order.getPrice(), 0);
            double difference = spentMoney - order.getPrice() - previousDifference;
            double newLimit = owner.getLimitNow() + difference;
            owner.setLimitNow(newLimit);
            this.employeeRepository.save(owner);
        }

//        if(owner.getLimitNow() > owner.getOrderlimit()) {
//            //Limit je predjen, stavi order na processing
//            order.setStatus(OrderStatus.PROCESSING);
//            this.orderRepository.save(order);
//        }
    }

    private void updateLimitOnFinish(MarketOrder order) {
        Employee owner = order.getOwner();
        double spentMoney = getSpentMoneyForOrder(order);
        if(spentMoney < order.getPrice()) {
            double difference = order.getPrice() - spentMoney;
            double newLimit = owner.getLimitNow() - difference;
            owner.setLimitNow(newLimit);
            this.employeeRepository.save(owner);
        }
    }

}
