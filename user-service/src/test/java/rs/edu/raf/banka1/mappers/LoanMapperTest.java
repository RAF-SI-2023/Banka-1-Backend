package rs.edu.raf.banka1.mappers;
import org.junit.jupiter.api.Test;
import rs.edu.raf.banka1.dtos.LoanDto;
import rs.edu.raf.banka1.dtos.LoanFullDto;
import rs.edu.raf.banka1.mapper.LoanMapper;
import rs.edu.raf.banka1.model.Loan;
import rs.edu.raf.banka1.model.LoanType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoanMapperTest {

    private final LoanMapper loanMapper = new LoanMapper();

    @Test
    void testLoanToLoanDto() {
        // Arrange
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLoanType(LoanType.AUTO);
        loan.setAccountNumber("123456789");
        loan.setLoanAmount(1000.0);

        // Act
        LoanDto loanDto = loanMapper.loanToLoanDto(loan);

        // Assert
        assertEquals(loan.getId(), loanDto.getId());
        assertEquals(loan.getLoanType(), loanDto.getLoanType());
        assertEquals(loan.getAccountNumber(), loanDto.getAccountNumber());
        assertEquals(loan.getLoanAmount(), loanDto.getLoanAmount());
    }

    @Test
    void testLoanToLoanDto_NullInput() {
        // Act
        LoanDto loanDto = loanMapper.loanToLoanDto(null);

        // Assert
        assertNull(loanDto);
    }

    @Test
    void testLoanToLoanFullDto() {
        // Arrange
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLoanType(LoanType.AUTO);
        loan.setAccountNumber("123456789");
        loan.setLoanAmount(1000.0);
        loan.setRepaymentPeriod(12);
        loan.setNominalInterestRate(5.0);
        loan.setEffectiveInterestRate(5.5);

        // Act
        LoanFullDto loanFullDto = loanMapper.loanToLoanFullDto(loan);

        // Assert
        assertEquals(loan.getId(), loanFullDto.getId());
        assertEquals(loan.getLoanType(), loanFullDto.getLoanType());
        assertEquals(loan.getAccountNumber(), loanFullDto.getAccountNumber());
        assertEquals(loan.getLoanAmount(), loanFullDto.getLoanAmount());
        assertEquals(loan.getRepaymentPeriod(), loanFullDto.getRepaymentPeriod());
        assertEquals(loan.getNominalInterestRate(), loanFullDto.getNominalInterestRate());
        assertEquals(loan.getEffectiveInterestRate(), loanFullDto.getEffectiveInterestRate());
    }

    @Test
    void testLoanToLoanFullDto_NullInput() {
        // Act
        LoanFullDto loanFullDto = loanMapper.loanToLoanFullDto(null);

        // Assert
        assertNull(loanFullDto);
    }
}
