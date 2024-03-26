package rs.edu.raf.banka1.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.requests.customer.AccountData;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateBankAccountRequest {
    private AccountData accountData;
    private Currency currency;
    private Customer customer;
    private Long employeeId;
    private Double maintananceFee;
}
