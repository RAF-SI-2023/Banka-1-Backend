package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractCreateDto {
    private Double amountToBuy;
    private Double offerPrice;
    private Long sellerId;
}
