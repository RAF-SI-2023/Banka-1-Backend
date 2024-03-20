package rs.edu.raf.banka1.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.edu.raf.banka1.model.enums.OptionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionsDto {
    private String ticker;
    private String optionType;
    private Double strikePrice;
    private String currency;
    private Double impliedVolatility;
    private Integer openInterest;
    private Long expirationDate;
}
