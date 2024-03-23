package rs.edu.raf.banka1.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBankAccountRequest {
    private String accountType;
    private Long customerId;
    private Long companyId;
    private Double balance;
    private Double availableBalance;
    private Long createdByAgentId;
    private String currency;

//    Only for current_account and foreign_currency_account
    private String subtypeOfAccount;
    private Double accountMaintenance;
}
