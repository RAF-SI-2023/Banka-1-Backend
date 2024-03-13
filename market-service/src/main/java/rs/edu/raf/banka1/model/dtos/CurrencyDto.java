package rs.edu.raf.banka1.model.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDto {

    private String currencyName;

    private String currencyCode;
}
