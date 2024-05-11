package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.ListingType;

@Getter
@Setter
public class ContractCreateDto {
    private Double amountToBuy;
    private Double offerPrice;
    private String bankAccountNumber;
    private Long listingId;
    private ListingType listingType;
    private String ticker;
}
