package rs.edu.raf.banka1.dtos.market_service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingStockDto extends ListingBaseDto {
    private Integer outstandingShares;
    private Double dividendYield;
    private Exchange exchange;
}