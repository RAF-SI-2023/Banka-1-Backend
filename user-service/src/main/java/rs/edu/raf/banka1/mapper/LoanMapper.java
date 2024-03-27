package rs.edu.raf.banka1.mapper;


import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.LoanDto;
import rs.edu.raf.banka1.dtos.LoanFullDto;
import rs.edu.raf.banka1.model.Loan;

@Component
public class LoanMapper {

    public LoanDto loanToLoanDto(Loan loan) {
        if (loan == null) {
            return null;
        }

        LoanDto loanDto = new LoanDto();
        loanDto.setId(loan.getId());
        loanDto.setLoanType(loan.getLoanType());
        loanDto.setAccountNumber(loan.getAccountNumber());
        loanDto.setLoanAmount(loan.getLoanAmount());
        return loanDto;
    }

    public LoanFullDto loanToLoanFullDto(Loan loan) {
        if (loan == null) {
            return null;
        }

        LoanFullDto loanFullDto = new LoanFullDto();
        loanFullDto.setId(loan.getId());
        loanFullDto.setLoanType(loan.getLoanType());
        loanFullDto.setAccountNumber(loan.getAccountNumber());
        loanFullDto.setLoanAmount(loan.getLoanAmount());
        loanFullDto.setRepaymentPeriod(loan.getRepaymentPeriod());
        loanFullDto.setNominalInterestRate(loan.getNominalInterestRate());
        loanFullDto.setEffectiveInterestRate(loan.getEffectiveInterestRate());
        loanFullDto.setAgreementDate(loan.getAgreementDate());
        loanFullDto.setMaturityDate(loan.getMaturityDate());
        loanFullDto.setInstallmentAmount(loan.getInstallmentAmount());
        loanFullDto.setNextInstallmentDate(loan.getNextInstallmentDate());
        loanFullDto.setRemainingDebt(loan.getRemainingDebt());
        loanFullDto.setCurrency(loan.getCurrency());
        return loanFullDto;
    }




}

