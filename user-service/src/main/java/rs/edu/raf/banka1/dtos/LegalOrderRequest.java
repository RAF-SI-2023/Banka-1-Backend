package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.model.OrderType;

@Getter
@Setter
@ToString
public class LegalOrderRequest {
    private OrderType orderType;
    private Long listingId;
    private ListingType listingType;
    private Long contractSize;
    private Double limitValue;
    private Double stopValue;
    private Boolean allOrNone;
    private Boolean isMargin;
}
