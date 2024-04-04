package rs.edu.raf.banka1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.ElementCollection;
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
    private Long ownerId;
    private Long createdByAgentId;
    private String accountNumber;
    private Double balance;
    private Double availableBalance;
    private Integer creationDate;
    private Integer expirationDate;
    private String currency;
    private String accountStatus;
    private String subtypeOfAccount;
    private Double accountMaintenance;
    private Boolean defaultCurrency;
}
