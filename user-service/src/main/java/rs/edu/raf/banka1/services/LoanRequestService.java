package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.LoanRequestDto;
import rs.edu.raf.banka1.model.LoanRequestStatus;
import rs.edu.raf.banka1.requests.CreateLoanRequest;

import java.util.List;


public interface LoanRequestService {

    List<LoanRequestDto> getLoanRequests();

    List<LoanRequestDto> getLoanRequestsForAccount(final String accountNumber);

    LoanRequestDto createRequest(final CreateLoanRequest createLoanRequest);

    void changeRequestStatus(final Long id, final LoanRequestStatus loanRequestStatus);






}
