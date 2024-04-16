package rs.edu.raf.banka1.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.ListingType;
@Getter
@Setter
@EqualsAndHashCode
public class CapitalDto {
    private String bankAccountNumber;
    private String currencyName;
    private ListingType listingType;
    private Long listingId;
    private Double total;
    private Double reserved;
}
