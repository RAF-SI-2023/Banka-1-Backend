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

    @ManyToOne()
    private BankAccount bankAccount;

    private Long dateTime;

    @ManyToOne()
    private MarketOrder marketOrder;

    @ManyToOne()
    private Employee employee;

    // make some kind of description builder function, so that descriptions are consistent
    private String description;

    @ManyToOne()
    private Currency currency;

    // decreases amount of money on bank account
    private Double buy;

    // increases amount of money on bank account
    private Double sell;

    private Double reserved;

    private Double reserveUsed;

}
