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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
public class OrderServiceImpl implements OrderService {
    private OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MarketService marketService;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static ScheduledExecutorService stopOrderExecutor = Executors.newScheduledThreadPool(1);
    private Random random = new Random();
    private Double PERCENT = 0.1;

    public OrderServiceImpl(
            final OrderMapper orderMapper,
            final OrderRepository orderRepository,
            final MarketService marketService,
            final UserRepository userRepository
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
        } else {
            marketOrder.setStatus(OrderStatus.PROCESSING);
        }
        marketOrder = orderRepository.save(marketOrder);
        return startOrder(marketOrder.getId());
    }

    private Double calculatePrice(final Double price, final Long contractSize) {
        return price * contractSize;
    }

    private Double calculateFee(final Double limitValue, final Double price) {
        return limitValue == null ?
            Math.min(0.14 * price, 7) : Math.min(0.24 * price, 12);
    }

    @Override
    public boolean startOrder(final Long orderId) {
        Optional<MarketOrder> optMarketOrder = orderRepository.findById(orderId);
        if (optMarketOrder.isEmpty()) return false;
        MarketOrder marketOrder = optMarketOrder.get();
        if(marketOrder.getStatus() != OrderStatus.APPROVED){
            executorService.schedule(() -> startOrder(orderId), 3, TimeUnit.MINUTES);
            return true;
        }
        WorkingHoursStatus workingHours = marketService.getWorkingHours(marketOrder.getStockId());

        if(workingHours==WorkingHoursStatus.CLOSED) {
            return false;
        }
        if (marketOrder.getStatus().equals(OrderStatus.DONE)) {
            return true;
        }
        final ListingBaseDto listingBaseDto = marketService.getStock(marketOrder.getStockId());

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
        final Long remainingQuantity = marketOrder.getContractSize() - marketOrder.getProcessedNumber();

        if(remainingQuantity == 0){
            return false;
        }

        long timeInterval = random.nextLong(24*60/(volume/remainingQuantity));
        timeInterval = workingHours.equals(WorkingHoursStatus.AFTER_HOURS) ? timeInterval + 30*60 : timeInterval;
        executorService.schedule(() -> startOrder(orderId), timeInterval, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public boolean changeStatus(final Long id, final OrderStatus orderStatus) {
        Optional<MarketOrder> optOrder = this.orderRepository.findById(id);
        if (optOrder.isEmpty()) return false;
        MarketOrder order = optOrder.get();
        order.setStatus(orderStatus);
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
    public boolean createLimitOrder(CreateOrderRequest request) {
        MarketOrder marketOrder = orderMapper.requestToMarketOrder(request);
        ListingBaseDto listingBaseDto = marketService.getStock(request.getStockId());

        marketOrder.setPrice(calculatePriceForLimitOrder(
                marketOrder.getOrderType(),
                marketOrder.getContractSize(),
                marketOrder.getLimitValue(),
                listingBaseDto.getPrice()));
        marketOrder.setFee(calculateFee(request.getLimitValue(), marketOrder.getPrice()));
        if (!orderRequiresApprove()) {
            marketOrder.setStatus(OrderStatus.APPROVED);
        } else {
            marketOrder.setStatus(OrderStatus.PROCESSING);
        }
        marketOrder = orderRepository.save(marketOrder);
        if (!startLimitOrder(marketOrder.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean startLimitOrder(Long orderId) {
        Optional<MarketOrder> optMarketOrder = orderRepository.findById(orderId);
        if (optMarketOrder.isEmpty()) return false;
        MarketOrder marketOrder = optMarketOrder.get();
//        MarketOrder marketOrder = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Cannot find limit order with id: " + orderId));
        if(marketOrder.getStatus() != OrderStatus.APPROVED){
            executorService.schedule(() -> startLimitOrder(orderId), 3, TimeUnit.MINUTES);
        }
        WorkingHoursStatus workingHours = marketService.getWorkingHours(marketOrder.getStockId());
        if(workingHours==WorkingHoursStatus.CLOSED) {
            return false;
        }
        if (marketOrder.getStatus().equals(OrderStatus.DONE)) {
            return true;
        }

        final ListingBaseDto listingBaseDto = marketService.getStock(marketOrder.getStockId());

        Double stockPrice = listingBaseDto.getPrice();
        Double change = random.nextDouble(stockPrice*PERCENT);
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
            marketOrder.setStatus(OrderStatus.DONE);
        }
        orderRepository.save(marketOrder);

        final Long volume = Long.valueOf(listingBaseDto.getVolume());
        Long remainingQuantity = marketOrder.getContractSize() - marketOrder.getProcessedNumber();

        if(remainingQuantity == 0){
            return false;
        }

        long timeInterval = random.nextLong(24*60/(volume/remainingQuantity));
        timeInterval = workingHours.equals(WorkingHoursStatus.AFTER_HOURS) ? timeInterval + 30*60 : timeInterval;
        executorService.schedule(() -> startLimitOrder(orderId), timeInterval, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public boolean createStopOrder(CreateOrderRequest stopOrderRequest) {
        MarketOrder marketOrder = orderMapper.requestToMarketOrder(stopOrderRequest);
        marketOrder = orderRepository.save(marketOrder);
        Long stockId = marketOrder.getStockId();
        Long marketOrderId = marketOrder.getId();
        stopOrderExecutor = Executors.newScheduledThreadPool(1);
        stopOrderExecutor.schedule(() -> checkConditionForStopOrderExecutor(marketOrderId, stockId), 0, TimeUnit.MINUTES);
        return true;
    }

    private void checkConditionForStopOrderExecutor(Long marketOrderId, Long stockId) {
        boolean conditionMet = checkStockPriceForStopOrder(marketOrderId, stockId);
        if (conditionMet) {
            executorService.schedule(() -> startOrder(marketOrderId), 0, TimeUnit.MINUTES);
        } else {
            stopOrderExecutor.schedule(() -> checkConditionForStopOrderExecutor(marketOrderId, stockId), 3, TimeUnit.MINUTES);
        }
    }

    @Override
    public Boolean checkStockPriceForStopOrder(Long marketOrderId, Long stockId) {
        Optional<MarketOrder> optMarketOrder = orderRepository.findById(marketOrderId);
        if (optMarketOrder.isEmpty()) return false;
        MarketOrder marketOrder = optMarketOrder.get();
//        MarketOrder marketOrder = orderRepository.findById(marketOrderId).orElseThrow(()-> new RuntimeException("Order not found"));
        ListingBaseDto listingBase = marketService.getStock(stockId);

        Double ask = listingBase.getHigh();
        Double bid = listingBase.getLow();

        Double changeAsk = random.nextDouble(ask*PERCENT);
        boolean plusAsk = random.nextBoolean();
        ask = plusAsk ? (ask + changeAsk) : (ask - changeAsk);
        Double changeBid = random.nextDouble(bid*PERCENT);
        boolean plusBid = random.nextBoolean();
        bid = plusBid ? (bid + changeBid) : (bid - changeBid);


        if(marketOrder.getOrderType().equals(OrderType.BUY)) {
            if(ask > marketOrder.getStopValue()) {
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
                if (!orderRequiresApprove()) {
                    marketOrder.setStatus(OrderStatus.APPROVED);
                } else {
                    marketOrder.setStatus(OrderStatus.PROCESSING);
                }
                orderRepository.save(marketOrder);
                return true;
            }
        } else { // SELL
            if(bid < marketOrder.getStopValue()) {
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
                if (!orderRequiresApprove()) {
                    marketOrder.setStatus(OrderStatus.APPROVED);
                } else {
                    marketOrder.setStatus(OrderStatus.PROCESSING);
                }
                orderRepository.save(marketOrder);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean createStopLimitOrder(CreateOrderRequest stopLimitOrderRequest) {
        MarketOrder marketOrder = orderMapper.requestToMarketOrder(stopLimitOrderRequest);
        marketOrder = orderRepository.save(marketOrder);
        Long stockId = marketOrder.getStockId();
        Long marketOrderId = marketOrder.getId();

        stopOrderExecutor = Executors.newScheduledThreadPool(1);
        stopOrderExecutor.scheduleWithFixedDelay(() -> {
            boolean conditionMet = checkStockPriceForStopOrder(marketOrderId, stockId);
            if (conditionMet) {
                startLimitOrder(marketOrderId);
                stopOrderExecutor.shutdown(); // Stop further executions
            }
        }, 0, 30, TimeUnit.SECONDS);
        return true;
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

        // Order must be in PROCESSING state to be DENIED / APPROVED
        if (order.getStatus() != null && !order.getStatus().equals(OrderStatus.PROCESSING)) return false;
        // Order processed before
        if (order.getStatus() != null && order.getStatus().equals(OrderStatus.DONE)) return false;

        order.setStatus(orderStatus);
        order.setApprovedBy(this.getLoggedUser());
        order.setLastModifiedDate(System.currentTimeMillis() / 1000);
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
        return Objects.equals(loggedUser.getUserId(), optOrderOwner.get().getUserId());
    }

    @Override
    public void resetUsersLimits() {
        if(LocalDate.now().getDayOfWeek().equals(DayOfWeek.SATURDAY) ||
            LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return;
        }
        List<User> users = userRepository.findAll();
        users.forEach(user->user.setLimitNow(0.0));
        userRepository.saveAll(users);
    }

    @Override
    public boolean resetLimitForUser(Long userId) {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) return false;
        User user = optUser.get();
//        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("Cannot find user with id: " + userId));
        user.setLimitNow(0.0);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean setLimitOrderForUser(Long userId, Double newOrderLimit) {
//        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("Cannot find user with id: " + userId));
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) return false;
        User user = optUser.get();
        user.setOrderlimit(newOrderLimit);
        userRepository.save(user);
        return true;
    }
}
