package rs.edu.raf.banka1.requests.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.edu.raf.banka1.model.AccountType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountData {
    private AccountType accountType;
    private Boolean status;
    private String currencyCode;
    private Double maintenanceCost;
//    private Double balance;
//    private Double availableBalance;
//    private String subtypeOfAccount;
//    private String accountName;
}
