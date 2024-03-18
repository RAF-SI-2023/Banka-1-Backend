package rs.edu.raf.banka1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "devizni_racuni", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class DevizniRacun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
//    private Long ownerId;
    private Long createdByAgentId;
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private VrstaRacuna vrstaRacuna;
    private Double balance;
    private Double availableBalance;
    private String creationDate;
    private String expirationDate;
    private String currency;
    @Enumerated(EnumType.STRING)
    private StatusRacuna statusRacuna;
    @Enumerated(EnumType.STRING)
    private PodvrstaRacuna podvrstaRacuna;
    private Double accountMaintenance;
    private Boolean defaultCurrency;

//    private List<String> allowedCurrencies;

}
