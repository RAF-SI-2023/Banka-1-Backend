package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "foreign_currency_accounts", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class ForeignCurrencyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
    private Long ownerId;
    private Long createdByAgentId;
    private String accountNumber;
    private String typeOfAccount;
    private Double balance;
    private Double availableBalance;
    private Integer creationDate;
    private Integer expirationDate;
    private String currency;
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    @Enumerated(EnumType.STRING)
    private SubtypeOfAccount subtypeOfAccount;
    private Double accountMaintenance;
    private Boolean defaultCurrency;
    @ElementCollection
    private List<String> allowedCurrencies;

}
