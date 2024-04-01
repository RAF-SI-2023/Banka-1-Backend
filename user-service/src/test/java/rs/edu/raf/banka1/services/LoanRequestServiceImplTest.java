package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.dtos.LoanRequestDto;
import rs.edu.raf.banka1.generators.LoanRequestGenerator;
import rs.edu.raf.banka1.mapper.LoanRequestMapper;
import rs.edu.raf.banka1.model.LoanRequest;
import rs.edu.raf.banka1.repositories.LoanRequestRepository;
import rs.edu.raf.banka1.requests.CreateLoanRequest;
import rs.edu.raf.banka1.services.implementations.LoanRequestServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LoanRequestServiceImplTest {
    @Mock
    private LoanRequestRepository loanRequestRepository;

    @Mock
    private LoanRequestMapper loanRequestMapper;

    @InjectMocks
    private LoanRequestServiceImpl loanRequestService;

    private LoanRequest loanRequest1;
    private LoanRequest loanRequest2;
    private LoanRequest loanRequest3;
    private LoanRequest loanRequest4;

    private final static String bankAcc = "1111";

    @BeforeEach
    public void setUp() {
        loanRequest1 = LoanRequestGenerator.generateLoanRequest();
        loanRequest2 = LoanRequestGenerator.generateLoanRequest();
        loanRequest3 = LoanRequestGenerator.generateLoanRequest(bankAcc);
        loanRequest4 = LoanRequestGenerator.generateLoanRequest(bankAcc);
    }

    @Test
    void testGetLoanRequests() {
        List<LoanRequest> loanRequests = new ArrayList<>();
        loanRequests.add(loanRequest1);
        loanRequests.add(loanRequest2);
        loanRequests.add(loanRequest3);
        loanRequests.add(loanRequest4);

        when(loanRequestRepository.findAll()).thenReturn(loanRequests);
        when(loanRequestMapper.loanRequestToLoanRequestDto(any())).thenReturn(new LoanRequestDto());

        List<LoanRequestDto> result = loanRequestService.getLoanRequests();

        assertEquals(4, result.size());
        verify(loanRequestMapper, times(4)).loanRequestToLoanRequestDto(any());
    }

    @Test
    void testGetLoanRequestsForAccount() {
        // Mocking data
        List<LoanRequest> loanRequests = new ArrayList<>();
        loanRequests.add(loanRequest3);
        loanRequests.add(loanRequest4);

        when(loanRequestRepository.findByAccountNumber(bankAcc)).thenReturn(loanRequests);
        when(loanRequestMapper.loanRequestToLoanRequestDto(any())).thenReturn(new LoanRequestDto());

        // Testing the method
        List<LoanRequestDto> result = loanRequestService.getLoanRequestsForAccount(bankAcc);

        // Verifying the results
        assertEquals(2, result.size());
        verify(loanRequestMapper, times(2)).loanRequestToLoanRequestDto(any());
    }

    @Test
    void testCreateRequest() {
        // Mocking data
        CreateLoanRequest createLoanRequest = new CreateLoanRequest();
        LoanRequest loanRequest = new LoanRequest();
        LoanRequestDto loanRequestDto = new LoanRequestDto();

        when(loanRequestMapper.createLoanRequestToLoanRequest(createLoanRequest)).thenReturn(loanRequest);
        when(loanRequestRepository.save(loanRequest)).thenReturn(loanRequest);
        when(loanRequestMapper.loanRequestToLoanRequestDto(loanRequest)).thenReturn(loanRequestDto);

        // Testing the method
        LoanRequestDto result = loanRequestService.createRequest(createLoanRequest);

        // Verifying the results
        Assertions.assertNotNull(result);
        verify(loanRequestMapper, times(1)).createLoanRequestToLoanRequest(createLoanRequest);
        verify(loanRequestRepository, times(1)).save(loanRequest);
        verify(loanRequestMapper, times(1)).loanRequestToLoanRequestDto(loanRequest);
    }
}
