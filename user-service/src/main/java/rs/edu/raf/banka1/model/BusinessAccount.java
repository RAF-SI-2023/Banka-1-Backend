package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
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
    private Long creationDate;
    private Long expirationDate;
    @ManyToOne
    private Currency currency;
    private String accountStatus;


}
