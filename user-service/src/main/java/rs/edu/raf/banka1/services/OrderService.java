package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;

public interface OrderService {
    boolean createOrder(final CreateOrderRequest request);
    boolean startOrder(final Long orderId);

    boolean startLimitOrder(Long orderId);
    boolean createLimitOrder(CreateOrderRequest limitOrderRequest);

    boolean createStopOrder(CreateOrderRequest stopOrderRequest);
    Boolean checkStockPriceForStopOrder(Long marketOrderId, Long stockId);

    boolean createStopLimitOrder(CreateOrderRequest stopLimitOrderRequest);

    boolean changeStatus(Long id, OrderStatus orderStatus);

    boolean decideOrder(Long id, OrderStatus orderStatus);

    boolean checkOrderOwner(Long id);

    void resetUsersLimits();

    boolean resetLimitForUser(Long userId);

    boolean setLimitOrderForUser(Long userId, Double newOrderLimit);
}
