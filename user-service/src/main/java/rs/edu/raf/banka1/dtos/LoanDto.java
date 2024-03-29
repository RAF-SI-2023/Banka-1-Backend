package rs.edu.raf.banka1.dtos;

import lombok.Data;
import rs.edu.raf.banka1.model.LoanType;

@Data
public class LoanDto {
    private Long id;
    private LoanType loanType;
    private String accountNumber;
    private Double loanAmount;
}
