package rs.edu.raf.banka1.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private LoanType loanType;
    @NotBlank
    @Column(nullable = false)
    private String accountNumber;
    @NotNull
    private Double loanAmount;
    @NotNull
    private Integer repaymentPeriod;
    @NotNull
    private Double nominalInterestRate;
    @NotNull
    private Double effectiveInterestRate;
    private Long agreementDate;
    private Long maturityDate;
    private Double installmentAmount;
    private Long nextInstallmentDate;
    private Double remainingDebt;
    private String currency;
}
