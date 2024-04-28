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

    @ManyToOne
    private BankAccount bankAccount;

    private ListingType listingType;

    private Long listingId;

    private String ticker;

    private Integer publicTotal = 0;

    private Double total;

    private Double reserved;
}
