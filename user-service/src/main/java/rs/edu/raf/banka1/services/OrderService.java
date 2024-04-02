package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;

public interface OrderService {
    boolean createOrder(final CreateOrderRequest request);
    void startOrder(final Long orderId);

    boolean changeStatus(final Long id, final OrderStatus orderStatus);

    boolean approveOrder(Long id);

    boolean settlementDateExpired(Long id);
}
