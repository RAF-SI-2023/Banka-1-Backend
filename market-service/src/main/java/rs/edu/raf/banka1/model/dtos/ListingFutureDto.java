package rs.edu.raf.banka1.model.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ListingFutureDto extends ListingBaseDto {
    private Integer contractSize;
    private String contractUnit;
    private Integer openInterest;
    private Integer settlementDate;
    private Double lastPrice;
}
