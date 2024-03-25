package rs.edu.raf.banka1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
public class LoanRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    private LoanRequestStatus status;

}
