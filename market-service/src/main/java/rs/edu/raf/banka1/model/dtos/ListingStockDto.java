package rs.edu.raf.banka1.model.dtos;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.entities.Exchange;

@Getter
@Setter
public class ListingStockDto extends ListingBaseDto {
    private Integer outstandingShares;
    private Double dividendYield;
    private Exchange exchange;

}
