package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CurrentAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    private Long ownerId;
    private Double balance;
    private Double availableBalance;
    private Long createdByAgentId;
    private Integer creationDate;
    private Integer expirationDate;
    private String currency;
    private String accountStatus;
    private String subtypeOfAccount;
    private String accountMaintenance;
}
