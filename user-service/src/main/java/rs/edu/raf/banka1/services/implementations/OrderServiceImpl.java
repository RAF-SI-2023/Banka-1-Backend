package rs.edu.raf.banka1.services.implementations;


import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.exceptions.OrderNotFoundByIdException;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CapitalRepository;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.repositories.TransactionRepository;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;
import rs.edu.raf.banka1.stocksimulation.StockSimulationJob;
import rs.edu.raf.banka1.stocksimulation.StockSimulationTrigger;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final MarketService marketService;
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CapitalRepository capitalRepository;
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
        final TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository, CapitalRepository capitalRepository,
        final TaskScheduler taskScheduler
    ) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.marketService = marketService;
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.capitalRepository = capitalRepository;
        this.taskScheduler = taskScheduler;

        scheduledFutureMap = new HashMap<>();
    }

    @Override
    public void createOrder(final CreateOrderRequest request, final Employee currentAuth) {
        final MarketOrder order = orderMapper.requestToMarketOrder(request);
        // todo forex future
        final ListingBaseDto listingBaseDto = marketService.getStockById(order.getListingId());
        order.setPrice(calculatePrice(order,listingBaseDto,order.getContractSize()));
        order.setFee(calculateFee(request.getLimitValue(), order.getPrice()));
        order.setOwner(currentAuth);
        if (!orderRequiresApprove(currentAuth)) {
            order.setStatus(OrderStatus.APPROVED);
        } else {
            order.setStatus(OrderStatus.PROCESSING);
        }
        orderRepository.save(order);

//        if(order.getLimitValue() == null && order.getStopValue() == null)
//            startOrderSimulation(order.getId());
        // ako je marketorder onda odmah startorderSimulation a ako je stop\


        //

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
                transactionRepository,
                capitalRepository,
                bankAccountRepository,
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
        return currentAuth.getRequireApproval() || currentAuth.getLimitNow() >= currentAuth.getOrderlimit();
    }
}
