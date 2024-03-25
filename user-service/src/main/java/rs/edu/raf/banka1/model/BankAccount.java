package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private String accountNumber;


    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "userId")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @Column
    private Double balance;

    @Column
    private Double availableBalance;

    @Column
    private Long createdByAgentId;

    @Column
    private Long creationDate;

    @Column
    private Long expirationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", referencedColumnName = "id")
    private Currency currency;

    @Column
    private String accountStatus; //string because of frontend

    //    Only for current_account and foreign_currency_account
    @Column
    private String subtypeOfAccount;

    @Column
    private Double accountMaintenance;
}