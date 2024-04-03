package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;

public interface OrderService {
    boolean createOrder(final CreateOrderRequest request);
    void startOrder(final Long orderId);

    void startLimitOrder(Long orderId);
    void createLimitOrder(CreateOrderRequest limitOrderRequest);

    void createStopOrder(CreateOrderRequest stopOrderRequest);
    Boolean checkStockPriceForStopOrder(Long marketOrderId, Long stockId);

    void createStopLimitOrder(CreateOrderRequest stopLimitOrderRequest);

    boolean changeStatus(Long id, OrderStatus orderStatus);

    boolean decideOrder(Long id, OrderStatus orderStatus);

    boolean checkOrderOwner(Long id);

    void resetUsersLimits();

    void resetLimitForUser(Long userId);

    void setLimitOrderForUser(Long userId, Double newOrderLimit);
}
