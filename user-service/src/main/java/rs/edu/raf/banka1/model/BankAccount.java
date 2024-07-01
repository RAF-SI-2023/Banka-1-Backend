package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private AccountType accountType;

    private String accountNumber;
    private String accountName;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "userId")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    private Double balance;
    private Double availableBalance;
    private Long createdByAgentId;
    private Long creationDate;
    private Long expirationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", referencedColumnName = "id")
    private Currency currency;

    private Boolean accountStatus;

    @OneToMany(mappedBy = "senderBankAccount")
    private List<Payment> payments;
    @OneToMany(mappedBy = "senderBankAccount")
    private List<Transfer> transfers;

//    Only for current_account and foreign_currency_account
    private String subtypeOfAccount;
    private Double maintenanceCost;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "bankAccount")
    private List<Card> cards = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
