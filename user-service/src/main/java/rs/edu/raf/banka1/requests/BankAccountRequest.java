package rs.edu.raf.banka1.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankAccountRequest {
    private String accountType;
    private Double balance;
    private Double availableBalance;
    private String currencyName;
    private String subtypeOfAccount;
    private Double maintenanceCost;
    private Boolean status;
}
