package rs.edu.raf.banka1.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBankAccountRequest {

    private BankAccountRequest account;
    private Long customerId;
    private Long companyId;
}
