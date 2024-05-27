package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.ListingType;

@Setter
@Getter
public class AddPublicCapitalDto {
    private ListingType listingType;
    private Long listingId;
    private Double addToPublic;
}
