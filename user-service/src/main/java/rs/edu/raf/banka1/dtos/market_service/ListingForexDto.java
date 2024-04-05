package rs.edu.raf.banka1.dtos.market_service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingForexDto extends ListingBaseDto {
    private String baseCurrency;
    private String quoteCurrency;
}
