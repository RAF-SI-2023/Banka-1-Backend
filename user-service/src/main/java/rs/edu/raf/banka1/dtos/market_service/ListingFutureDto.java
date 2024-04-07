package rs.edu.raf.banka1.dtos.market_service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingFutureDto extends ListingBaseDto {
    private Integer contractSize;
    private String contractUnit;
    private Integer openInterest;
    private Integer settlementDate;
    private Double lastPrice;
    private String alternativeTicker;
}

