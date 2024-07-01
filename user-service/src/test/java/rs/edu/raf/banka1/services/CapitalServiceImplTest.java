package rs.edu.raf.banka1.services;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.dtos.AddPublicCapitalDto;
import rs.edu.raf.banka1.dtos.AllPublicCapitalsDto;
import rs.edu.raf.banka1.dtos.CapitalProfitDto;
import rs.edu.raf.banka1.dtos.PublicCapitalDto;
import rs.edu.raf.banka1.dtos.market_service.ListingForexDto;
import rs.edu.raf.banka1.dtos.market_service.ListingFutureDto;
import rs.edu.raf.banka1.dtos.market_service.OptionsDto;
import rs.edu.raf.banka1.exceptions.*;


import org.junit.jupiter.api.Assertions;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.mapper.CapitalMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CapitalRepository;
import rs.edu.raf.banka1.repositories.CompanyRepository;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.implementations.CapitalServiceImpl;
import rs.edu.raf.banka1.utils.Constants;

import java.util.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CapitalServiceImplTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private CapitalRepository capitalRepository;
    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CapitalMapper capitalMapper;
    @Mock
    private BankAccountService bankAccountService;
    @Mock
    private MarketService marketService;

    private CapitalServiceImpl capitalService;

    @BeforeEach
    public void setUp() {
        //  capitalMapper = new CapitalMapper();
        capitalService = new CapitalServiceImpl(bankAccountRepository, capitalRepository, capitalMapper, bankAccountService, marketService, companyRepository);
//        capitalService.setCapitalMapper(capitalMapper);
//        capitalService.setCapitalRepository(capitalRepository);
//        capitalService.setBankAccountRepository(bankAccountRepository);
//        capitalService.setBankAccountService(bankAccountService);
    }

    @Test
    void shouldCreateCapitalForBankAccount_Stock() {
        BankAccount bankAccount = new BankAccount();
        Double total = 1.00;
        Double reserved = 0.00;
        Long listingId = 1l;
        ListingType listingType = ListingType.STOCK;
        ListingStockDto stockDto = new ListingStockDto();
        stockDto.setTicker("DT");

        Capital capital = new Capital();
        capital.setBankAccount(bankAccount);
        capital.setTotal(total);
        capital.setReserved(reserved);
        capital.setListingId(listingId);
        capital.setListingType(ListingType.STOCK);

        when(marketService.getStockById(listingId)).thenReturn(stockDto);

        Capital result = capitalService.createCapital(listingType, listingId, total, reserved, bankAccount);
        assertEquals(capital.getBankAccount(), result.getBankAccount());
    }
    @Test
    void shouldCreateCapitalForBankAccount_Future() {
        BankAccount bankAccount = new BankAccount();
        Double total = 1.00;
        Double reserved = 0.00;
        Long listingId = 1l;
        ListingType listingType = ListingType.FUTURE;
        ListingFutureDto futureDto = new ListingFutureDto();
        futureDto.setTicker("DT");

        Capital capital = new Capital();
        capital.setBankAccount(bankAccount);
        capital.setTotal(total);
        capital.setReserved(reserved);
        capital.setListingId(listingId);
        capital.setListingType(ListingType.FUTURE);

        when(marketService.getFutureById(listingId)).thenReturn(futureDto);

        Capital result = capitalService.createCapital(listingType, listingId, total, reserved, bankAccount);
        assertEquals(capital.getBankAccount(), result.getBankAccount());
    }

    @Test
    void shouldCreateCapitalForBankAccount_Forex() {
        BankAccount bankAccount = new BankAccount();
        Double total = 1.00;
        Double reserved = 0.00;
        Long listingId = 1l;
        ListingType listingType = ListingType.FOREX;
        ListingForexDto forexDto = new ListingForexDto();
        forexDto.setTicker("DT");

        Capital capital = new Capital();
        capital.setBankAccount(bankAccount);
        capital.setTotal(total);
        capital.setReserved(reserved);
        capital.setListingId(listingId);
        capital.setListingType(ListingType.FOREX);

        when(marketService.getForexById(listingId)).thenReturn(forexDto);

        Capital result = capitalService.createCapital(listingType, listingId, total, reserved, bankAccount);
        assertEquals(capital.getBankAccount(), result.getBankAccount());
    }

    @Test
    void shouldCreateCapitalForBankAccount_Options() {
        BankAccount bankAccount = new BankAccount();
        Double total = 1.00;
        Double reserved = 0.00;
        Long listingId = 1l;
        ListingType listingType = ListingType.OPTIONS;
        OptionsDto optionsDto = new OptionsDto();
        optionsDto.setTicker("DT");

        Capital capital = new Capital();
        capital.setBankAccount(bankAccount);
        capital.setTotal(total);
        capital.setReserved(reserved);
        capital.setListingId(listingId);
        capital.setListingType(ListingType.OPTIONS);

        when(marketService.getOptionsById(listingId)).thenReturn(optionsDto);

        Capital result = capitalService.createCapital(listingType, listingId, total, reserved, bankAccount);
        assertEquals(capital.getBankAccount(), result.getBankAccount());
    }
    @Test
    void testGetCapitalByListingIdAndTypeAndBankAccount_CapitalExists() {
        Long listingId = 1L;
        BankAccount bankAccount = new BankAccount();
        ListingType type = ListingType.STOCK;
        Capital capital = new Capital();

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount))
                .thenReturn(Optional.of(capital));

        Capital result = capitalService.getCapitalByListingIdAndTypeAndBankAccount(listingId, type, bankAccount);

        assertNotNull(result);
        assertEquals(capital, result);
        verify(capitalRepository, times(1)).getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount);
    }

    @Test
    void testGetCapitalStockForBank() {
        Capital capital1 = new Capital();
        Capital capital2 = new Capital();
        BankAccount bankAccount = new BankAccount();
        List<Capital> capitalList = Arrays.asList(capital1, capital2);

        when(capitalRepository.getCapitalsByListingTypeAndBankAccount(ListingType.STOCK, bankAccount))
                .thenReturn(capitalList);

        List<Capital> result = capitalService.getCapitalStockForBank(bankAccount);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(capitalList, result);
        verify(capitalRepository, times(1)).getCapitalsByListingTypeAndBankAccount(ListingType.STOCK, bankAccount);
    }

    @Test
    void testRemoveBalance_Success() {
        Long listingId = 1L;
        ListingType type = ListingType.STOCK;
        BankAccount bankAccount = new BankAccount();
        Double amount = 100.0;
        Capital capital = new Capital();
        capital.setTotal(200.0);
        capital.setReserved(50.0);

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount))
                .thenReturn(Optional.of(capital));

        capitalService.removeBalance(listingId, type, bankAccount, amount);

        assertEquals(100.0, capital.getTotal());
        verify(capitalRepository, times(1)).getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount);
        verify(capitalRepository, times(1)).save(capital);
    }

    @Test
    void testRemoveBalance_CapitalNotFound() {
        Long listingId = 1L;
        BankAccount bankAccount = new BankAccount();
        ListingType type = ListingType.STOCK; // Replace with actual ListingType
        Double amount = 100.0;

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount))
                .thenReturn(Optional.empty());

        assertThrows(CapitalNotFoundByListingIdAndTypeException.class, () -> {
            capitalService.removeBalance(listingId, type, bankAccount, amount);
        });

        verify(capitalRepository, times(1)).getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount);
        verify(capitalRepository, never()).save(any(Capital.class));
    }

    @Test
    void testRemoveBalance_InvalidAmount() {
        Long listingId = 1L;
        ListingType type = ListingType.STOCK;
        BankAccount bankAccount = new BankAccount();
        Double amount = -100.0;
        Capital capital = new Capital();
        capital.setTotal(200.0);
        capital.setReserved(50.0);

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount))
                .thenReturn(Optional.of(capital));

        assertThrows(InvalidCapitalAmountException.class, () -> {
            capitalService.removeBalance(listingId, type, bankAccount, amount);
        });

        verify(capitalRepository, times(1)).getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount);
        verify(capitalRepository, never()).save(any(Capital.class));
    }

    @Test
    void testCommitReserved_Success() {
        Long listingId = 1L;
        ListingType type = ListingType.STOCK;
        BankAccount bankAccount = new BankAccount();
        Double amount = 50.0;
        Capital capital = new Capital();
        capital.setTotal(200.0);
        capital.setReserved(100.0);

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount))
                .thenReturn(Optional.of(capital));

        capitalService.commitReserved(listingId, type, bankAccount, amount);

        assertEquals(150.0, capital.getTotal());
        assertEquals(50.0, capital.getReserved());
        verify(capitalRepository, times(1)).getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount);
        verify(capitalRepository, times(1)).save(capital);
    }

    @Test
    void testCommitReserved_CapitalNotFound() {
        Long listingId = 1L;
        ListingType type = ListingType.STOCK;
        BankAccount bankAccount = new BankAccount();
        Double amount = 50.0;

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount))
                .thenReturn(Optional.empty());

        assertThrows(CapitalNotFoundByListingIdAndTypeException.class, () -> {
            capitalService.commitReserved(listingId, type, bankAccount, amount);
        });

        verify(capitalRepository, times(1)).getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount);
        verify(capitalRepository, never()).save(any(Capital.class));
    }

    @Test
    void testCommitReserved_InvalidAmount() {
        Long listingId = 1L;
        ListingType type = ListingType.STOCK;
        BankAccount bankAccount = new BankAccount();
        Double amount = -50.0;
        Capital capital = new Capital();
        capital.setTotal(200.0);
        capital.setReserved(100.0);

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount))
                .thenReturn(Optional.of(capital));

        assertThrows(InvalidReservationAmountException.class, () -> {
            capitalService.commitReserved(listingId, type, bankAccount, amount);
        });

        verify(capitalRepository, times(1)).getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount);
        verify(capitalRepository, never()).save(any(Capital.class));
    }

    @Test
    void testReleaseReserved_Success() {
        Long listingId = 1L;
        ListingType type = ListingType.STOCK;
        BankAccount bankAccount = new BankAccount();
        Double amount = 50.0;
        Capital capital = new Capital();
        capital.setReserved(100.0);

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount))
                .thenReturn(Optional.of(capital));

        capitalService.releaseReserved(listingId, type, bankAccount, amount);

        assertEquals(50.0, capital.getReserved());
        verify(capitalRepository, times(1)).getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount);
        verify(capitalRepository, times(1)).save(capital);
    }

    @Test
    void testReleaseReserved_CapitalNotFound() {
        Long listingId = 1L;
        ListingType type = ListingType.STOCK;
        BankAccount bankAccount = new BankAccount();
        Double amount = 50.0;

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount))
                .thenReturn(Optional.empty());

        assertThrows(CapitalNotFoundByListingIdAndTypeException.class, () -> {
            capitalService.releaseReserved(listingId, type, bankAccount, amount);
        });

        verify(capitalRepository, times(1)).getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount);
        verify(capitalRepository, never()).save(any(Capital.class));
    }

    @Test
    void testReleaseReserved_InvalidAmount() {
        Long listingId = 1L;
        ListingType type = ListingType.STOCK;
        BankAccount bankAccount = new BankAccount();
        Double amount = -50.0;
        Capital capital = new Capital();
        capital.setReserved(100.0);

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount))
                .thenReturn(Optional.of(capital));

        assertThrows(InvalidReservationAmountException.class, () -> {
            capitalService.releaseReserved(listingId, type, bankAccount, amount);
        });

        verify(capitalRepository, times(1)).getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount);
        verify(capitalRepository, never()).save(any(Capital.class));
    }

    @Test
    void testAddBalance_Success_CapitalExists() {
        Long listingId = 1L;
        ListingType type = ListingType.STOCK;
        BankAccount bankAccount = new BankAccount();
        Double amount = 50.0;
        Capital capital = new Capital();
        capital.setTotal(100.0);

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount))
                .thenReturn(Optional.of(capital));

        capitalService.addBalance(listingId, type, bankAccount, amount);

        assertEquals(150.0, capital.getTotal());
        verify(capitalRepository, times(1)).getCapitalByListingIdAndListingTypeAndBankAccount(listingId, type, bankAccount);
        verify(capitalRepository, times(1)).save(capital);
    }

    @Test
    void testGetAllPublicStockCapitals() {
        // Arrange
        Capital capital1 = new Capital();
        capital1.setListingId(1L);
        capital1.setListingType(ListingType.STOCK);
        capital1.setTotal(100.0);
        capital1.setReserved(20.0);
        capital1.setPublicTotal(50.0);

        List<Capital> capitals = Collections.singletonList(capital1);

        PublicCapitalDto publicCapitalDto1 = new PublicCapitalDto();
        publicCapitalDto1.setListingId(1L);
        publicCapitalDto1.setListingType(ListingType.STOCK);
        publicCapitalDto1.setPublicTotal(50.0);
        publicCapitalDto1.setBankAccountNumber("12");

        List<PublicCapitalDto> expectedPublicCapitalDtos = Collections.singletonList(publicCapitalDto1);

        when(capitalRepository.findByBankAccount_CompanyNullAndListingTypeAndPublicTotalGreaterThan(ListingType.STOCK, 0d)).thenReturn(capitals);
        when(capitalMapper.capitalToPublicCapitalDto(capital1)).thenReturn(publicCapitalDto1);

        // Act
        List<PublicCapitalDto> result = capitalService.getAllPublicStockCapitals();

        // Assert
        assertEquals(expectedPublicCapitalDtos, result);
    }

    @Test
    void testGetAllPublicListingCapitals() {
        // Arrange
        Capital capital1 = new Capital();
        capital1.setListingId(1L);
        capital1.setListingType(ListingType.STOCK);
        capital1.setTotal(100.0);
        capital1.setReserved(20.0);
        capital1.setPublicTotal(50.0);

        List<Capital> capitals = Collections.singletonList(capital1);

        PublicCapitalDto publicCapitalDto1 = new PublicCapitalDto();
        publicCapitalDto1.setListingId(1L);
        publicCapitalDto1.setListingType(ListingType.STOCK);
        publicCapitalDto1.setPublicTotal(50.0);

        List<PublicCapitalDto> expectedPublicCapitalDtos = Collections.singletonList(publicCapitalDto1);

        when(capitalRepository.getAllPublicCapitals()).thenReturn(capitals);
        when(capitalMapper.capitalToPublicCapitalDto(capital1)).thenReturn(publicCapitalDto1);

        // Act
        List<PublicCapitalDto> result = capitalService.getAllPublicListingCapitals();

        // Assert
        assertEquals(expectedPublicCapitalDtos, result);
    }

    @Test
    public void testEstimateBalanceForex_CapitalNotFound() {
        BankAccount bankAccount = new BankAccount();
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(1L, ListingType.FOREX, bankAccount)).thenReturn(Optional.empty());
        assertThrows(CapitalNotFoundByListingIdAndTypeException.class, () -> capitalService.estimateBalanceForex(1L));
    }

    @Test
    public void testEstimateBalanceStock_CapitalNotFound() {
        BankAccount bankAccount = new BankAccount();
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(1L, ListingType.STOCK, bankAccount)).thenReturn(Optional.empty());
        assertThrows(CapitalNotFoundByListingIdAndTypeException.class, () -> capitalService.estimateBalanceStock(1L));
    }

    @Test
    public void testEstimateBalanceOptions_CapitalNotFound() {
        BankAccount bankAccount = new BankAccount();
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(1L, ListingType.OPTIONS, bankAccount)).thenReturn(Optional.empty());
        assertThrows(CapitalNotFoundByListingIdAndTypeException.class, () -> capitalService.estimateBalanceOptions(1L));
    }

    @Test
    void testEstimateBalanceStock() {
        // Arrange
        Long stockId = 1L;
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123456789");

        ListingStockDto listingStockDto = new ListingStockDto();
        listingStockDto.setListingId(stockId);
        listingStockDto.setPrice(1.25);

        Capital capital = new Capital();
        capital.setListingId(stockId);
        capital.setListingType(ListingType.STOCK);
        capital.setTotal(100.0);
        capital.setReserved(20.0);
        capital.setBankAccount(bankAccount);

        when(bankAccountService.getDefaultBankAccount()).thenReturn(bankAccount);
        when(marketService.getStockById(stockId)).thenReturn(listingStockDto);
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(stockId, ListingType.STOCK, bankAccount)).thenReturn(Optional.of(capital));

        // Act
        Double result = capitalService.estimateBalanceStock(stockId);

        // Assert
        Double expectedBalance = 100.0;
        assertEquals(expectedBalance, result);
    }

    @Test
    void testEstimateBalanceOptions() {
        // Arrange
        Long optionsId = 1L;
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123456789");

        OptionsDto optionsDto = new OptionsDto();
        optionsDto.setListingId(optionsId);
        optionsDto.setPrice(1.25);

        Capital capital = new Capital();
        capital.setListingId(optionsId);
        capital.setListingType(ListingType.OPTIONS);
        capital.setTotal(100.0);
        capital.setReserved(20.0);
        capital.setBankAccount(bankAccount);

        when(bankAccountService.getDefaultBankAccount()).thenReturn(bankAccount);
        when(marketService.getOptionsById(optionsId)).thenReturn(optionsDto);
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(optionsId, ListingType.OPTIONS, bankAccount)).thenReturn(Optional.of(capital));

        // Act
        Double result = capitalService.estimateBalanceOptions(optionsId);

        // Assert
        Double expectedBalance = 100.0;
        assertEquals(expectedBalance, result);
    }

    @Test
    void testEstimateBalanceForex() {
        // Arrange
        Long forexId = 1L;
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123456789");

        ListingForexDto listingForexDto = new ListingForexDto();
        listingForexDto.setListingId(forexId);
        listingForexDto.setPrice(1.25);

        Capital capital = new Capital();
        capital.setListingId(forexId);
        capital.setListingType(ListingType.FOREX);
        capital.setTotal(100.0);
        capital.setReserved(20.0);
        capital.setBankAccount(bankAccount);

        when(bankAccountService.getDefaultBankAccount()).thenReturn(bankAccount);
        when(marketService.getForexById(forexId)).thenReturn(listingForexDto);
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(forexId, ListingType.FOREX, bankAccount)).thenReturn(Optional.of(capital));

        // Act
        Double result = capitalService.estimateBalanceForex(forexId);

        // Assert
        Double expectedBalance = 100.0;
        assertEquals(expectedBalance, result);
    }

    @Test
    public void testEstimateBalanceFuture_CapitalNotFound() {
        BankAccount bankAccount = new BankAccount();
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(1L, ListingType.FUTURE, bankAccount)).thenReturn(java.util.Optional.empty());
        assertThrows(CapitalNotFoundByListingIdAndTypeException.class, () -> capitalService.estimateBalanceFuture(1L));
    }

    @Test
    void testAddToPublicCapitalCompany() {
        User userPrincipal = new User();
        userPrincipal.setUserId(1L);
        Company company = new Company();
        company.setId(1L);
        userPrincipal.setCompany(company);

        AddPublicCapitalDto setPublicCapitalDto = new AddPublicCapitalDto();
        setPublicCapitalDto.setListingId(1L);
        setPublicCapitalDto.setListingType(ListingType.STOCK);
        setPublicCapitalDto.setAddToPublic(10.0);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123456789");
        Currency currency = new Currency();
        currency.setCurrencyCode("RSD");
        bankAccount.setCurrency(currency);

        Capital capital = new Capital();
        capital.setListingId(1L);
        capital.setListingType(ListingType.STOCK);
        capital.setTotal(100.0);
        capital.setReserved(20.0);
        capital.setPublicTotal(30.0);
        capital.setBankAccount(bankAccount);

        when(bankAccountService.getBankAccountByCompanyAndCurrencyCode(anyLong(),anyString())).thenReturn(bankAccount);
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(anyLong(), any(ListingType.class), any(BankAccount.class))).thenReturn(Optional.of(capital));

        Boolean result = capitalService.addToPublicCapital(userPrincipal, setPublicCapitalDto);

        assertTrue(result);

    }
    @Test
    void testAddToPublicCapitalCompany_OTCInvalidBankAccountCurrency() {
        User userPrincipal = new User();
        userPrincipal.setUserId(1L);
        Company company = new Company();
        company.setId(1L);
        userPrincipal.setCompany(company);

        AddPublicCapitalDto setPublicCapitalDto = new AddPublicCapitalDto();
        setPublicCapitalDto.setListingId(1L);
        setPublicCapitalDto.setListingType(ListingType.STOCK);
        setPublicCapitalDto.setAddToPublic(10.0);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123456789");
        Currency currency = new Currency();
        currency.setCurrencyCode("USD");
        bankAccount.setCurrency(currency);

        Capital capital = new Capital();
        capital.setListingId(1L);
        capital.setListingType(ListingType.STOCK);
        capital.setTotal(100.0);
        capital.setReserved(20.0);
        capital.setPublicTotal(30.0);
        capital.setBankAccount(bankAccount);

        when(bankAccountService.getBankAccountByCompanyAndCurrencyCode(anyLong(),anyString())).thenReturn(bankAccount);
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(anyLong(), any(ListingType.class), any(BankAccount.class))).thenReturn(Optional.of(capital));

        assertThrows(OTCInvalidBankAccountCurrencyException.class, () -> capitalService.addToPublicCapital(userPrincipal, setPublicCapitalDto));

    }

    @Test
    void testAddToPublicCapitalCompany_NotEnoughCapitalAvailableException() {
        User userPrincipal = new User();
        userPrincipal.setUserId(1L);
        Company company = new Company();
        company.setId(1L);
        userPrincipal.setCompany(company);

        AddPublicCapitalDto setPublicCapitalDto = new AddPublicCapitalDto();
        setPublicCapitalDto.setListingId(1L);
        setPublicCapitalDto.setListingType(ListingType.STOCK);
        setPublicCapitalDto.setAddToPublic(10.0);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123456789");
        Currency currency = new Currency();
        currency.setCurrencyCode("RSD");
        bankAccount.setCurrency(currency);

        Capital capital = new Capital();
        capital.setListingId(1L);
        capital.setListingType(ListingType.STOCK);
        capital.setTotal(10.0);
        capital.setReserved(50.0);
        capital.setPublicTotal(30.0);
        capital.setBankAccount(bankAccount);

        when(bankAccountService.getBankAccountByCompanyAndCurrencyCode(anyLong(),anyString())).thenReturn(bankAccount);
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(anyLong(), any(ListingType.class), any(BankAccount.class))).thenReturn(Optional.of(capital));

        assertThrows(NotEnoughCapitalAvailableException.class, () -> capitalService.addToPublicCapital(userPrincipal, setPublicCapitalDto));

    }

    @Test
    void testAddToPublicCapitalIndividual() {
        User userPrincipal = new User();
        userPrincipal.setUserId(1L);

        AddPublicCapitalDto setPublicCapitalDto = new AddPublicCapitalDto();
        setPublicCapitalDto.setListingId(1L);
        setPublicCapitalDto.setListingType(ListingType.STOCK);
        setPublicCapitalDto.setAddToPublic(10.0);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123456789");
        Currency currency = new Currency();
        currency.setCurrencyCode("RSD");
        bankAccount.setCurrency(currency);

        Capital capital = new Capital();
        capital.setListingId(1L);
        capital.setListingType(ListingType.STOCK);
        capital.setTotal(100.0);
        capital.setReserved(20.0);
        capital.setPublicTotal(30.0);
        capital.setBankAccount(bankAccount);

        when(bankAccountService.getBankAccountByCustomerAndCurrencyCode(anyLong(),anyString())).thenReturn(bankAccount);
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(anyLong(), any(ListingType.class), any(BankAccount.class))).thenReturn(Optional.of(capital));

        Boolean result = capitalService.addToPublicCapital(userPrincipal, setPublicCapitalDto);

        assertTrue(result);

    }

    @Test
    void testAddToPublicCapitalIndividual_OTCListingTypeException() {
        User userPrincipal = new User();
        userPrincipal.setUserId(1L);

        AddPublicCapitalDto setPublicCapitalDto = new AddPublicCapitalDto();
        setPublicCapitalDto.setListingId(1L);
        setPublicCapitalDto.setListingType(ListingType.FUTURE);
        setPublicCapitalDto.setAddToPublic(10.0);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123456789");
        Currency currency = new Currency();
        currency.setCurrencyCode("RSD");
        bankAccount.setCurrency(currency);

        Capital capital = new Capital();
        capital.setListingId(1L);
        capital.setListingType(ListingType.FUTURE);
        capital.setTotal(100.0);
        capital.setReserved(20.0);
        capital.setPublicTotal(30.0);
        capital.setBankAccount(bankAccount);

        when(bankAccountService.getBankAccountByCustomerAndCurrencyCode(anyLong(),anyString())).thenReturn(bankAccount);
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(anyLong(), any(ListingType.class), any(BankAccount.class))).thenReturn(Optional.of(capital));

        assertThrows(OTCListingTypeException.class, () -> capitalService.addToPublicCapital(userPrincipal,setPublicCapitalDto));
        
    }

    @Test
    public void removeFromPublicCapitalTest(){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123456789");
        Currency currency = new Currency();
        currency.setCurrencyCode("RSD");
        bankAccount.setCurrency(currency);

        Capital capital = new Capital();
        capital.setListingId(1L);
        capital.setListingType(ListingType.STOCK);
        capital.setTotal(100.0);
        capital.setReserved(20.0);
        capital.setPublicTotal(50.0);
        capital.setBankAccount(bankAccount);

        Long listingId = 1L;
        Double amountToRemove = 30.0;

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(anyLong(), any(ListingType.class), any(BankAccount.class))).thenReturn(Optional.of(capital));

        capitalService.removeFromPublicCapital(listingId, ListingType.STOCK, bankAccount, amountToRemove);

        Double expectedPublicTotal = 20.0;
        assertEquals(expectedPublicTotal, capital.getPublicTotal());

    }

    @Test
    public void removeFromPublicCapitalTest_NotEnoughCapitalAvailableException(){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123456789");
        Currency currency = new Currency();
        currency.setCurrencyCode("RSD");
        bankAccount.setCurrency(currency);

        Capital capital = new Capital();
        capital.setListingId(1L);
        capital.setListingType(ListingType.STOCK);
        capital.setTotal(10.0);
        capital.setReserved(2.0);
        capital.setPublicTotal(50.0);
        capital.setBankAccount(bankAccount);

        Long listingId = 1L;
        Double amountToRemove = 30.0;

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(anyLong(), any(ListingType.class), any(BankAccount.class))).thenReturn(Optional.of(capital));

        assertThrows(NotEnoughCapitalAvailableException.class, () -> capitalService.removeFromPublicCapital(listingId, ListingType.STOCK, bankAccount, amountToRemove));
    }


    @Test
    public void hasEnoughCapitalForOrder_Buy(){
        MarketOrder order = new MarketOrder();
        order.setOrderType(OrderType.BUY);
        order.setPrice(50.0);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123");
        Currency currency = new Currency();
        currency.setCurrencyCode("RSD");
        bankAccount.setCurrency(currency);
        bankAccount.setAvailableBalance(100.0);

        when(bankAccountService.getDefaultBankAccount()).thenReturn(bankAccount);

        boolean result = capitalService.hasEnoughCapitalForOrder(order);

        assertTrue(result);

    }

    @Test
    public void hasEnoughCapitalForOrderCustomer_Buy(){
        MarketOrder order = new MarketOrder();
        order.setOrderType(OrderType.BUY);
        order.setPrice(50.0);
        Customer customer = new Customer();
        order.setCustomer(customer);
        order.setBankAccountNumber("123");

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123");
        Currency currency = new Currency();
        currency.setCurrencyCode("RSD");
        bankAccount.setCurrency(currency);
        bankAccount.setAvailableBalance(100.0);

        when(bankAccountService.findBankAccountByAccountNumber(anyString())).thenReturn(bankAccount);

        boolean result = capitalService.hasEnoughCapitalForOrder(order);

        assertTrue(result);

    }

    @Test
    public void testHasEnoughCapitalForOrder_Sell() {
        // Arrange
        MarketOrder order = new MarketOrder();
        order.setOrderType(OrderType.SELL);
        order.setContractSize(50L);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("123456789");

        Capital capital = new Capital();
        capital.setTotal(100.0);
        capital.setReserved(20.0);

        when(bankAccountService.getDefaultBankAccount()).thenReturn(bankAccount);
        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(order.getListingId(), order.getListingType(), bankAccount)).thenReturn(Optional.of(capital));

        // Act
        boolean result = capitalService.hasEnoughCapitalForOrder(order);

        // Assert
        assertTrue(result);
    }


    @Test
    public void testGetListingCapitalsQuantity() {
        User user = new User();
        user.setUserId(1L);

        Capital capital1 = new Capital();
        capital1.setListingId(1L);
        capital1.setListingType(ListingType.STOCK);
        capital1.setTotal(500.0);
        capital1.setReserved(100.0);

        Capital capital2 = new Capital();
        capital2.setListingId(2L);
        capital2.setListingType(ListingType.FOREX);
        capital2.setTotal(400.0);
        capital2.setReserved(0.0);

        Capital capital3 = new Capital();
        capital3.setListingId(3L);
        capital3.setListingType(ListingType.FUTURE);
        capital3.setTotal(300.0);
        capital3.setReserved(75.0);

        Capital capital4 = new Capital();

        ListingStockDto stock1 = new ListingStockDto();
        stock1.setListingId(1L);
        stock1.setListingType("STOCK");
        stock1.setPrice(100.0);
        ListingForexDto forex1 = new ListingForexDto();
        forex1.setListingId(2L);
        forex1.setListingType("FOREX");
        forex1.setPrice(50.0);
        ListingFutureDto future1 = new ListingFutureDto();
        future1.setListingId(3L);
        future1.setListingType("FUTURE");
        future1.setPrice(75.0);

        CapitalProfitDto capitalProfitDto1 = new CapitalProfitDto();
        capitalProfitDto1.setListingId(1L);
        capitalProfitDto1.setListingType(ListingType.STOCK);
        capitalProfitDto1.setTotalPrice(40000.0);
        CapitalProfitDto capitalProfitDto2 = new CapitalProfitDto();
        capitalProfitDto1.setListingId(2L);
        capitalProfitDto1.setListingType(ListingType.FOREX);
        capitalProfitDto2.setTotalPrice(20000.0);
        CapitalProfitDto capitalProfitDto3 = new CapitalProfitDto();
        capitalProfitDto1.setListingId(3L);
        capitalProfitDto1.setListingType(ListingType.FUTURE);
        capitalProfitDto3.setTotalPrice(16875.0);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("1234");

        List<Capital> capitals = Arrays.asList(capital1, capital2, capital3, capital4);
        List<CapitalProfitDto> expectedResults = Arrays.asList(capitalProfitDto1, capitalProfitDto2, capitalProfitDto3);

        when(bankAccountService.getBankAccountByCustomerAndCurrencyCode(any(),any())).thenReturn(bankAccount);
        when(capitalRepository.findByBankAccount_AccountNumber(any())).thenReturn(capitals);
        when(marketService.getStockById(1L)).thenReturn(stock1);
        when(marketService.getForexById(2L)).thenReturn(forex1);
        when(marketService.getFutureById(3L)).thenReturn(future1);

        List<CapitalProfitDto> actualResults = capitalService.getListingCapitalsQuantity(user);

        assertEquals(expectedResults.size(), actualResults.size());
    }

    @Test
    public void reserveBalanceTest(){
        Capital capital = new Capital();
        capital.setTotal(100.0);
        capital.setReserved(10.0);
        Long listingId = 1L;
        ListingType listingType = ListingType.STOCK;
        Double amount = 10.0;
        BankAccount bankAccount = new BankAccount();

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(any(),any(),any())).thenReturn(Optional.of(capital));

        capitalService.reserveBalance(listingId,listingType,bankAccount,amount);

        verify(capitalRepository).save(eq(capital));
    }

    @Test
    public void reserveBalanceTest_InvalidReservationAmountException(){
        Capital capital = new Capital();
        capital.setTotal(100.0);
        capital.setReserved(10.0);
        Long listingId = 1L;
        ListingType listingType = ListingType.STOCK;
        Double amount = -10.0;
        BankAccount bankAccount = new BankAccount();

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(any(),any(),any())).thenReturn(Optional.of(capital));

        assertThrows(InvalidReservationAmountException.class, () ->  capitalService.reserveBalance(listingId,listingType,bankAccount,amount));
    }

    @Test
    public void reserveBalanceTest_NotEnoughCapitalAvailableException(){
        Capital capital = new Capital();
        capital.setTotal(100.0);
        capital.setReserved(10.0);
        Long listingId = 1L;
        ListingType listingType = ListingType.STOCK;
        Double amount = 100.0;
        BankAccount bankAccount = new BankAccount();

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(any(),any(),any())).thenReturn(Optional.of(capital));

        assertThrows(NotEnoughCapitalAvailableException.class, () ->  capitalService.reserveBalance(listingId,listingType,bankAccount,amount));

    }

    @Test
    public void getAllPublicCapitalsCustomer(){
        Capital capital = new Capital();
        when(capitalRepository.getAllByPublicTotalGreaterThan(any())).thenReturn(List.of(capital));
        Customer customer = new Customer();
        BankAccount bankAccount = new BankAccount();
        customer.setAccountIds(List.of(bankAccount));

        BankAccount bankAccount1 = new BankAccount();
        capital.setBankAccount(bankAccount1);
        capitalService.getAllPublicCapitals(customer);
    }

    @Test
    public void updateAverageBuyingPrice(){
        Capital capital = new Capital();
        capital.setTotal(100.0);
        capital.setReserved(10.0);
        Long listingId = 1L;
        ListingType listingType = ListingType.STOCK;
        Double amount = 10.0;
        BankAccount bankAccount = new BankAccount();

        when(capitalRepository.getCapitalByListingIdAndListingTypeAndBankAccount(any(),any(),any())).thenReturn(Optional.of(capital));

        capitalService.updateAverageBuyingPrice(listingId,listingType,bankAccount,amount);

        verify(capitalRepository).save(eq(capital));
    }

//    @Test
//    public void getAllPublicCapitals(){
//        Capital capital1 = new Capital();
//        BankAccount bankAccount = new BankAccount();
//        Company company = new Company();
//        company.setCompanyName("dfsad");
//        bankAccount.setCompany(company);
//        capital1.setBankAccount(bankAccount);
//
//        Capital capital2 = new Capital();
//        Customer customer = new Customer();
//        customer.setFirstName("dfsad");
//        customer.setLastName("dfsad");
//        bankAccount.setCustomer(customer);
//        capital2.setBankAccount(bankAccount);
//
//        List<Capital> capitals = new ArrayList<>();
//        capitals.add(capital1);
//        capitals.add(capital2);
//
//        when(capitalRepository.getAllByPublicTotalGreaterThan(any())).thenReturn(capitals);
//
//        List<AllPublicCapitalsDto> out = capitalService.getAllPublicCapitals();
//    }


//    @Test
//    void testGetAllPublicCapitals() {
//        Customer customer = new Customer();
//        customer.setUserId(1L);
//
//        BankAccount bankAccount1 = new BankAccount();
//        bankAccount1.setAccountNumber("123456789");
//
//        BankAccount bankAccount2 = new BankAccount();
//        bankAccount2.setAccountNumber("123");
//
//        customer.setAccountIds(List.of(bankAccount1,bankAccount2));
//
//        Capital capital1 = new Capital();
//        capital1.setListingId(1L);
//        capital1.setListingType(ListingType.STOCK);
//        capital1.setTotal(100.0);
//        capital1.setReserved(20.0);
//        capital1.setBankAccount(bankAccount1);
//
//        Capital capital2 = new Capital();
//        capital2.setListingId(2L);
//        capital2.setListingType(ListingType.FOREX);
//        capital2.setTotal(200.0);
//        capital2.setReserved(40.0);
//        capital2.setBankAccount(bankAccount2);
//
//        List<Capital> capitals = Arrays.asList(capital1, capital2);
//
//        when(capitalRepository.getAllByPublicTotalGreaterThan(0d)).thenReturn(capitals);
//
//        // Act
//        List<AllPublicCapitalsDto> result = capitalService.getAllPublicCapitals(customer);
//
//        // Assert
//        assertEquals(2, result.size());
//    }
}



//
//    @Nested
//    class ProcessCommitReservedTests {
////        @Test
////        void shouldCommitFunds() {
////            Capital capital = new Capital();
////            BankAccount bankAccount = new BankAccount();
////            bankAccount.setBalance(1000.0);
////            bankAccount.setAvailableBalance(500.0);
////            capital.setBankAccount(bankAccount);
////            capital.setTotal(1000.0);
////            capital.setReserved(500.0);
////
////            String currencyCode = "RSD";
////            double amount = 100.0;
////
////            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));
////
////            capitalService.commitReserved(currencyCode, amount);
////
////            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
////            verify(capitalRepository).save(eq(capital));
////        }
////        @Test
////        void shouldCommitMoreFundsThanReservedAndSucceeds() {
////            Capital capital = new Capital();
////            BankAccount bankAccount = new BankAccount();
////            bankAccount.setBalance(1000.0);
////            bankAccount.setAvailableBalance(500.0);
////            capital.setBankAccount(bankAccount);
////            capital.setTotal(1000.0);
////            capital.setReserved(500.0);
////
////            String currencyCode = "RSD";
////            double amount = 600.0;
////
////            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));
////
////            capitalService.commitReserved(currencyCode, amount);
////
////            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
////            verify(capitalRepository).save(eq(capital));
////        }
//
//        @Test
//        void shouldCommitSoldSecurities() {
//            ListingType listingType = ListingType.STOCK;
//            long listingId = 1;
//
//            Capital capital = new Capital();
//            capital.setListingType(listingType);
//            capital.setListingId(listingId);
//            capital.setTotal(1000.0);
//            capital.setReserved(100.0);
//
//            double amount = 100.0;
//
//            when(capitalRepository.getCapitalByListingIdAndListingType(anyLong(), any(ListingType.class))).thenReturn(Optional.of(capital));
//
//            capitalService.commitReserved(listingId, listingType, amount);
//
//            verify(capitalRepository).getCapitalByListingIdAndListingType(eq(listingId), eq(listingType));
//            verify(capitalRepository).save(eq(capital));
//        }
//
////        @Test
////        void shouldThrowInvalidReservationAmountException() {
////            Capital capital = new Capital();
////
////            String currencyCode = "RSD";
////            double amount = -1;
////
////            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));
////
////            assertThrows(InvalidReservationAmountException.class, () -> capitalService.commitReserved(currencyCode, amount));
////
////            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
//        }
//
//        @Test
//        void shouldThrowInvalidReservationAmountExceptionOnMoreAmount() {
//            Capital capital = new Capital();
//            BankAccount bankAccount = new BankAccount();
//            bankAccount.setBalance(550.0);
//            bankAccount.setAvailableBalance(500.0);
//            capital.setBankAccount(bankAccount);
//            capital.setTotal(550.0);
//            capital.setReserved(500.0);
//
//            String currencyCode = "RSD";
//            double amount = 600.0;
//
//            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));
//
//            assertThrows(InvalidReservationAmountException.class, () -> capitalService.commitReserved(currencyCode, amount));
//
//            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
//        }
//
//    }
//
//    @Nested
//    class ProcessReservationReleasedTests {
//        @Test
//        void shouldReleaseFundsForBuyOrder() {
//            Capital capital = new Capital();
//            BankAccount bankAccount = new BankAccount();
//            bankAccount.setBalance(1000.0);
//            bankAccount.setAvailableBalance(500.0);
//            capital.setBankAccount(bankAccount);
//            capital.setTotal(1000.0);
//            capital.setReserved(500.0);
//
//            String currencyCode = "RSD";
//            double amount = 100.0;
//
//            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));
//
//            capitalService.releaseReserved(currencyCode, amount);
//
//            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
//            verify(capitalRepository).save(eq(capital));
//        }
//
//        @Test
//        void shouldReleaseFundsForSellOrder() {
//            ListingType listingType = ListingType.STOCK;
//            long listingId = 1;
//
//            Capital capital = new Capital();
//            capital.setListingType(listingType);
//            capital.setListingId(listingId);
//            capital.setTotal(1000.0);
//            capital.setReserved(100.0);
//
//            double amount = 100.0;
//
//            when(capitalRepository.getCapitalByListingIdAndListingType(anyLong(), any(ListingType.class))).thenReturn(Optional.of(capital));
//
//            capitalService.releaseReserved(listingId, listingType, amount);
//
//            verify(capitalRepository).getCapitalByListingIdAndListingType(eq(listingId), eq(listingType));
//            verify(capitalRepository).save(eq(capital));
//        }
//
//        @Test
//        void shouldThrowInvalidReservationAmountExceptionAmount() {
//            Capital capital = new Capital();
//            BankAccount bankAccount = new BankAccount();
//            bankAccount.setBalance(1000.0);
//            bankAccount.setAvailableBalance(500.0);
//            capital.setBankAccount(bankAccount);
//            capital.setTotal(1000.0);
//            capital.setReserved(500.0);
//
//            String currencyCode = "RSD";
//            double amount = -100.0;
//
//            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));
//
//            assertThrows(InvalidReservationAmountException.class, () -> capitalService.releaseReserved(currencyCode, amount));
//
//            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
//        }
//
//        @Test
//        void shouldThrowInvalidReservationAmountExceptionReservedAmount() {
//            Capital capital = new Capital();
//            BankAccount bankAccount = new BankAccount();
//            bankAccount.setBalance(1000.0);
//            bankAccount.setAvailableBalance(500.0);
//            capital.setBankAccount(bankAccount);
//            capital.setTotal(1000.0);
//            capital.setReserved(500.0);
//
//            String currencyCode = "RSD";
//            double amount = 600;
//
//            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));
//
//            assertThrows(InvalidReservationAmountException.class, () -> capitalService.releaseReserved(currencyCode, amount));
//
//            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
//        }
//    }
//




