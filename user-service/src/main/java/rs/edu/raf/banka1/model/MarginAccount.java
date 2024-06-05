package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MarginAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bank_account_id", referencedColumnName = "id")
    private BankAccount customer;

    @Enumerated(EnumType.STRING)
    private ListingType listingType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", referencedColumnName = "id")
    private Currency currency;

    private Double balance = 0d;

    private Double loanValue = 0d;

    private Double maintenanceMargin = 0d;

    private int marginCallLevel = 0; //0 -> margin call not triggered, 1 -> margin call triggered, 2 -> margin call time exceeded - automatic liquidation

    @Version
    private Integer version;
}
