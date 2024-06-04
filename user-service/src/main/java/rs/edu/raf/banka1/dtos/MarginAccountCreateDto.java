package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.ListingType;

@Getter
@Setter
public class MarginAccountCreateDto {
    private ListingType listingType;
    private Currency currency;
    private Double balance;
    private Double maintenanceMargin;
    private Long customerId;
    private Long companyId;
}
