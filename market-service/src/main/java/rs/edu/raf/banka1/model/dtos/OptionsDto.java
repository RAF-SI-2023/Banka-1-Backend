package rs.edu.raf.banka1.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionsDto extends ListingBaseDto {
    private String optionType;
    private Double strikePrice;
    private String currency;
    private Double impliedVolatility;
    private Integer openInterest;
    private Long expirationDate;
}
