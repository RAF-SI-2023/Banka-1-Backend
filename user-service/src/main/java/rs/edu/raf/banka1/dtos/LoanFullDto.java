package rs.edu.raf.banka1.dtos;

import lombok.Data;
import rs.edu.raf.banka1.model.LoanType;

@Data
public class LoanFullDto {
    private Long id;
    private LoanType loanType;
    private String accountNumber;
    private Double loanAmount;
    private Integer repaymentPeriod;
    private Double nominalInterestRate;
    private Double effectiveInterestRate;
    private Long agreementDate;
    private Long maturityDate;
    private Double installmentAmount;
    private Long nextInstallmentDate;
    private Double remainingDebt;
    private String currency;
}
