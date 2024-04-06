package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.repositories.EmployeeRepository;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;

@Component
public class OrderMapper {

    private final EmployeeMapper employeeMapper;

    public OrderMapper(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public MarketOrder requestToMarketOrder(CreateOrderRequest request) {
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setStockId(request.getStockId());
        // whoever makes controller for this, should first find in repository Employee with given id
//        marketOrder.set(request.getUserId());
        marketOrder.setOrderType(request.getOrderType());
        marketOrder.setStatus(OrderStatus.PROCESSING);
        marketOrder.setContractSize(request.getContractSize());
        marketOrder.setProcessedNumber(0L);
        marketOrder.setLimitValue(request.getLimitValue());
        marketOrder.setStopValue(request.getStopValue());
        marketOrder.setAllOrNone(request.getAllOrNone());
        return marketOrder;
    }

    public OrderDto marketOrderToOrderDto(MarketOrder marketOrder) {
        OrderDto orderDto = new OrderDto();
//        orderDto.setId(marketOrder.getId());
        orderDto.setStockId(marketOrder.getStockId());
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
