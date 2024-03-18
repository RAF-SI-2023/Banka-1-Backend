package rs.edu.raf.banka1.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rs.edu.raf.banka1.model.DevizniRacun;
import rs.edu.raf.banka1.model.PodvrstaRacuna;
import rs.edu.raf.banka1.model.StatusRacuna;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.model.VrstaRacuna;
import rs.edu.raf.banka1.services.BankAccountService;

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
    void testGetAllDevizniRacuniSuccess() {
        List<DevizniRacun> devizniRacuni = Arrays.asList(
                createDevizniRacun(new User(), new User()),
                createDevizniRacun(new User(), new User()));

        when(bankAccountService.getAllDevizniRacuni()).thenReturn(devizniRacuni);

        ResponseEntity<List<DevizniRacun>> response = bankAccountController.getAllDevizniRacuni();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(devizniRacuni, response.getBody());
    }

    @Test
    void testGetDevizniRacunSuccess() {
        Long devizniRacunId = 1L;
        DevizniRacun devizniRacun = createDevizniRacun(new User(), new User());
        when(bankAccountService.getDevizniRacunById(devizniRacunId)).thenReturn(devizniRacun);

        ResponseEntity<DevizniRacun> response = bankAccountController.getDevizniRacun(devizniRacunId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(devizniRacun, response.getBody());
    }

    private DevizniRacun createDevizniRacun(User client, User user1) {
        DevizniRacun account1 = new DevizniRacun();
//        account1.setOwnerId(user1.getUserId());
        account1.setUser(client);
        account1.setCreatedByAgentId(user1.getUserId());
        account1.setAccountNumber("ACC123456789");
        account1.setVrstaRacuna(VrstaRacuna.DEVIZNI);
        account1.setBalance(1000.0);
        account1.setAvailableBalance(900.0);
        account1.setCreationDate("2024-03-18");
        account1.setExpirationDate("2025-03-18");
        account1.setCurrency("USD");
        account1.setStatusRacuna(StatusRacuna.ACTIVE);
        account1.setPodvrstaRacuna(PodvrstaRacuna.LICNI);
        account1.setAccountMaintenance(10.0);
        account1.setDefaultCurrency(true);
        return account1;
    }
}
