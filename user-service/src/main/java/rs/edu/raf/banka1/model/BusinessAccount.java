package rs.edu.raf.banka1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BusinessAccount {
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


}
