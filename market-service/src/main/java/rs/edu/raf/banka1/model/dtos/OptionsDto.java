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
    private OptionType optionType;
    private double strikePrice;
    private String currency;
    private double impliedVolatility;
    private int openInterest;
    private long expirationDate;
}
