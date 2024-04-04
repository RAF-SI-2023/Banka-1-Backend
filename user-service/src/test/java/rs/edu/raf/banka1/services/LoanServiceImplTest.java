package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.dtos.LoanDto;
import rs.edu.raf.banka1.dtos.LoanFullDto;
import rs.edu.raf.banka1.generators.LoanGenerators;
import rs.edu.raf.banka1.mapper.LoanMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.LoanRepository;
import rs.edu.raf.banka1.services.implementations.LoanServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LoanServiceImplTest {
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Loan loan1;
    private Loan loan2;
    private Loan loan3;
    private Loan loan4;

    private static String accNum = "11111";


    @BeforeEach
    public void setUp(){
        loan1 = LoanGenerators.generateLoan();
        loan2 = LoanGenerators.generateLoan();
        loan3 = LoanGenerators.generateLoan(accNum);
        loan4 = LoanGenerators.generateLoan(accNum);
    }

    @Test
    void testGetLoans() {
        List<Loan> loans = new ArrayList<>();
        loans.add(loan1);
        loans.add(loan2);
        loans.add(loan3);
        loans.add(loan4);

        LoanDto loanDto1 = new LoanDto();

        when(loanRepository.findAll()).thenReturn(loans);
        when(loanMapper.loanToLoanDto(any())).thenReturn(loanDto1);

        List<LoanDto> result = loanService.getLoans();

        assertEquals(4, result.size());
    }
    @Test
    void testGetLoansForUser() {
        Long id = 1L;
        List<Loan> loans = new ArrayList<>();
        loans.add(loan1);
        loans.add(loan2);
        loans.add(loan3);
        loans.add(loan4);

        LoanDto loanDto1 = new LoanDto();

        when(loanRepository.findByUser(id)).thenReturn(loans);
        when(loanMapper.loanToLoanDto(any())).thenReturn(loanDto1);

        List<LoanDto> result = loanService.getLoansForUser(id);

        assertEquals(4, result.size());
    }

    @Test
    void testGetLoansForAccountNumber() {
        List<Loan> loans = new ArrayList<>();
        loans.add(loan3);
        loans.add(loan4);

        LoanDto loanDto1 = new LoanDto();

        when(loanRepository.findByAccountNumber(accNum)).thenReturn(loans);
        when(loanMapper.loanToLoanDto(any())).thenReturn(loanDto1);

        List<LoanDto> result = loanService.getLoansForAccount(accNum);

        assertEquals(2, result.size());
    }

    @Test
    void testGetLoanDetail() {
        Long id = 1L;

        LoanFullDto loanDto1 = new LoanFullDto();

        when(loanRepository.findById(id)).thenReturn(Optional.of(loan1));
        when(loanMapper.loanToLoanFullDto(loan1)).thenReturn(loanDto1);

        LoanFullDto result = loanService.getLoanDetails(id);

        assertEquals(loanDto1,result);
    }

    @Test
    void testGetLoanDetailsNotFound() {
        // Mocking data
        Long id = 1L;
        when(loanRepository.findById(id)).thenReturn(Optional.empty());

        // Testing the method
        assertThrows(NoSuchElementException.class, () -> loanService.getLoanDetails(id));
    }


}
