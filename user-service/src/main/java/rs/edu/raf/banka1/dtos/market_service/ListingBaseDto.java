package rs.edu.raf.banka1.dtos.market_service;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.ListingType;

@Getter
@Setter
public class ListingBaseDto {
    private Long listingId;
    private ListingType listingType;
    private String ticker;
    private String name;
    private String exchangeName;
    private Integer lastRefresh;
    private Double price;
    private Double high;
    private Double low;
    private Double priceChange;
    private Integer volume;

}