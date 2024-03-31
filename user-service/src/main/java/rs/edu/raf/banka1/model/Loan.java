package rs.edu.raf.banka1.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private LoanType loanType;
    @NotBlank
    @Column(nullable = false)
    private String accountNumber;
    @NonNull
    private Double loanAmount;
    @NonNull
    private Integer repaymentPeriod;
    @NonNull
    private Double nominalInterestRate;
    @NonNull
    private Double effectiveInterestRate;
    @NonNull
    private Long agreementDate;
    @NonNull
    private Long maturityDate;
    @NonNull
    private Double installmentAmount;
    @NonNull
    private Long nextInstallmentDate;
    @NonNull
    private Double remainingDebt;
    @NonNull
    private String currency;
}
