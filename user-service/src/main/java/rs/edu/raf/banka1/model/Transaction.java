package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private BankAccount bankAccount;

    private Long dateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    private MarketOrder marketOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    private Employee employee;

    // make some kind of description builder function, so that descriptions are consistent
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    private Currency currency;

    // decreases amount of money on bank account
    private Double buy;

    // increases amount of money on bank account
    private Double sell;

    private Double reserved;

    private Double reserveUsed;

}
