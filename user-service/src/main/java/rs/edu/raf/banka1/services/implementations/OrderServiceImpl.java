package rs.edu.raf.banka1.services.implementations;

import rs.edu.raf.banka1.dtos.ListingBaseDto;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final MarketService marketService;
    private final ScheduledExecutorService executorService;

    public OrderServiceImpl(
        final OrderMapper orderMapper,
        final OrderRepository orderRepository,
        final MarketService marketService,
        final ScheduledExecutorService executorService
    ) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.marketService = marketService;
        this.executorService = executorService;
    }

    @Override
    public void createOrder(final CreateOrderRequest request) {
        MarketOrder marketOrder = orderMapper.requestToMarketOrder(request);
        final ListingBaseDto listingBaseDto = marketService.getStock(request.getStockId());
        marketOrder.setPrice(calculatePrice(listingBaseDto.getPrice(), request.getContractSize()));
        marketOrder.setFee(calculateFee(request.getLimitValue(),marketOrder.getPrice()));
        // dok ne implementiramo approvovanje ordera
        marketOrder.setStatus(OrderStatus.APPROVED);
        marketOrder = orderRepository.save(marketOrder);
        startOrder(marketOrder.getId());
    }

    private Double calculatePrice(final Double price, final Long contractSize) {
        return price * contractSize;
    }

    private Double calculateFee(final Double limitValue, final Double price) {
        return limitValue == null ?
            Math.min(0.14 * price, 7) : Math.min(0.24 * price, 12);
    }

    @Override
    public void startOrder(final Long orderId) {
        MarketOrder marketOrder = orderRepository.getReferenceById(orderId);
        if(marketOrder.getStatus() != OrderStatus.APPROVED){
            executorService.schedule(() -> startOrder(orderId), 3, TimeUnit.MINUTES);
        }
        WorkingHoursStatus workingHours = marketService.getWorkingHours(marketOrder.getStockId());

        if(workingHours==WorkingHoursStatus.CLOSED || marketOrder.getStatus().equals(OrderStatus.DONE))
            return;

        final ListingBaseDto listingBaseDto = marketService.getStock(marketOrder.getStockId());
        Random random = new Random();

        if(marketOrder.getAllOrNone()){
            marketOrder.setProcessedNumber(marketOrder.getContractSize());
        }else{
            Long processedNumber = random.nextLong(marketOrder.getContractSize() - marketOrder.getProcessedNumber()) + 1;
            marketOrder.setProcessedNumber(marketOrder.getProcessedNumber() + processedNumber);
        }

        if(marketOrder.getContractSize().equals(marketOrder.getProcessedNumber())) {
            marketOrder.setStatus(OrderStatus.DONE);
        }
        orderRepository.save(marketOrder);

        final Long volume = Long.valueOf(listingBaseDto.getVolume());
        Long remainingQuantity = marketOrder.getContractSize() - marketOrder.getProcessedNumber();

        long timeInterval = random.nextLong(24*60/(volume/remainingQuantity));
        timeInterval = workingHours.equals(WorkingHoursStatus.AFTER_HOURS) ? timeInterval + 30*60 : timeInterval;
        executorService.schedule(() -> startOrder(orderId), timeInterval, TimeUnit.MINUTES);
    }

//    private void processOrder(
//        final Long orderId,
//        final WorkingHoursStatus workingHours
//    ){
//        if(workingHours==WorkingHoursStatus.CLOSED)
//            return;
//        MarketOrder marketOrder = orderRepository.getReferenceById(orderId);
//
//        if(marketOrder.getAllOrNone()){
//            marketOrder.setProcessedNumber(marketOrder.getContractSize());
//            marketOrder.setStatus(OrderStatus.DONE);
//            orderRepository.save(marketOrder);
//            return;
//        }
//
//        final ListingBaseDto listingBaseDto = marketService.getStock(marketOrder.getStockId());
//
//
//        Random random = new Random();
//        Long processedNumber = random.nextLong(marketOrder.getContractSize() - marketOrder.getProcessedNumber()) + 1;
//        marketOrder.setProcessedNumber(marketOrder.getProcessedNumber() + processedNumber);
//        if(marketOrder.getContractSize().equals(marketOrder.getProcessedNumber())) {
//            marketOrder.setStatus(OrderStatus.DONE);
//            orderRepository.save(marketOrder);
//            return;
//        }
//        orderRepository.save(marketOrder);
//
//        final Long volume = Long.valueOf(listingBaseDto.getVolume());
//        Long remainingQuantity = marketOrder.getContractSize() - marketOrder.getProcessedNumber();
//
//        long timeInterval = random.nextLong(24*60/(volume/remainingQuantity));
//        timeInterval = workingHours.equals(WorkingHoursStatus.AFTER_HOURS) ? timeInterval + 30*60 : timeInterval;
//
//        executorService.schedule(() -> processOrder(orderId, workingHours), timeInterval, TimeUnit.SECONDS);
//    }
}
