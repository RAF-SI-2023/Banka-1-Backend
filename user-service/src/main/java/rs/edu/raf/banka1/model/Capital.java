package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Capital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // if we use BankAccount and Currency, then Listing and listingType are null
    @OneToOne
    private BankAccount bankAccount;

    @OneToOne
    private Currency currency;

    private ListingType listingType;

    // if we use Listng and listingType, then BankAccount and Currency are null
    private Long listingId;

    private Double total;

    private Double reserved;

    private String ticker;
}
