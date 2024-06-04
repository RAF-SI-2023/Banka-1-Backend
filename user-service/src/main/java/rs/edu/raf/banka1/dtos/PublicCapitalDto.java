package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.ListingType;

@Getter
@Setter
public class PublicCapitalDto {
    private Double publicTotal;
    private Boolean isIndividual;
    private String bankAccountNumber;
    private ListingType listingType;
    private Long listingId;
}
