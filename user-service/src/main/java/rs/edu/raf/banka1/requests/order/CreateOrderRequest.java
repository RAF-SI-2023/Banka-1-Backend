package rs.edu.raf.banka1.requests.order;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.OrderType;

@Getter
@Setter
public class CreateOrderRequest {
    private OrderType orderType;
    private Long listingId;
    private ListingType listingType;
    private Long contractSize;
    private Double limitValue;
    private Double stopValue;
    private Boolean allOrNone;
    private Boolean isMargin;
}
