package rs.edu.raf.banka1.services;

import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import rs.edu.raf.banka1.dtos.LoanDto;
import rs.edu.raf.banka1.dtos.LoanFullDto;
import rs.edu.raf.banka1.mapper.LoanMapper;
import rs.edu.raf.banka1.repositories.LoanRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LoanServiceImpl implements LoanService{

    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;

    public LoanServiceImpl(
        final LoanRepository loanRepository,
        final LoanMapper loanMapper
    ) {
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
    }


    @Override
    public List<LoanDto> getLoans() {
        return loanRepository.findAll()
            .stream().map(loanMapper::loanToLoanDto).toList();
    }

    @Override
    public List<LoanDto> getLoansForUser(Long userId) {
        return loanRepository.findByUser(userId)
            .stream().map(loanMapper::loanToLoanDto).toList();
    }

    @Override
    public List<LoanDto> getLoansForAccount(String accountNumber) {
        return loanRepository.findByAccountNumber(accountNumber)
            .stream().map(loanMapper::loanToLoanDto).toList();
    }

    @Override
    public LoanFullDto getLoanDetails(Long id) {
        return loanRepository.findById(id)
            .map(loanMapper::loanToLoanFullDto)
            .orElseThrow(NoSuchElementException::new);
    }
}
