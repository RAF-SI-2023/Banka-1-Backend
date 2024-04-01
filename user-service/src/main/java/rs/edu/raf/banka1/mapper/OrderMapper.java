package rs.edu.raf.banka1.mapper;

import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;

public class OrderMapper {

    public MarketOrder requestToMarketOrder(CreateOrderRequest request) {
        MarketOrder order = new MarketOrder();
        order.setStockId(request.getStockId());
        order.setOrderType(request.getOrderType());
        order.setStatus(OrderStatus.PROCESSING);
        order.setContractSize(request.getContractSize());
        order.setProcessedNumber(0L);
        order.setLimitValue(request.getLimitValue());
        order.setStopValue(request.getStopValue());
        order.setAllOrNone(request.getAllOrNone());
        return order;
    }


}
