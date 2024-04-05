package rs.edu.raf.banka1.services.implementations;


import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.ListingBaseDto;
import rs.edu.raf.banka1.exceptions.OrderNotFoundByIdException;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;
import rs.edu.raf.banka1.stocksimulation.StockSimulationJob;
import rs.edu.raf.banka1.stocksimulation.StockSimulationTrigger;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final MarketService marketService;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();
    private final TaskScheduler taskScheduler;

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
    }

    @Override
    public void createOrder(final CreateOrderRequest request) {
        final MarketOrder marketOrder = orderMapper.requestToMarketOrder(request);
        final ListingBaseDto listingBaseDto = marketService.getStock(request.getStockId());
        marketOrder.setPrice(calculatePrice(listingBaseDto.getPrice(), request.getContractSize()));
        marketOrder.setFee(calculateFee(request.getLimitValue(), marketOrder.getPrice()));
        // dok ne implementiramo approvovanje ordera
        marketOrder.setStatus(OrderStatus.APPROVED);
        orderRepository.save(marketOrder);

        //Start simulation
        taskScheduler.schedule(new StockSimulationJob(marketOrder.getId()), new StockSimulationTrigger());
    }

    @Override
    public MarketOrder getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundByIdException(orderId));
    }

    @Override
    public void finishOrder(Long orderId) {
        this.orderRepository.changeStatus(orderId, OrderStatus.DONE);
    }

    @Override
    public void setProcessedNumber(Long orderId, Long processedNumber) {
        this.orderRepository.changeProcessedNumber(orderId, processedNumber);
    }


    private Double calculatePrice(final Double price, final Long contractSize) {
        return price * contractSize;
    }

    private Double calculateFee(final Double limitValue, final Double price) {
        return limitValue == null ?
                Math.min(0.14 * price, 7) : Math.min(0.24 * price, 12);
    }
}
