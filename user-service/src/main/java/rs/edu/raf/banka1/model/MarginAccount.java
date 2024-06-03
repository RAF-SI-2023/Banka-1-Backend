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

    @OneToOne
    @JoinColumn(name = "bank_account_id", referencedColumnName = "id")
    private BankAccount customer;

    @Enumerated(EnumType.STRING)
    private ListingType listingType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", referencedColumnName = "id")
    private Currency currency;

    private Double balance;

    private Double loanValue;

    private Double maintenanceMargin;

    private Boolean marginCallLevel = false;

    @Version
    private Integer version;
}
