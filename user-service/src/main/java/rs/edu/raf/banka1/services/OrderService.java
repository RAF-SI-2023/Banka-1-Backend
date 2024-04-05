package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;

public interface OrderService {
    void createOrder(final CreateOrderRequest request, final Employee currentAuth);
    MarketOrder getOrderById(Long orderId);
    void finishOrder(Long orderId);
    void setProcessedNumber(Long orderId, Long processedNumber);
}
