package rs.edu.raf.banka1.services.implementations;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.ListingBaseDto;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final MarketService marketService;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();

    public OrderServiceImpl(
        final OrderMapper orderMapper,
        final OrderRepository orderRepository,
        final MarketService marketService
    ) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.marketService = marketService;
    }

    @Override
    public void createOrder(final CreateOrderRequest request) {
        MarketOrder marketOrder = orderMapper.requestToMarketOrder(request);
        final ListingBaseDto listingBaseDto = marketService.getStock(request.getStockId());
        marketOrder.setPrice(calculatePrice(listingBaseDto.getPrice(), request.getContractSize()));
        marketOrder.setFee(calculateFee(request.getLimitValue(), marketOrder.getPrice()));

        if (!orderRequiresApprove()) {
            marketOrder.setStatus(OrderStatus.APPROVED);
            marketOrder.setDone(true);
        } else {
            marketOrder.setStatus(OrderStatus.PROCESSING);
            marketOrder.setDone(false);
        }
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

//        if(workingHours==WorkingHoursStatus.CLOSED || marketOrder.getStatus().equals(OrderStatus.DONE))
//            return;
        if(workingHours==WorkingHoursStatus.CLOSED || marketOrder.getDone())
            return;

        final ListingBaseDto listingBaseDto = marketService.getStock(marketOrder.getStockId());


        if(marketOrder.getAllOrNone()){
            marketOrder.setProcessedNumber(marketOrder.getContractSize());
        }else{
            Long processedNumber = random.nextLong(marketOrder.getContractSize() - marketOrder.getProcessedNumber()) + 1;
            marketOrder.setProcessedNumber(marketOrder.getProcessedNumber() + processedNumber);
        }

        if(marketOrder.getContractSize().equals(marketOrder.getProcessedNumber())) {
//            marketOrder.setStatus(OrderStatus.DONE);
            marketOrder.setDone(true);
        }
        orderRepository.save(marketOrder);

        final Long volume = Long.valueOf(listingBaseDto.getVolume());
        Long remainingQuantity = marketOrder.getContractSize() - marketOrder.getProcessedNumber();

        long timeInterval = random.nextLong(24*60/(volume/remainingQuantity));
        timeInterval = workingHours.equals(WorkingHoursStatus.AFTER_HOURS) ? timeInterval + 30*60 : timeInterval;
        executorService.schedule(() -> startOrder(orderId), timeInterval, TimeUnit.MINUTES);
    }

    @Override
    public boolean changeStatus(final Long id, final OrderStatus orderStatus) {
        Optional<MarketOrder> optOrder = this.orderRepository.findById(id);
        if (optOrder.isEmpty()) return false;
        MarketOrder order = optOrder.get();
        order.setStatus(orderStatus);
        order.setDone(true); // Status changed
        order.setLastModifiedDate(System.currentTimeMillis() / 1000);
        this.orderRepository.save(order);
        return true;
    }

    private User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Assuming your UserDetails implementation has the email field
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            //return this.agentRepository.findByEmail(email);
            //TODO waiting for agent implementation
        }

        return null;
    }

    private boolean orderRequiresApprove() {
        User loggedUser = this.getLoggedUser();
        if (loggedUser instanceof Agent) {
            Agent agent = (Agent) loggedUser;
            if (agent.getRequireApproval()) { // Agent requires every approval
                return true;
            }
            if (agent.getLimitNow() >= agent.getOrderlimit()) { // Limit exceeded
                return true;
            }
            if (agent.getLimitNow() == 0) { // Agent wasted his given limit
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean approveOrder(Long id) {
        Optional<MarketOrder> optOrder = this.orderRepository.findById(id);
        if (optOrder.isEmpty()) return false;
        MarketOrder order = optOrder.get();
        order.setStatus(OrderStatus.APPROVED);
        order.setApprovedBy(this.getLoggedUser());
        order.setLastModifiedDate(System.currentTimeMillis() / 1000);
        order.setDone(true);
        this.orderRepository.save(order);
        return true;
    }

    @Override
    public boolean settlementDateExpired(Long id) {
        Optional<MarketOrder> optOrder = this.orderRepository.findById(id);
        if (optOrder.isEmpty()) return false;
        MarketOrder order = optOrder.get();
        // TODO Kako se vezuju ListingFuture i Order??
        return false;
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
