package rs.edu.raf.banka1.requests;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.model.Currency;

@Getter
@Setter
public class CreateTransactionRequest {
    private String accountNumber;
    private Currency currency;
    private Double value;
}
