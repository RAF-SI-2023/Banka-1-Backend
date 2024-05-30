package rs.edu.raf.banka1.dtos.market_service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionsDto extends ListingBaseDto{
    private String optionType;
    private Double strikePrice;
    private String currency;
    private Double impliedVolatility;
    private Integer openInterest;
    private Long expirationDate;
}
