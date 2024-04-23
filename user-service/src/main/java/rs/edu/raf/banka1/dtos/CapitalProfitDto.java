package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CapitalProfitDto extends CapitalDto{
    private Double totalPrice;
    private String ticker;
}
