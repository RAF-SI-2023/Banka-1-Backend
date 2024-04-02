package rs.edu.raf.banka1.services.implementations;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.ListingBaseDto;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.OrderService;
import rs.edu.raf.banka1.utils.Constants;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MarketService marketService;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static ScheduledExecutorService stopOrderExecutor = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();

    public OrderServiceImpl(
            OrderMapper orderMapper,
            OrderRepository orderRepository,
            MarketService marketService,
            UserRepository userRepository
    ) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.marketService = marketService;
        this.userRepository = userRepository;
    }

    @Override
    public boolean createOrder(final CreateOrderRequest request) {
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
        return true;
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

    private Double calculatePriceForLimitOrder(OrderType orderType, Long contractSize, Double limitValue, Double stockPrice) {
        if(orderType.equals(OrderType.BUY)) {
            return contractSize * Math.min(stockPrice, limitValue); // high(ask) umesto stockPrice
        } else {
            return contractSize * Math.max(stockPrice, limitValue); // low(bid) umesto stockPrice
        }
    }
    @Override
    public void createLimitOrder(CreateOrderRequest request) {
        MarketOrder marketOrder = orderMapper.requestToMarketOrder(request);
        ListingBaseDto listingBaseDto = marketService.getStock(request.getStockId());
//    /** Ovo je provera - za test jer getStock vraca null trenutno !!! **/
//        ListingBaseDto listingBaseDto = new ListingBaseDto();
//        listingBaseDto.setPrice(99.99);
//        listingBaseDto.setHigh(101.01);
//        listingBaseDto.setLow(98.98);
//        listingBaseDto.setVolume(10);

        marketOrder.setPrice(calculatePriceForLimitOrder(
                marketOrder.getOrderType(),
                marketOrder.getContractSize(),
                marketOrder.getLimitValue(),
                listingBaseDto.getPrice()));
        marketOrder.setFee(calculateFee(request.getLimitValue(), marketOrder.getPrice()));
        // dok ne implementiramo approvovanje ordera
        marketOrder.setStatus(OrderStatus.APPROVED);
        marketOrder.setDone(false);
        marketOrder = orderRepository.save(marketOrder);
        startLimitOrder(marketOrder.getId());
    }

    @Override
    public void startLimitOrder(Long orderId) {
        System.out.println("Start Limit Order: ");
        MarketOrder marketOrder = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Cannot find limit order with id: " + orderId));
        if(marketOrder.getStatus() != OrderStatus.APPROVED){
            executorService.schedule(() -> startLimitOrder(orderId), 3, TimeUnit.MINUTES);
        }
        WorkingHoursStatus workingHours = marketService.getWorkingHours(marketOrder.getStockId());
//        WorkingHoursStatus workingHours = WorkingHoursStatus.OPENED;
        if(workingHours==WorkingHoursStatus.CLOSED || marketOrder.getDone())
            return;

        final ListingBaseDto listingBaseDto = marketService.getStock(marketOrder.getStockId());
//    /** Ovo je provera - za test jer getStock vraca null trenutno !!! **/
//        ListingBaseDto listingBaseDto = new ListingBaseDto();
//        listingBaseDto.setPrice(99.99);
//        listingBaseDto.setHigh(101.01);
//        listingBaseDto.setLow(98.98);
//        listingBaseDto.setVolume(10);


        Double stockPrice = listingBaseDto.getPrice();
        Double change = random.nextDouble(stockPrice*0.1);
        boolean plus = random.nextBoolean();
        stockPrice = plus ? (stockPrice + change) : (stockPrice - change);

        boolean processOrder = false;
        if(marketOrder.getOrderType().equals(OrderType.BUY)) {
            if(stockPrice < marketOrder.getLimitValue()) {
                processOrder = true;
            }
        } else { // SELL
            if(stockPrice > marketOrder.getLimitValue()) {
                processOrder = true;
            }
        }

        if(processOrder) {
            if(marketOrder.getAllOrNone()){
                marketOrder.setProcessedNumber(marketOrder.getContractSize());
            }else{
                Long processedNumber = random.nextLong(marketOrder.getContractSize() - marketOrder.getProcessedNumber()) + 1;
                marketOrder.setProcessedNumber(marketOrder.getProcessedNumber() + processedNumber);
                marketOrder.setPrice(calculatePriceForLimitOrder(
                        marketOrder.getOrderType(),
                        marketOrder.getContractSize(),
                        marketOrder.getLimitValue(),
                        stockPrice));
            }
        }

        if(marketOrder.getContractSize().equals(marketOrder.getProcessedNumber())) {
            marketOrder.setDone(true);
        }
        orderRepository.save(marketOrder);

        final Long volume = Long.valueOf(listingBaseDto.getVolume());
        Long remainingQuantity = marketOrder.getContractSize() - marketOrder.getProcessedNumber();

        if(remainingQuantity == 0){
            return;
        }

        long timeInterval = random.nextLong(24*60/(volume/remainingQuantity));
        timeInterval = workingHours.equals(WorkingHoursStatus.AFTER_HOURS) ? timeInterval + 30*60 : timeInterval;
        executorService.schedule(() -> startLimitOrder(orderId), timeInterval, TimeUnit.MINUTES);
    }

    @Override
    public void createStopOrder(CreateOrderRequest stopOrderRequest) {
        MarketOrder marketOrder = orderMapper.requestToMarketOrder(stopOrderRequest);
        marketOrder = orderRepository.save(marketOrder);
        Long stockId = marketOrder.getStockId();
        Long marketOrderId = marketOrder.getId();

        stopOrderExecutor = Executors.newScheduledThreadPool(1);
        stopOrderExecutor.scheduleWithFixedDelay(() -> {
            System.out.println("STOP ORDER EXECUTOR");
            boolean conditionMet = checkStockPriceForStopOrder(marketOrderId, stockId);
            if (conditionMet) {
                // popraviti ovu startOrder Funkciju da radi
                startOrder(marketOrderId);
                stopOrderExecutor.shutdown(); // Stop further executions
            }
        }, 0, 30, TimeUnit.SECONDS);

    }

    private Boolean checkStockPriceForStopOrder(Long marketOrderId, Long stockId) {
        MarketOrder marketOrder = orderRepository.findById(marketOrderId).orElseThrow(()-> new RuntimeException("Order not found"));
        ListingBaseDto listingBase = new ListingBaseDto();
        listingBase.setPrice(99.99);
        listingBase.setHigh(99.99);
        listingBase.setLow(98.98);
        listingBase.setVolume(10);

        Double ask = listingBase.getHigh();
        Double bid = listingBase.getLow();

        Double changeAsk = random.nextDouble(ask*0.1);
        boolean plusAsk = random.nextBoolean();
        ask = plusAsk ? (ask + changeAsk) : (ask - changeAsk);
        Double changeBid = random.nextDouble(bid*0.1);
        boolean plusBid = random.nextBoolean();
        bid = plusBid ? (bid + changeBid) : (bid - changeBid);


        if(marketOrder.getOrderType().equals(OrderType.BUY)) {
            if(ask > marketOrder.getStopValue()) {
                marketOrder.setPrice(calculatePrice(ask, marketOrder.getContractSize()));
                marketOrder.setFee(calculateFee(marketOrder.getLimitValue(), marketOrder.getPrice()));
                marketOrder.setStatus(OrderStatus.APPROVED);
                orderRepository.save(marketOrder);
                return true;
            }
        } else { // SELL
            if(bid < marketOrder.getStopValue()) {
                marketOrder.setPrice(calculatePrice(bid, marketOrder.getContractSize()));
                marketOrder.setFee(calculateFee(marketOrder.getLimitValue(), marketOrder.getPrice()));
                marketOrder.setStatus(OrderStatus.APPROVED);
                orderRepository.save(marketOrder);
                return true;
            }
        }
        return false;
    }
    private User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if the user is authenticated
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Assuming your UserDetails implementation has the email field
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Optional<User> optUser = this.userRepository.findByEmail(email);
            if (optUser.isPresent()) {
                return optUser.get();
            }
        }
        return null;
    }

    private boolean orderRequiresApprove() {
        User loggedUser = this.getLoggedUser();
        if (loggedUser == null) return false;
        if (loggedUser.getPosition().equalsIgnoreCase(Constants.AGENT)) {
            if (loggedUser.getRequireApproval()) { // Agent requires every approval
                return true;
            }
            if (loggedUser.getLimitNow() >= loggedUser.getOrderlimit()) { // Limit exceeded
                return true;
            }
            if (loggedUser.getLimitNow() == 0) { // Agent wasted his given limit
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean decideOrder(Long id, OrderStatus orderStatus) {
        Optional<MarketOrder> optOrder = this.orderRepository.findById(id);
        if (optOrder.isEmpty()) return false;
        MarketOrder order = optOrder.get();
        order.setStatus(orderStatus);
        order.setApprovedBy(this.getLoggedUser());
        order.setLastModifiedDate(System.currentTimeMillis() / 1000);
        order.setDone(true);
        this.orderRepository.save(order);
        return true;
    }

    @Override
    public boolean checkOrderOwner(Long id) {
        Optional<MarketOrder> optOrder = this.orderRepository.findById(id);
        if (optOrder.isEmpty()) return false;
        MarketOrder order = optOrder.get();

        // Check order owner
        User loggedUser = this.getLoggedUser();
        if (loggedUser == null) return false;
        Optional<User> optOrderOwner = this.userRepository.findById(order.getOwnerId());
        if (optOrderOwner.isEmpty()) return false;
        // Check if logged user and order owner are the same person
        return loggedUser.getUserId().equals(optOrderOwner.get().getUserId());
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
