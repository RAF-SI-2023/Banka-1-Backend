package rs.edu.raf.banka1.services;

import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.LoanRequestDto;
import rs.edu.raf.banka1.mapper.LoanRequestMapper;
import rs.edu.raf.banka1.model.LoanRequestStatus;
import rs.edu.raf.banka1.repositories.LoanRequestRepository;
import rs.edu.raf.banka1.requests.CreateLoanRequest;

import java.util.List;

@Service
public class LoanRequestServiceImpl implements LoanRequestService{

    private final LoanRequestRepository loanRequestRepository;
    private final LoanRequestMapper loanRequestMapper;

    public LoanRequestServiceImpl(
        final LoanRequestRepository loanRequestRepository,
        final LoanRequestMapper loanRequestMapper) {
        this.loanRequestRepository = loanRequestRepository;
        this.loanRequestMapper = loanRequestMapper;
    }

    @Override
    public List<LoanRequestDto> getLoanRequests() {
        return loanRequestRepository.findAll()
            .stream().map(loanRequestMapper::loanRequestToLoanRequestDto).toList();
    }

    @Override
    public List<LoanRequestDto> getLoanRequestsForAccount(final String accountNumber) {
        return loanRequestRepository.findByAccountNumber(accountNumber)
            .stream().map(loanRequestMapper::loanRequestToLoanRequestDto).toList();
    }

    @Override
    public LoanRequestDto createRequest(final CreateLoanRequest createLoanRequest) {
        return loanRequestMapper.loanRequestToLoanRequestDto(
            loanRequestRepository.save(
                loanRequestMapper.createLoanRequestToLoanRequest(createLoanRequest)
            )
        );
    }

    @Override
    public boolean changeRequestStatus(Long id, LoanRequestStatus loanRequestStatus) {
        loanRequestRepository.changeStatusForLoan(id, loanRequestStatus);
        return true;
    }
}
