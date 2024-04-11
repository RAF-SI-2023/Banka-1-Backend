package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.model.Currency;

@Getter
@Setter
public class TransactionDto {

    private BankAccountDto bankAccount;

    private Long dateTime;

    private OrderDto marketOrder;

    private EmployeeDto employee;

    private String description;

    private Currency currency;

    private Double buy;

    private Double sell;

    private Double reserved;

    private Double reserveUsed;

}
