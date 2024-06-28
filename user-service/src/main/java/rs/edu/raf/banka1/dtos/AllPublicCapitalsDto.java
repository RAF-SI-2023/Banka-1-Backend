package rs.edu.raf.banka1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.edu.raf.banka1.model.ListingType;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AllPublicCapitalsDto {
    private Long listingId;
    private ListingType listingType;
    private String ticker;
    private String bankAccountNumber;
    private Double amount;
    private Instant lastModified;
    private String ownerName;
}
