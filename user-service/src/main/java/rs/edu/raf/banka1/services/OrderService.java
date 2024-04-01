package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;

public interface OrderService {
    void createOrder(final CreateOrderRequest request);
    void startOrder(final Long orderId);

    void changeStatus(final Long id, final OrderStatus orderStatus);
}
