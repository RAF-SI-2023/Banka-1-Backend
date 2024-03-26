package rs.edu.raf.banka1.model.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingStockDto extends ListingBaseDto{
    private Integer outstandingShares;
    private Double dividendYield;

}
