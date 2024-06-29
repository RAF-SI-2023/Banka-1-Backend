package rs.edu.raf.banka1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.ListingType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarginAccountCreateDto {
    private ListingType listingType;
    private Currency currency;
    private Long customerId;
    private Long companyId;
}
