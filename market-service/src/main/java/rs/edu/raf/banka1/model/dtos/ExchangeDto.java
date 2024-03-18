package rs.edu.raf.banka1.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeDto {
    private Long exchangeId;
    private String exchangeName;
    private String exchangeAcronym;
    private String exchangeMicCode;
    private String country;
    private String currency;
    private int timeZone;
}
