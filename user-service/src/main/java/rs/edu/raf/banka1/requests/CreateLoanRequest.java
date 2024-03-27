package rs.edu.raf.banka1.requests;

import lombok.Data;
import rs.edu.raf.banka1.model.LoanType;

@Data
public class CreateLoanRequest {
    private LoanType loanType;
    private Double loanAmount;
    private String currency;
    private String loanPurpose;
    private Double monthlyIncomeAmount;
    private String monthlyIncomeCurrency;
    private Boolean permanentEmployee;
    private Long employmentPeriod;
    private Long loanTerm;
    private String branchOffice;
    private String phoneNumber;
    private String accountNumber;
}
