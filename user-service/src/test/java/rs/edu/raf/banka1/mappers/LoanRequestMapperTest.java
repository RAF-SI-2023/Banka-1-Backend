package rs.edu.raf.banka1.mappers;

import org.junit.jupiter.api.Test;
import rs.edu.raf.banka1.dtos.LoanRequestDto;
import rs.edu.raf.banka1.mapper.LoanRequestMapper;
import rs.edu.raf.banka1.model.LoanRequest;
import rs.edu.raf.banka1.model.LoanRequestStatus;
import rs.edu.raf.banka1.model.LoanType;
import rs.edu.raf.banka1.requests.CreateLoanRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanRequestMapperTest {

    @Test
    void testLoanRequestToLoanRequestDto() {
        // Arrange
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setId(1L);
        loanRequest.setLoanType(LoanType.AUTO);
        loanRequest.setLoanAmount(100000.0);
        loanRequest.setCurrency("USD");
        loanRequest.setLoanPurpose("Buying a new house");
        loanRequest.setMonthlyIncomeAmount(5000.0);
        loanRequest.setMonthlyIncomeCurrency("USD");
        loanRequest.setPermanentEmployee(true);
        loanRequest.setEmploymentPeriod(5L);
        loanRequest.setLoanTerm(20L);
        loanRequest.setBranchOffice("Main Branch");
        loanRequest.setPhoneNumber("123456789");
        loanRequest.setAccountNumber("1234567890");
        loanRequest.setStatus(LoanRequestStatus.PENDING);

        LoanRequestMapper mapper = new LoanRequestMapper();

        // Act
        LoanRequestDto loanRequestDto = mapper.loanRequestToLoanRequestDto(loanRequest);

        // Assert
        assertEquals(loanRequest.getId(), loanRequestDto.getId());
        assertEquals(loanRequest.getLoanType(), loanRequestDto.getLoanType());
        assertEquals(loanRequest.getLoanAmount(), loanRequestDto.getLoanAmount());
        assertEquals(loanRequest.getCurrency(), loanRequestDto.getCurrency());
        assertEquals(loanRequest.getLoanPurpose(), loanRequestDto.getLoanPurpose());
        assertEquals(loanRequest.getMonthlyIncomeAmount(), loanRequestDto.getMonthlyIncomeAmount());
        assertEquals(loanRequest.getMonthlyIncomeCurrency(), loanRequestDto.getMonthlyIncomeCurrency());
        assertEquals(loanRequest.getPermanentEmployee(), loanRequestDto.getPermanentEmployee());
        assertEquals(loanRequest.getEmploymentPeriod(), loanRequestDto.getEmploymentPeriod());
        assertEquals(loanRequest.getLoanTerm(), loanRequestDto.getLoanTerm());
        assertEquals(loanRequest.getBranchOffice(), loanRequestDto.getBranchOffice());
        assertEquals(loanRequest.getPhoneNumber(), loanRequestDto.getPhoneNumber());
        assertEquals(loanRequest.getAccountNumber(), loanRequestDto.getAccountNumber());
        assertEquals(loanRequest.getStatus(), loanRequestDto.getStatus());
    }

    @Test
    void testCreateLoanRequestToLoanRequest() {
        // Arrange
        CreateLoanRequest createLoanRequest = new CreateLoanRequest();
        createLoanRequest.setLoanType(LoanType.AUTO);
        createLoanRequest.setLoanAmount(50000.0);
        createLoanRequest.setCurrency("EUR");
        createLoanRequest.setLoanPurpose("Buying a new car");
        createLoanRequest.setMonthlyIncomeAmount(3000.0);
        createLoanRequest.setMonthlyIncomeCurrency("EUR");
        createLoanRequest.setPermanentEmployee(false);
        createLoanRequest.setEmploymentPeriod(3L);
        createLoanRequest.setLoanTerm(5L);
        createLoanRequest.setBranchOffice("Branch A");
        createLoanRequest.setPhoneNumber("987654321");
        createLoanRequest.setAccountNumber("9876543210");

        LoanRequestMapper mapper = new LoanRequestMapper();

        // Act
        LoanRequest loanRequest = mapper.createLoanRequestToLoanRequest(createLoanRequest);

        // Assert
        assertEquals(createLoanRequest.getLoanType(), loanRequest.getLoanType());
        assertEquals(createLoanRequest.getLoanAmount(), loanRequest.getLoanAmount());
        assertEquals(createLoanRequest.getCurrency(), loanRequest.getCurrency());
        assertEquals(createLoanRequest.getLoanPurpose(), loanRequest.getLoanPurpose());
        assertEquals(createLoanRequest.getMonthlyIncomeAmount(), loanRequest.getMonthlyIncomeAmount());
        assertEquals(createLoanRequest.getMonthlyIncomeCurrency(), loanRequest.getMonthlyIncomeCurrency());
        assertEquals(createLoanRequest.getPermanentEmployee(), loanRequest.getPermanentEmployee());
        assertEquals(createLoanRequest.getEmploymentPeriod(), loanRequest.getEmploymentPeriod());
        assertEquals(createLoanRequest.getLoanTerm(), loanRequest.getLoanTerm());
        assertEquals(createLoanRequest.getBranchOffice(), loanRequest.getBranchOffice());
        assertEquals(createLoanRequest.getPhoneNumber(), loanRequest.getPhoneNumber());
        assertEquals(createLoanRequest.getAccountNumber(), loanRequest.getAccountNumber());
        assertEquals(LoanRequestStatus.PENDING, loanRequest.getStatus()); // Verify default status
    }
}
