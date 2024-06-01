package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.model.DecideOrderResponse;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;

import java.time.Instant;
import java.util.List;

public interface OrderService {
    void createOrder(final CreateOrderRequest request, final Employee currentAuth);
    MarketOrder getOrderById(Long orderId);
    void finishOrder(Long orderId);
    void setProcessedNumber(Long orderId, Long processedNumber);
    List<MarketOrder> getInactiveOrders(Instant timeThreshold);
    void startOrderSimulation(Long orderId);
    ListingBaseDto getListingByOrder(MarketOrder order);
    Double calculatePrice(final MarketOrder order, final ListingBaseDto listingBaseDto, long processNum);
    List<OrderDto> getAllOrdersForEmployee(Employee currentAuth);
    List<OrderDto> getAllOrders();
    void cancelOrder(Long orderId);
    DecideOrderResponse decideOrder(Long orderId, String status, Employee currentAuth);
    void updateEmployeeLimit(Long orderId);
}
