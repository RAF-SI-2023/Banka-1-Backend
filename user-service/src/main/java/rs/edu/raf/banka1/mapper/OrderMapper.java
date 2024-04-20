package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;

@Component
public class OrderMapper {

    private final EmployeeMapper employeeMapper;

    public OrderMapper(final EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public MarketOrder requestToMarketOrder(CreateOrderRequest request, Employee owner) {
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setListingId(request.getListingId());
        marketOrder.setListingType(request.getListingType());
        marketOrder.setOrderType(request.getOrderType());
        marketOrder.setStatus(OrderStatus.PROCESSING);
        marketOrder.setContractSize(request.getContractSize());
        marketOrder.setProcessedNumber(0L);
        marketOrder.setLimitValue(request.getLimitValue() == 0.0 ? null : request.getLimitValue());
        marketOrder.setStopValue(request.getStopValue() == 0.0 ? null : request.getStopValue());
        marketOrder.setAllOrNone(request.getAllOrNone());
        marketOrder.setOwner(owner);
        return marketOrder;
    }

    public OrderDto marketOrderToOrderDto(MarketOrder marketOrder) {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(marketOrder.getId());
//        orderDto.setId(marketOrder.getId());
        orderDto.setListingId(marketOrder.getListingId());
        orderDto.setListingType(marketOrder.getListingType());
        orderDto.setOwner(employeeMapper.employeeToEmployeeDto(marketOrder.getOwner()));
        orderDto.setOrderType(marketOrder.getOrderType());
        orderDto.setStatus(marketOrder.getStatus());
        orderDto.setContractSize(marketOrder.getContractSize());
        orderDto.setProcessedNumber(marketOrder.getProcessedNumber());
        orderDto.setLimitValue(marketOrder.getLimitValue());
        orderDto.setStopValue(marketOrder.getStopValue());
        orderDto.setFee(marketOrder.getFee());
        orderDto.setPrice(marketOrder.getPrice());
        orderDto.setAllOrNone(marketOrder.getAllOrNone());
        orderDto.setUpdatedAt(marketOrder.getUpdatedAt().getEpochSecond());
        orderDto.setApprovedBy(employeeMapper.employeeToEmployeeDto(marketOrder.getApprovedBy()));
        return orderDto;
    }


}
