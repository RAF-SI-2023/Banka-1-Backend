package rs.edu.raf.banka1.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.services.BankAccountService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class BankAccountControllerUnitTest {

    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private BankAccountController bankAccountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllForeignCurrencyAccountsSuccess() {
        List<ForeignCurrencyAccount> foreignCurrencyAccounts = Arrays.asList(
                createForeignCurrencyAccount(new User(), new User()),
                createForeignCurrencyAccount(new User(), new User()));

        when(bankAccountService.getAllForeignCurrencyAccounts()).thenReturn(foreignCurrencyAccounts);

        ResponseEntity<List<ForeignCurrencyAccount>> response = bankAccountController.getAllForeignCurrencyAccounts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(foreignCurrencyAccounts, response.getBody());
    }

    @Test
    void testGetForeignCurrencyAccountSuccess() {
        Long foreignCurrencyAccountId = 1L;
        ForeignCurrencyAccount foreignCurrencyAccount = createForeignCurrencyAccount(new User(), new User());
        when(bankAccountService.getForeignCurrencyAccountById(foreignCurrencyAccountId)).thenReturn(foreignCurrencyAccount);

        ResponseEntity<ForeignCurrencyAccount> response = bankAccountController.getForeignCurrencyAccount(foreignCurrencyAccountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(foreignCurrencyAccount, response.getBody());
    }

    private ForeignCurrencyAccount createForeignCurrencyAccount(User client, User user1) {
        ForeignCurrencyAccount account1 = new ForeignCurrencyAccount();
//        account1.setOwnerId(user1.getUserId());
//        account1.setUser(client);
        String creationDate = "2024-03-18";
        String expirationDate = "2024-03-18";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDateCreation = LocalDate.parse(creationDate, formatter);
        LocalDate localDateExpiration = LocalDate.parse(expirationDate, formatter);
        int dateIntegerCreation = (int) localDateCreation.toEpochDay();
        int dateIntegerExpiration = (int) localDateExpiration.toEpochDay();

        account1.setOwnerId(client.getUserId());
        account1.setCreatedByAgentId(user1.getUserId());
        account1.setAccountNumber("ACC123456789");
        account1.setTypeOfAccount("DEVIZNI");
        account1.setBalance(1000.0);
        account1.setAvailableBalance(900.0);
        account1.setCreationDate(dateIntegerCreation);
        account1.setExpirationDate(dateIntegerExpiration);
        account1.setCurrency("USD");
        account1.setAccountStatus(AccountStatus.ACTIVE);
        account1.setSubtypeOfAccount(SubtypeOfAccount.LICNI);
        account1.setAccountMaintenance(10.0);
        account1.setDefaultCurrency(true);
        return account1;
    }
}
