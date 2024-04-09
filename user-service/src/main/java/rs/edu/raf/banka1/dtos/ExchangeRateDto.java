package rs.edu.raf.banka1.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto {
    private String baseCurrency;
    private String quoteCurrency;
    private double rate;
}
