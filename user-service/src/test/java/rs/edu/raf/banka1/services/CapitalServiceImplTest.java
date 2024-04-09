package rs.edu.raf.banka1.services;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.exceptions.BankAccountNotFoundException;
import rs.edu.raf.banka1.exceptions.CapitalNotFoundByBankAccountException;
import rs.edu.raf.banka1.exceptions.CapitalNotFoundByListingIdAndTypeException;
import rs.edu.raf.banka1.mapper.CapitalMapper;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Capital;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CapitalRepository;
import rs.edu.raf.banka1.services.implementations.CapitalServiceImpl;
import rs.edu.raf.banka1.services.implementations.EmployeeServiceImpl;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CapitalServiceImpl.class})
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CapitalServiceImplTest {
    @MockBean
    private BankAccountRepository bankAccountRepository;

    @MockBean
    private CapitalRepository capitalRepository;

    @MockBean
    private MarketService marketService;
    @MockBean
    private CapitalMapper capitalMapper;

    @InjectMocks
    @Autowired
    private CapitalServiceImpl capitalService;


    @BeforeEach
    public void setup() {
    }

    @Test
    public void testGetCapital() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("1234567890");

        Capital capital = new Capital();
        capital.setTotal(1000.0);
        capital.setReserved(200.0);

        when(bankAccountRepository.findBankAccountByAccountNumber("1234567890")).thenReturn(Optional.of(bankAccount));
        when(capitalRepository.getCapitalByBankAccount(bankAccount)).thenReturn(Optional.of(capital));

        Double result = capitalService.getCapital("1234567890");

        Assertions.assertEquals(800.0, result);
    }

    @Test
    public void testGetCapitalBankAccountNotFound() {
        when(bankAccountRepository.findBankAccountByAccountNumber(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(BankAccountNotFoundException.class, () -> capitalService.getCapital("nonexistent"));

        verify(bankAccountRepository).findBankAccountByAccountNumber("nonexistent");
        verifyNoMoreInteractions(capitalRepository);
    }

    @Test
    public void testGetCapitalCapitalNotFound() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("1234567890");

        when(bankAccountRepository.findBankAccountByAccountNumber("1234567890")).thenReturn(Optional.of(bankAccount));
        when(capitalRepository.getCapitalByBankAccount(bankAccount)).thenReturn(Optional.empty());

        Assertions.assertThrows(CapitalNotFoundByBankAccountException.class, () -> capitalService.getCapital("1234567890"));

        verify(bankAccountRepository).findBankAccountByAccountNumber("1234567890");
        verify(capitalRepository).getCapitalByBankAccount(bankAccount);
    }
    @Test
    public void testEstimateBalanceStock() {
        Long stockId = 123L;
        ListingStockDto listingStockDto = new ListingStockDto();
        listingStockDto.setPrice(50.0);

        Capital capital = new Capital();
        capital.setTotal(1000.0);
        capital.setReserved(200.0);

        when(marketService.getStockById(stockId)).thenReturn(listingStockDto);
        when(capitalRepository.getCapitalByListingIdAndListingType(stockId, ListingType.STOCK)).thenReturn(Optional.of(capital));

        Double result = capitalService.estimateBalanceStock(stockId);

        Assertions.assertEquals(40000.0, result);
    }

    @Test
    public void testEstimateBalanceStockCapitalNotFound() {
        Long stockId = 123L;

        when(capitalRepository.getCapitalByListingIdAndListingType(stockId, ListingType.STOCK)).thenReturn(Optional.empty());

        Assertions.assertThrows(CapitalNotFoundByListingIdAndTypeException.class, () -> capitalService.estimateBalanceStock(stockId));

        verify(capitalRepository).getCapitalByListingIdAndListingType(stockId, ListingType.STOCK);
    }
}
