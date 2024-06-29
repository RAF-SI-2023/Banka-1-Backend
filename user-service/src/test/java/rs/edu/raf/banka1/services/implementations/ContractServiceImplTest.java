package rs.edu.raf.banka1.services.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;
import rs.edu.raf.banka1.dtos.ContractCreateDto;
import rs.edu.raf.banka1.dtos.ContractDto;
import rs.edu.raf.banka1.exceptions.ContractNotFoundByIdException;
import rs.edu.raf.banka1.exceptions.InvalidCapitalAmountException;
import rs.edu.raf.banka1.mapper.ContractMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.ContractRepository;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CapitalService;
import rs.edu.raf.banka1.services.ContractService;
import rs.edu.raf.banka1.utils.Constants;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContractServiceImplTest {
    private ContractMapper contractMapper = new ContractMapper();

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private CapitalService capitalService;

    private ContractService contractService;

    @BeforeEach
    void setup() {
        contractService = new ContractServiceImpl(contractMapper, bankAccountService, contractRepository, capitalService);
    }

    @Nested
    class CreateContractTests {
        @Test
        void shouldCreateContractForIndividual() {
            long userId = 1L;
            BankAccount buyerAccount = new BankAccount();
            buyerAccount.setAccountNumber("123");

            BankAccount sellerAccount = new BankAccount();
            sellerAccount.setAccountNumber("456");

            User user = new User();
            user.setUserId(userId);

            ContractCreateDto contractCreateDto = new ContractCreateDto();
            contractCreateDto.setBankAccountNumber("456");
            contractCreateDto.setListingId(1L);
            contractCreateDto.setTicker("AAPL");
            contractCreateDto.setListingType(ListingType.STOCK);
            contractCreateDto.setOfferPrice(1D);
            contractCreateDto.setAmountToBuy(1D);

            ContractDto resultDto = new ContractDto();
            resultDto.setContractId(null);
            resultDto.setBuyerAccountNumber(buyerAccount.getAccountNumber());
            resultDto.setSellerAccountNumber(sellerAccount.getAccountNumber());
            resultDto.setBankApproval(false);
            resultDto.setSellerApproval(false);
            resultDto.setComment(null);
            resultDto.setReferenceNumber("1/2024");
            resultDto.setTicker("AAPL");
            resultDto.setAmount(1D);
            resultDto.setPrice(1D);
            resultDto.setListingId(1L);
            resultDto.setIsIndividual(true);

            Capital sellerCapital = new Capital();
            sellerCapital.setTotal(2D);
            sellerCapital.setPublicTotal(2D);

            when(contractRepository.count()).thenReturn(0L);
            when(bankAccountService.getBankAccountByNumber(anyString())).thenReturn(sellerAccount);
            when(bankAccountService.getBankAccountByCustomerAndCurrencyCode(anyLong(), anyString())).thenReturn(buyerAccount);
            when(capitalService.getCapitalByListingIdAndTypeAndBankAccount(anyLong(), any(ListingType.class), any(BankAccount.class))).thenReturn(sellerCapital);

            ContractDto result = contractService.createContract(contractCreateDto, user);

            assertEquals(result, resultDto);

            verify(contractRepository).count();
            verify(bankAccountService).getBankAccountByNumber("456");
            verify(bankAccountService).getBankAccountByCustomerAndCurrencyCode(userId, Constants.DEFAULT_CURRENCY);
            verify(capitalService).getCapitalByListingIdAndTypeAndBankAccount(1L, ListingType.STOCK, sellerAccount);
            verify(contractRepository).save(any(Contract.class));
        }

        @Test
        void shouldCreateContractForCompany() {
            long userId = 1L;
            BankAccount buyerAccount = new BankAccount();
            buyerAccount.setAccountNumber("123");

            BankAccount sellerAccount = new BankAccount();
            sellerAccount.setAccountNumber("456");

            User user = new User();
            user.setUserId(userId);

            Company company = new Company();
            company.setId(1L);

            user.setCompany(company);

            ContractCreateDto contractCreateDto = new ContractCreateDto();
            contractCreateDto.setBankAccountNumber("456");
            contractCreateDto.setListingId(1L);
            contractCreateDto.setTicker("AAPL");
            contractCreateDto.setListingType(ListingType.STOCK);
            contractCreateDto.setOfferPrice(1D);
            contractCreateDto.setAmountToBuy(1D);

            ContractDto resultDto = new ContractDto();
            resultDto.setContractId(null);
            resultDto.setBuyerAccountNumber(buyerAccount.getAccountNumber());
            resultDto.setSellerAccountNumber(sellerAccount.getAccountNumber());
            resultDto.setBankApproval(false);
            resultDto.setSellerApproval(false);
            resultDto.setComment(null);
            resultDto.setReferenceNumber("1/2024");
            resultDto.setTicker("AAPL");
            resultDto.setAmount(1D);
            resultDto.setPrice(1D);
            resultDto.setListingId(1L);
            resultDto.setIsIndividual(true);

            Capital sellerCapital = new Capital();
            sellerCapital.setTotal(2D);
            sellerCapital.setPublicTotal(2D);

            when(contractRepository.count()).thenReturn(0L);
            when(bankAccountService.getBankAccountByNumber(anyString())).thenReturn(sellerAccount);
            when(bankAccountService.getBankAccountByCompanyAndCurrencyCode(anyLong(), anyString())).thenReturn(buyerAccount);
            when(capitalService.getCapitalByListingIdAndTypeAndBankAccount(anyLong(), any(ListingType.class), any(BankAccount.class))).thenReturn(sellerCapital);

            ContractDto result = contractService.createContract(contractCreateDto, user);

            assertEquals(result, resultDto);

            verify(contractRepository).count();
            verify(bankAccountService).getBankAccountByNumber("456");
            verify(bankAccountService).getBankAccountByCompanyAndCurrencyCode(user.getCompany().getId(), Constants.DEFAULT_CURRENCY);
            verify(capitalService).getCapitalByListingIdAndTypeAndBankAccount(1L, ListingType.STOCK, sellerAccount);
            verify(contractRepository).save(any(Contract.class));
        }

        @Test
        void shouldThrowInvalidCapitalAmountException() {
            long userId = 1L;
            BankAccount buyerAccount = new BankAccount();
            buyerAccount.setAccountNumber("123");

            BankAccount sellerAccount = new BankAccount();
            sellerAccount.setAccountNumber("456");

            User user = new User();
            user.setUserId(userId);

            Company company = new Company();
            company.setId(1L);

            user.setCompany(company);

            ContractCreateDto contractCreateDto = new ContractCreateDto();
            contractCreateDto.setBankAccountNumber("456");
            contractCreateDto.setListingId(1L);
            contractCreateDto.setTicker("AAPL");
            contractCreateDto.setListingType(ListingType.STOCK);
            contractCreateDto.setOfferPrice(1D);
            contractCreateDto.setAmountToBuy(1D);

            ContractDto resultDto = new ContractDto();
            resultDto.setContractId(null);
            resultDto.setBuyerAccountNumber(buyerAccount.getAccountNumber());
            resultDto.setSellerAccountNumber(sellerAccount.getAccountNumber());
            resultDto.setBankApproval(false);
            resultDto.setSellerApproval(false);
            resultDto.setComment(null);
            resultDto.setReferenceNumber("1/2024");
            resultDto.setTicker("AAPL");
            resultDto.setAmount(1D);
            resultDto.setPrice(1D);

            Capital sellerCapital = new Capital();
            sellerCapital.setTotal(2D);
            sellerCapital.setPublicTotal(0D);

            when(contractRepository.count()).thenReturn(0L);
            when(bankAccountService.getBankAccountByNumber(anyString())).thenReturn(sellerAccount);
            when(bankAccountService.getBankAccountByCompanyAndCurrencyCode(anyLong(), anyString())).thenReturn(buyerAccount);
            when(capitalService.getCapitalByListingIdAndTypeAndBankAccount(anyLong(), any(ListingType.class), any(BankAccount.class))).thenReturn(sellerCapital);

            assertThrows(InvalidCapitalAmountException.class, () -> contractService.createContract(contractCreateDto, user));

            verify(contractRepository).count();
            verify(bankAccountService).getBankAccountByNumber("456");
            verify(bankAccountService).getBankAccountByCompanyAndCurrencyCode(user.getCompany().getId(), Constants.DEFAULT_CURRENCY);
            verify(capitalService).getCapitalByListingIdAndTypeAndBankAccount(1L, ListingType.STOCK, sellerAccount);
        }
    }

    @Nested
    class DenyContractTests {
        @Test
        void shouldDenyContract() {
            long contractId = 1;

            boolean result = contractService.denyContract(contractId, "test");

            assertTrue(result);

            verify(contractRepository).updateCommentById("test", contractId);
        }
    }

    @Nested
    class GetAllContractsSupervisorTests {
        @Test
        void shouldReturnAllContracts() {

            BankAccount buyerAccount = new BankAccount();
            buyerAccount.setAccountNumber("123");

            BankAccount sellerAccount = new BankAccount();
            sellerAccount.setAccountNumber("456");

            Contract contract = new Contract();
            contract.setBuyer(buyerAccount);
            contract.setSeller(sellerAccount);

            ContractDto contractDto = contractMapper.contractToContractDto(contract);

            when(contractRepository.findAll()).thenReturn(List.of(contract));

            List<ContractDto> result = contractService.getAllContractsSupervisor(null);

            assertEquals(result, List.of(contractDto));

            verify(contractRepository).findAll();
        }
    }

    @Nested
    class GetAllContractsCustomerTests {
        @Test
        void shouldReturnAllContractsIndividual() {
            long userId = 1L;

            BankAccount sellerAccount = new BankAccount();
            sellerAccount.setAccountNumber("456");

            Customer user = new Customer();
            user.setUserId(userId);

            BankAccount buyerAccount = new BankAccount();
            buyerAccount.setAccountNumber("123");


            Contract contract = new Contract();
            contract.setBuyer(buyerAccount);
            contract.setSeller(sellerAccount);

            ContractDto contractDto = contractMapper.contractToContractDto(contract);

            when(bankAccountService.getBankAccountByCustomerAndCurrencyCode(anyLong(), anyString())).thenReturn(sellerAccount);
            when(contractRepository.findAllCustomerContracts(anyString())).thenReturn(List.of(contract));

            List<ContractDto> result = contractService.getAllContractsCustomer(user);

            assertEquals(result, List.of(contractDto));

            verify(bankAccountService).getBankAccountByCustomerAndCurrencyCode(userId, Constants.DEFAULT_CURRENCY);
            verify(contractRepository).findAllCustomerContracts(sellerAccount.getAccountNumber());

        }

        @Test
        void shouldReturnAllContractsCompany() {
            long userId = 1L;

            BankAccount sellerAccount = new BankAccount();
            sellerAccount.setAccountNumber("456");

            Company company = new Company();
            company.setId(1L);

            Customer user = new Customer();
            user.setUserId(userId);
            user.setCompany(company);

            BankAccount buyerAccount = new BankAccount();
            buyerAccount.setAccountNumber("123");


            Contract contract = new Contract();
            contract.setBuyer(buyerAccount);
            contract.setSeller(sellerAccount);

            ContractDto contractDto = contractMapper.contractToContractDto(contract);

            when(bankAccountService.getBankAccountByCompanyAndCurrencyCode(anyLong(), anyString())).thenReturn(sellerAccount);
            when(contractRepository.findAllCustomerContracts(anyString())).thenReturn(List.of(contract));

            List<ContractDto> result = contractService.getAllContractsCustomer(user);

            assertEquals(result, List.of(contractDto));

            verify(bankAccountService).getBankAccountByCompanyAndCurrencyCode(user.getCompany().getId(), Constants.DEFAULT_CURRENCY);
            verify(contractRepository).findAllCustomerContracts(sellerAccount.getAccountNumber());
        }
    }

    @Nested
    class AcceptContractTests {
        @Test
        void shouldAcceptAndFinalizeContract() {
            long contractId = 1L;

            BankAccount buyerAccount = new BankAccount();
            buyerAccount.setAccountNumber("123");

            BankAccount sellerAccount = new BankAccount();
            sellerAccount.setAccountNumber("456");

            Contract contract = new Contract();
            contract.setBuyer(buyerAccount);
            contract.setSeller(sellerAccount);
            contract.setListingType(ListingType.STOCK);
            contract.setListingId(1L);
            contract.setAmount(1D);
            contract.setPrice(1D);

            contract.setBankApproval(true);
            contract.setSellerApproval(true);

            when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));

            assertTrue(contractService.acceptContract(contractId));

            verify(contractRepository).acceptContract(eq(contractId));
            verify(contractRepository).findById(eq(contractId));
            verify(capitalService).removeFromPublicCapital(eq(contract.getListingId()), eq(contract.getListingType()), eq(contract.getSeller()), eq(contract.getAmount()));
            verify(capitalService).removeBalance(eq(contract.getListingId()), eq(contract.getListingType()), eq(contract.getSeller()), eq(contract.getAmount()));
            verify(capitalService).addBalance(eq(contract.getListingId()), eq(contract.getListingType()), eq(contract.getBuyer()), eq(contract.getAmount()));
            verify(bankAccountService).removeBalance(eq(contract.getBuyer()), eq(contract.getPrice()));
            verify(bankAccountService).addBalance(eq(contract.getSeller()), eq(contract.getPrice()));
        }

        @Test
        void shouldAcceptAndNotFinalizeContract() {
            long contractId = 1L;

            BankAccount buyerAccount = new BankAccount();
            buyerAccount.setAccountNumber("123");

            BankAccount sellerAccount = new BankAccount();
            sellerAccount.setAccountNumber("456");

            Contract contract = new Contract();
            contract.setBuyer(buyerAccount);
            contract.setSeller(sellerAccount);
            contract.setListingType(ListingType.STOCK);
            contract.setListingId(1L);
            contract.setAmount(1D);
            contract.setPrice(1D);

            contract.setBankApproval(false);
            contract.setSellerApproval(true);

            when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));

            assertTrue(contractService.acceptContract(contractId));

            verify(contractRepository).acceptContract(eq(contractId));
            verify(contractRepository).findById(eq(contractId));
        }

        @Test
        void shouldThrowContractNotFoundById() {
            when(contractRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertThrows(ContractNotFoundByIdException.class, () -> contractService.acceptContract(1L));
            verify(contractRepository).acceptContract(1L);
        }
    }

    @Nested
    class ApproveContractTests {
        @Test
        void shouldApproveAndFinalizeContract() {
            long contractId = 1L;

            BankAccount buyerAccount = new BankAccount();
            buyerAccount.setAccountNumber("123");

            BankAccount sellerAccount = new BankAccount();
            sellerAccount.setAccountNumber("456");

            Contract contract = new Contract();
            contract.setBuyer(buyerAccount);
            contract.setSeller(sellerAccount);
            contract.setListingType(ListingType.STOCK);
            contract.setListingId(1L);
            contract.setAmount(1D);
            contract.setPrice(1D);

            contract.setBankApproval(true);
            contract.setSellerApproval(true);

            when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));

            assertTrue(contractService.approveContract(contractId));

            verify(contractRepository).approveContract(eq(contractId));
            verify(contractRepository).findById(eq(contractId));
            verify(capitalService).removeFromPublicCapital(eq(contract.getListingId()), eq(contract.getListingType()), eq(contract.getSeller()), eq(contract.getAmount()));
            verify(capitalService).removeBalance(eq(contract.getListingId()), eq(contract.getListingType()), eq(contract.getSeller()), eq(contract.getAmount()));
            verify(capitalService).addBalance(eq(contract.getListingId()), eq(contract.getListingType()), eq(contract.getBuyer()), eq(contract.getAmount()));
            verify(bankAccountService).removeBalance(eq(contract.getBuyer()), eq(contract.getPrice()));
            verify(bankAccountService).addBalance(eq(contract.getSeller()), eq(contract.getPrice()));
        }

        @Test
        void shouldApproveAndNotFinalizeContract() {
            long contractId = 1L;

            BankAccount buyerAccount = new BankAccount();
            buyerAccount.setAccountNumber("123");

            BankAccount sellerAccount = new BankAccount();
            sellerAccount.setAccountNumber("456");

            Contract contract = new Contract();
            contract.setBuyer(buyerAccount);
            contract.setSeller(sellerAccount);
            contract.setListingType(ListingType.STOCK);
            contract.setListingId(1L);
            contract.setAmount(1D);
            contract.setPrice(1D);

            contract.setBankApproval(true);
            contract.setSellerApproval(false);

            when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));

            assertTrue(contractService.approveContract(contractId));

            verify(contractRepository).approveContract(eq(contractId));
            verify(contractRepository).findById(eq(contractId));
        }

        @Test
        void shouldThrowContractNotFoundById() {
            when(contractRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertThrows(ContractNotFoundByIdException.class, () -> contractService.approveContract(1L));
            verify(contractRepository).findById(1L);
        }
    }
}