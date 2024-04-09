package rs.edu.raf.banka1.services.implementations;


import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.exceptions.OrderNotFoundByIdException;
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

    private final EmployeeRepository employeeRepository;

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
        EmployeeRepository employeeRepository) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.marketService = marketService;
        this.taskScheduler = taskScheduler;
        this.transactionService = transactionService;
        this.capitalService = capitalService;
        this.employeeRepository = employeeRepository;

        scheduledFutureMap = new HashMap<>();
    }

    @Override
    public void createOrder(final CreateOrderRequest request, final Employee currentAuth) {
        final MarketOrder order = orderMapper.requestToMarketOrder(request, currentAuth);
        ListingBaseDto listingBaseDto;

        if(order.getListingType().equals(ListingType.STOCK)) {
            listingBaseDto = marketService.getStockById(order.getListingId());
        } else if(order.getListingType().equals(ListingType.FOREX)) {
            listingBaseDto = marketService.getForexById(order.getListingId());
        } else {
            listingBaseDto = marketService.getFutureById(order.getListingId());
        }

        if(listingBaseDto == null) return;

        order.setPrice(calculatePrice(order,listingBaseDto,order.getContractSize()));
        order.setFee(calculateFee(request.getLimitValue(), order.getPrice()));
        order.setOwner(currentAuth);

        if(!currentAuth.getPosition().equalsIgnoreCase(Constants.SUPERVIZOR)) {
            if(currentAuth.getOrderlimit() < currentAuth.getLimitNow() + order.getPrice()) {
                order.setStatus(OrderStatus.DENIED);
            } else {
                currentAuth.setLimitNow(currentAuth.getLimitNow() + order.getPrice());
            }
        }

        if (!orderRequiresApprove(currentAuth)) {
            order.setStatus(OrderStatus.APPROVED);
        } else {
            order.setStatus(OrderStatus.PROCESSING);
        }
        orderRepository.save(order);

        //Will automatically throw an exception if there is insufficient capital to create order
        if(order.getStatus().equals(OrderStatus.APPROVED)) {
            reserveStockCapital(order);
        }

        startOrderSimulation(order.getId());
    }

    @Override
    public void startOrderSimulation(Long orderId) {
        orderRepository.updateUpdatedAtById(Instant.now(), orderId); // Update updatedAt to ensure multiple instances don't run the same simulation
        //Start simulation
        ScheduledFuture<?> future = taskScheduler.schedule(
            new StockSimulationJob(
                this,
                marketService,
                transactionService,
                capitalService,
                orderId
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

    public Boolean checkStockPriceForStopOrder(Long marketOrderId, Long stockId) {
        Optional<MarketOrder> optMarketOrder = orderRepository.findById(marketOrderId);
        if (optMarketOrder.isEmpty()) return false;
        MarketOrder marketOrder = optMarketOrder.get();
        ListingBaseDto listingBase = marketService.getStockById(stockId);

        Double ask = listingBase.getHigh();
        Double bid = listingBase.getLow();

        Double changeAsk = random.nextDouble(ask * PERCENT);
        boolean plusAsk = random.nextBoolean();
        ask = plusAsk ? (ask + changeAsk) : (ask - changeAsk);
        Double changeBid = random.nextDouble(bid * PERCENT);
        boolean plusBid = random.nextBoolean();
        bid = plusBid ? (bid + changeBid) : (bid - changeBid);


        if(marketOrder.getOrderType().equals(OrderType.BUY) && ask > marketOrder.getStopValue()) {

            if(marketOrder.getLimitValue() == null) {
                marketOrder.setPrice(calculatePrice(ask, marketOrder.getContractSize()));
            } else {
                marketOrder.setPrice(calculatePriceForLimitOrder(
                    marketOrder.getOrderType(),
                    marketOrder.getContractSize(),
                    marketOrder.getLimitValue(),
                    ask));
            }
            marketOrder.setFee(calculateFee(marketOrder.getLimitValue(), marketOrder.getPrice()));
            if (!orderRequiresApprove(marketOrder.getOwner())) {
                marketOrder.setStatus(OrderStatus.APPROVED);
            } else {
                marketOrder.setStatus(OrderStatus.PROCESSING);
            }
            orderRepository.save(marketOrder);
            return true;

        } else if (bid < marketOrder.getStopValue()){ // SELL
            if(marketOrder.getLimitValue() == null) {
                marketOrder.setPrice(calculatePrice(bid, marketOrder.getContractSize()));
            } else {
                marketOrder.setPrice(calculatePriceForLimitOrder(
                    marketOrder.getOrderType(),
                    marketOrder.getContractSize(),
                    marketOrder.getLimitValue(),
                    bid));
            }
            marketOrder.setFee(calculateFee(marketOrder.getLimitValue(), marketOrder.getPrice()));
            if (!orderRequiresApprove(marketOrder.getOwner())) {
                marketOrder.setStatus(OrderStatus.APPROVED);
            } else {
                marketOrder.setStatus(OrderStatus.PROCESSING);
            }
            orderRepository.save(marketOrder);
            return true;

        }
        return false;
    }

    private Double calculatePriceForLimitOrder(OrderType orderType, Long contractSize, Double limitValue, Double stockPrice) {
        if(orderType.equals(OrderType.BUY)) {
            return contractSize * Math.min(stockPrice, limitValue); // high(ask) umesto stockPrice
        } else {
            return contractSize * Math.max(stockPrice, limitValue); // low(bid) umesto stockPrice
        }
    }

    private Double calculatePrice(final Double price, final Long contractSize) {
        return price * contractSize;
    }

    @Override
    public MarketOrder getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundByIdException(orderId));
    }

    @Override
    public void finishOrder(Long orderId) {
        this.orderRepository.finishOrder(orderId, OrderStatus.DONE);
        updateLimit(orderId);
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
                listingBaseDto.getPrice());
        } else {
            return proccessNum * (order.getLimitValue() != null ?
                Math.max(listingBaseDto.getLow(), order.getLimitValue()) :
                order.getStopValue() !=null ? listingBaseDto.getHigh() :
                listingBaseDto.getPrice());
        }
    }

    private Double calculateFee(final Double limitValue, final Double price) {
        return limitValue == null ?
                Math.min(0.14 * price, 7) : Math.min(0.24 * price, 12);
    }

    private boolean orderRequiresApprove(final Employee currentAuth) {
        System.out.println(currentAuth);
        return currentAuth.getRequireApproval() || (currentAuth.getOrderlimit() != null && currentAuth.getLimitNow() != null && currentAuth.getLimitNow() >= currentAuth.getOrderlimit());
    }

    private void reserveStockCapital(MarketOrder order) {
        if(!order.getListingType().equals(ListingType.STOCK))
            return;

        Capital bankAccountCapital = capitalService.getCapitalByCurrencyCode("RSD");
        Capital securityCapital = capitalService.getCapitalByListingIdAndType(order.getListingId(), order.getListingType());

        if(order.getOrderType().equals(OrderType.BUY)) {
            capitalService.reserveBalance(bankAccountCapital.getCurrency().getCurrencyCode(), order.getPrice());
        } else {
            capitalService.reserveBalance(securityCapital.getListingId(), securityCapital.getListingType(), (double)order.getContractSize());
        }

    }

    private void updateLimit(Long orderId) {
        List<TransactionDto> transactionsForOrder=this.transactionService.getTransactionsForOrderId(orderId);
        MarketOrder order = this.orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundByIdException(orderId));
        Employee owner = order.getOwner();
        double spentMoney = transactionsForOrder.stream()
                .mapToDouble(TransactionDto::getBuy)
                .sum();

        double difference = spentMoney - order.getPrice();

        owner.setLimitNow(owner.getLimitNow() + Math.max(difference, 0) - Math.min(difference, 0));
        this.employeeRepository.save(owner);
    }
}
