package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DevizniRacun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String accountNumber;
    private String ownerId;
    private VrstaRacuna vrstaRacuna;
    private Double balance;
    private Double availableBalance;
    private Long createdByAgentId;
    private String creationDate;
    private String expirationDate;
    private String currency;
    private StatusRacuna statusRacuna;
    private PodvrstaRacuna podvrstaRacuna;
    private Integer interestRatePercentage;
    private Double accountMaintenance;
    private Boolean defaultCurrency;
    @ElementCollection
    private List<String> allowedCurrencies;

    public DevizniRacun(String id, String accountNumber, String ownerId, Double balance, Double availableBalance, Long createdByAgentId, String creationDate, String expirationDate, String currency, StatusRacuna statusRacuna, PodvrstaRacuna podvrstaRacuna, Integer interestRatePercentage, Double accountMaintenance, Boolean defaultCurrency, List<String> allowedCurrencies) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.ownerId = ownerId;
        this.vrstaRacuna = VrstaRacuna.DEVIZNI;
        this.balance = balance;
        this.availableBalance = availableBalance;
        this.createdByAgentId = createdByAgentId;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.currency = currency;
        this.statusRacuna = statusRacuna;
        this.podvrstaRacuna = podvrstaRacuna;
        this.interestRatePercentage = interestRatePercentage;
        this.accountMaintenance = accountMaintenance;
        this.defaultCurrency = defaultCurrency;
        this.allowedCurrencies = allowedCurrencies;
    }
}
