package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.mapper.ForeignCurrencyAccountMapper;
import rs.edu.raf.banka1.model.ForeignCurrencyAccount;
import rs.edu.raf.banka1.repositories.ForeignCurrencyAccountRepository;
import rs.edu.raf.banka1.requests.ForeignCurrencyAccountRequest;
import rs.edu.raf.banka1.responses.CreateForeignCurrencyAccountResponse;
import rs.edu.raf.banka1.responses.ForeignCurrencyAccountResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceImplTest {

    @Mock
    private ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;

    @Mock
    private ForeignCurrencyAccountMapper foreignCurrencyAccountMapper;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    @Test
    public void testCreateForeignCurrencyAccount() {
        ForeignCurrencyAccountRequest request = createForeignCurrencyAccountRequest();
        ForeignCurrencyAccount foreignCurrencyAccount = new ForeignCurrencyAccount();
        when(foreignCurrencyAccountMapper.createForeignCurrencyAccountRequestToForeignCurrencyAccount(request)).thenReturn(foreignCurrencyAccount);

        CreateForeignCurrencyAccountResponse response = bankAccountService.createForeignCurrencyAccount(request);

        assertEquals(foreignCurrencyAccount.getId(), response.getId());
        verify(foreignCurrencyAccountRepository, times(1)).save(foreignCurrencyAccount);
    }

    @Test
    public void testGetForeignCurrencyAccountById_WhenExists() {
        ForeignCurrencyAccount foreignCurrencyAccount = new ForeignCurrencyAccount();
        when(foreignCurrencyAccountRepository.findById(1L)).thenReturn(Optional.of(foreignCurrencyAccount));
        ForeignCurrencyAccountResponse expectedResponse = new ForeignCurrencyAccountResponse();
        when(foreignCurrencyAccountMapper.foreignCurrencyAccountToForeignCurrencyAccountResponse(foreignCurrencyAccount)).thenReturn(expectedResponse);

        ForeignCurrencyAccountResponse actualResponse = bankAccountService.getForeignCurrencyAccountById(1L);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetForeignCurrencyAccountById_WhenNotExists() {
        when(foreignCurrencyAccountRepository.findById(1L)).thenReturn(Optional.empty());

        ForeignCurrencyAccountResponse actualResponse = bankAccountService.getForeignCurrencyAccountById(1L);

        assertNull(actualResponse);
    }

    @Test
    public void testGetForeignCurrencyAccountById_WhenIdIsNull() {
        ForeignCurrencyAccountResponse actualResponse = bankAccountService.getForeignCurrencyAccountById(null);

        assertNull(actualResponse);
    }

    @Test
    public void testGetAllForeignCurrencyAccounts() {
        List<ForeignCurrencyAccount> accountList = Collections.singletonList(new ForeignCurrencyAccount());
        when(foreignCurrencyAccountRepository.findAll()).thenReturn(accountList);
        List<ForeignCurrencyAccountResponse> expectedResponseList = Collections.singletonList(new ForeignCurrencyAccountResponse());
        when(foreignCurrencyAccountMapper.foreignCurrencyAccountToForeignCurrencyAccountResponse(any())).thenReturn(expectedResponseList.getFirst());

        List<ForeignCurrencyAccountResponse> actualResponseList = bankAccountService.getAllForeignCurrencyAccounts();

        assertNotNull(actualResponseList);
        assertEquals(expectedResponseList.size(), actualResponseList.size());
        assertEquals(expectedResponseList.getFirst(), actualResponseList.getFirst());
    }

    @Test
    public void testCreateForeignCurrencyAccount_WhenRequestIsNull() {
        CreateForeignCurrencyAccountResponse response = bankAccountService.createForeignCurrencyAccount(null);

        assertNotNull(response);
    }

    @Test
    public void testCreateForeignCurrencyAccount_WhenRepositorySaveFails() {
        ForeignCurrencyAccountRequest request = new ForeignCurrencyAccountRequest();
        when(foreignCurrencyAccountMapper.createForeignCurrencyAccountRequestToForeignCurrencyAccount(request)).thenReturn(new ForeignCurrencyAccount());
        doThrow(RuntimeException.class).when(foreignCurrencyAccountRepository).save(any());

        assertThrows(RuntimeException.class, () -> bankAccountService.createForeignCurrencyAccount(request));
    }

    @Test
    public void testGetAllForeignCurrencyAccounts_WhenRepositoryReturnsEmptyList() {
        when(foreignCurrencyAccountRepository.findAll()).thenReturn(Collections.emptyList());

        List<ForeignCurrencyAccountResponse> actualResponseList = bankAccountService.getAllForeignCurrencyAccounts();

        assertNotNull(actualResponseList);
        assertTrue(actualResponseList.isEmpty());
    }

    public ForeignCurrencyAccountRequest createForeignCurrencyAccountRequest() {
        ForeignCurrencyAccountRequest request = new ForeignCurrencyAccountRequest();
        request.setOwnerId(2L);
        request.setCreatedByAgentId(1L);
        request.setCurrency("USD");
        request.setSubtypeOfAccount("LICNI");
        request.setAccountMaintenance(10.0);
        request.setDefaultCurrency(true);
        request.setAllowedCurrencies(List.of("USD", "EUR", "CHF"));
        return request;
    }
}
