package rs.edu.raf.banka1.services.implementations;


import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.exceptions.OrderNotFoundByIdException;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;
import rs.edu.raf.banka1.stocksimulation.StockSimulationJob;
import rs.edu.raf.banka1.stocksimulation.StockSimulationTrigger;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final MarketService marketService;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();
    private final TaskScheduler taskScheduler;

    //Map for storing ScheduledFuture instances for every order - used for task cancellation (kills finished simulation threads).
    //Used only for orders simulated by this one server instance.
    private final Map<Long, ScheduledFuture<?>> scheduledFutureMap;

    public OrderServiceImpl(
        final OrderMapper orderMapper,
        final OrderRepository orderRepository,
        final MarketService marketService,
        final TaskScheduler taskScheduler
    ) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.marketService = marketService;
        this.taskScheduler = taskScheduler;

        scheduledFutureMap = new HashMap<>();
    }

    @Override
    public void createOrder(final CreateOrderRequest request) {
        final MarketOrder marketOrder = orderMapper.requestToMarketOrder(request);
        final ListingBaseDto listingBaseDto = marketService.getStockById(request.getStockId());
        marketOrder.setPrice(calculatePrice(listingBaseDto.getPrice(), request.getContractSize()));
        marketOrder.setFee(calculateFee(request.getLimitValue(), marketOrder.getPrice()));
        // dok ne implementiramo approvovanje ordera
        marketOrder.setStatus(OrderStatus.APPROVED);
        orderRepository.save(marketOrder);

        startOrderSimulation(marketOrder.getId());
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
    public void startOrderSimulation(Long orderId) {
        orderRepository.updateUpdatedAtById(Instant.now(), orderId); // Update updatedAt to ensure multiple instances don't run the same simulation
        //Start simulation
        ScheduledFuture<?> future = taskScheduler.schedule(
                new StockSimulationJob(
                        this,
                        orderId
                ),
                new StockSimulationTrigger(
                        this,
                        marketService,
                        orderId,
                        WorkingHoursStatus.OPENED
                )
        );
        this.scheduledFutureMap.put(orderId, future);
    }

    private Double calculatePrice(final Double price, final Long contractSize) {
        return price * contractSize;
    }

    private Double calculateFee(final Double limitValue, final Double price) {
        return limitValue == null ?
                Math.min(0.14 * price, 7) : Math.min(0.24 * price, 12);
    }
}
