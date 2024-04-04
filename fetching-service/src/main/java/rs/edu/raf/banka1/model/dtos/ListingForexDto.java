package rs.edu.raf.banka1.model.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingForexDto extends ListingBaseDto {
    private String baseCurrency;
    private String quoteCurrency;
}
