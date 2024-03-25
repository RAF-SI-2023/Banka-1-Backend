package rs.edu.raf.banka1.mapper;


import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.LoanRequestDto;
import rs.edu.raf.banka1.model.LoanRequest;
import rs.edu.raf.banka1.model.LoanRequestStatus;
import rs.edu.raf.banka1.requests.CreateLoanRequest;

@Component
public class LoanRequestMapper {

    public LoanRequestDto loanRequestToLoanRequestDto(LoanRequest loanRequest) {
        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setId(loanRequest.getId());
        loanRequestDto.setLoanType(loanRequest.getLoanType());
        loanRequestDto.setLoanAmount(loanRequest.getLoanAmount());
        loanRequestDto.setCurrency(loanRequest.getCurrency());
        loanRequestDto.setLoanPurpose(loanRequest.getLoanPurpose());
        loanRequestDto.setMonthlyIncomeAmount(loanRequest.getMonthlyIncomeAmount());
        loanRequestDto.setMonthlyIncomeCurrency(loanRequest.getMonthlyIncomeCurrency());
        loanRequestDto.setPermanentEmployee(loanRequest.getPermanentEmployee());
        loanRequestDto.setEmploymentPeriod(loanRequest.getEmploymentPeriod());
        loanRequestDto.setLoanTerm(loanRequest.getLoanTerm());
        loanRequestDto.setBranchOffice(loanRequest.getBranchOffice());
        loanRequestDto.setPhoneNumber(loanRequest.getPhoneNumber());
        loanRequestDto.setAccountNumber(loanRequest.getAccountNumber());
        return loanRequestDto;
    }

    public LoanRequest createLoanRequestToLoanRequest(CreateLoanRequest createLoanRequest) {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanType(createLoanRequest.getLoanType());
        loanRequest.setLoanAmount(createLoanRequest.getLoanAmount());
        loanRequest.setCurrency(createLoanRequest.getCurrency());
        loanRequest.setLoanPurpose(createLoanRequest.getLoanPurpose());
        loanRequest.setMonthlyIncomeAmount(createLoanRequest.getMonthlyIncomeAmount());
        loanRequest.setMonthlyIncomeCurrency(createLoanRequest.getMonthlyIncomeCurrency());
        loanRequest.setPermanentEmployee(createLoanRequest.getPermanentEmployee());
        loanRequest.setEmploymentPeriod(createLoanRequest.getEmploymentPeriod());
        loanRequest.setLoanTerm(createLoanRequest.getLoanTerm());
        loanRequest.setBranchOffice(createLoanRequest.getBranchOffice());
        loanRequest.setPhoneNumber(createLoanRequest.getPhoneNumber());
        loanRequest.setAccountNumber(createLoanRequest.getAccountNumber());
        loanRequest.setStatus(LoanRequestStatus.PENDING);
        return loanRequest;
    }



}

