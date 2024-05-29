package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.error.Mark;
import rs.edu.raf.banka1.dtos.LegalOrderRequest;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;

@Component
public class OrderMapper {

    private final EmployeeMapper employeeMapper;

    public OrderMapper(final EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public MarketOrder requestToMarketOrder(CreateOrderRequest request, User owner) {
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
        //marketOrder.setOwner((Employee)owner);
        marketOrder.setCurrentAmount(0L);
        marketOrder.setTimestamp(System.currentTimeMillis()/1000);
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

    public CreateOrderRequest legalMarketOrderToCreateOrderRequest(LegalOrderRequest marketOrder) {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setListingId(marketOrder.getListingId());
        createOrderRequest.setListingType(marketOrder.getListingType());
        createOrderRequest.setOrderType(marketOrder.getOrderType());
        createOrderRequest.setContractSize(marketOrder.getContractSize());
        createOrderRequest.setLimitValue(marketOrder.getLimitValue());
        createOrderRequest.setStopValue(marketOrder.getStopValue());
        createOrderRequest.setAllOrNone(marketOrder.getAllOrNone());
        return createOrderRequest;
    }

}
