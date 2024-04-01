package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;

@Component
public class OrderMapper {

    public MarketOrder requestToMarketOrder(CreateOrderRequest request) {
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setStockId(request.getStockId());
        marketOrder.setAgentId(request.getUserId());
        marketOrder.setOrderType(request.getOrderType());
        marketOrder.setStatus(OrderStatus.PROCESSING);
        marketOrder.setContractSize(request.getContractSize());
        marketOrder.setProcessedNumber(0L);
        marketOrder.setLimitValue(request.getLimitValue());
        marketOrder.setStopValue(request.getStopValue());
        marketOrder.setAllOrNone(request.getAllOrNone());
        return marketOrder;
    }


}
