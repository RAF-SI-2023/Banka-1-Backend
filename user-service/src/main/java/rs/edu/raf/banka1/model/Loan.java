package rs.edu.raf.banka1.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LoanType loanType;
    @NotBlank
    @Column(nullable = false)
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
