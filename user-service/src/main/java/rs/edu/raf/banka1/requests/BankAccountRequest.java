package rs.edu.raf.banka1.requests;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.AccountType;
import rs.edu.raf.banka1.model.Company;
import rs.edu.raf.banka1.model.Customer;

@Getter
@Setter
public class BankAccountRequest {
    private AccountType accountType;
    private String accountName;
    private Double balance;
    private Double availableBalance;
    private String currencyCode;
    private String subtypeOfAccount;
    private Double maintenanceCost;
}
