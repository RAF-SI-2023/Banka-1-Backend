package rs.edu.raf.banka1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
public class LoanRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private LoanType loanType;
    @NotNull
    private Double loanAmount;
    @NotNull
    private String currency;
    @NotNull
    private String loanPurpose;
    @NotNull
    private Double monthlyIncomeAmount;
    @NotNull
    private String monthlyIncomeCurrency;
    @NotNull
    private Boolean permanentEmployee;
    @NotNull
    private Long employmentPeriod;
    @NotNull
    private Long loanTerm;
    @NotNull
    private String branchOffice;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String accountNumber;
    @NotNull
    private LoanRequestStatus status;

}
