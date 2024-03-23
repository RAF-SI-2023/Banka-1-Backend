package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentAccountDto {
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
