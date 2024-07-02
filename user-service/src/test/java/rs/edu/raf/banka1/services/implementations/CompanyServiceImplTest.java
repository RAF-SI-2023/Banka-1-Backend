package rs.edu.raf.banka1.services.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.dtos.CompanyDto;
import rs.edu.raf.banka1.dtos.CreateCompanyDto;
import rs.edu.raf.banka1.dtos.JoinCompanyDto;
import rs.edu.raf.banka1.exceptions.CompanyNotFoundException;
import rs.edu.raf.banka1.exceptions.CustomerNotFoundException;
import rs.edu.raf.banka1.mapper.BankAccountMapper;
import rs.edu.raf.banka1.mapper.CapitalMapper;
import rs.edu.raf.banka1.mapper.CompanyMapper;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Company;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CompanyService;
import rs.edu.raf.banka1.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CompanyServiceImplTest {
    private CompanyMapper companyMapper = new CompanyMapper();
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private BankAccountMapper bankAccountMapper;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private CapitalMapper capitalMapper;
    @Mock
    private CapitalRepository capitalRepository;

    private CompanyService companyService;
    @BeforeEach
    void setup() {
        companyService = new CompanyServiceImpl(companyRepository, companyMapper,
                customerRepository, bankAccountMapper, currencyRepository, bankAccountRepository,
                capitalMapper, capitalRepository);
    }

    @Nested
    class CreateCompanyTests {
        @Test
        public void createsCompanyTest() {
            CreateCompanyDto createCompanyDto = new CreateCompanyDto();
            createCompanyDto.setCompanyName("company name");
            createCompanyDto.setPib("123456789");
            createCompanyDto.setAddress("company address");
            createCompanyDto.setJobId("jobId");
            createCompanyDto.setFaxNumber("faxNumber");
            createCompanyDto.setIdNumber("idNumber");
            createCompanyDto.setRegistrationNumber("regNumber");
            createCompanyDto.setTelephoneNumber("telephoneNumber");

            Company companyResultExpected = new Company();
            companyResultExpected.setCompanyName("company name");
            companyResultExpected.setPib("123456789");
            companyResultExpected.setAdress("company address");
            companyResultExpected.setJobId("jobId");
            companyResultExpected.setFaxNumber("faxNumber");
            companyResultExpected.setIdNumber("idNumber");
            companyResultExpected.setRegistrationNumber("regNumber");
            companyResultExpected.setTelephoneNumber("telephoneNumber");

            when(currencyRepository.findCurrencyByCurrencyCode("RSD")).thenReturn(Optional.of(new Currency()));
            Company realResult = companyService.createCompany(createCompanyDto);

            assertEquals(companyResultExpected.getCompanyName(), realResult.getCompanyName());
            assertEquals(companyResultExpected.getPib(), realResult.getPib());
            assertEquals(companyResultExpected.getAdress(), realResult.getAdress());
            assertEquals(companyResultExpected.getJobId(), realResult.getJobId());
            assertEquals(companyResultExpected.getIdNumber(), realResult.getIdNumber());
            assertEquals(companyResultExpected.getFaxNumber(), realResult.getFaxNumber());
            assertEquals(companyResultExpected.getTelephoneNumber(), realResult.getTelephoneNumber());
            assertEquals(companyResultExpected.getRegistrationNumber(), realResult.getRegistrationNumber());
        }
    }

    @Nested
    class JoinCompanyTests {
        @Test
        public void joinCompanySuccess() {
            JoinCompanyDto joinCompanyDto = new JoinCompanyDto();
            joinCompanyDto.setCompanyPib("companyPib");
            joinCompanyDto.setCustomerEmail("customerEmail");

            Company company = new Company();
            company.setPib("companyPib");
            List<Company> companyList = new ArrayList<>();
            companyList.add(company);

            Customer customer = new Customer();
            customer.setEmail("customerEmail");

            when(companyRepository.findCompaniesByPibContainingIgnoreCase("companyPib")).thenReturn(companyList);
            when(customerRepository.findCustomerByEmail("customerEmail")).thenReturn(Optional.of(customer));

            assertEquals(companyService.joinCompany(joinCompanyDto), true);
        }
        @Test
        public void joinCompanyCompanyNotFoundException() {
            JoinCompanyDto joinCompanyDto = new JoinCompanyDto();
            joinCompanyDto.setCompanyPib("companyPibBad");
            joinCompanyDto.setCustomerEmail("customerEmail");

            when(companyRepository.findCompaniesByPibContainingIgnoreCase("companyPib")).thenReturn(new ArrayList<>());

            assertThrows(CompanyNotFoundException.class, () -> companyService.joinCompany(joinCompanyDto));
        }
        @Test
        public void joinCompanyCompanyIsNull() {
            JoinCompanyDto joinCompanyDto = new JoinCompanyDto();
            joinCompanyDto.setCompanyPib("companyPibBad");
            joinCompanyDto.setCustomerEmail("customerEmail");

            when(companyRepository.findCompaniesByPibContainingIgnoreCase(anyString())).thenReturn(List.of());

            assertThrows(CompanyNotFoundException.class, () -> companyService.joinCompany(joinCompanyDto));
            verify(companyRepository).findCompaniesByPibContainingIgnoreCase(eq("companyPibBad"));
        }

        @Test
        public void joinCompanyCustomerNotFoundException() {
            JoinCompanyDto joinCompanyDto = new JoinCompanyDto();
            joinCompanyDto.setCompanyPib("companyPib");
            joinCompanyDto.setCustomerEmail("customerEmail");

            Company company = new Company();
            company.setPib("companyPib");
            List<Company> companyList = new ArrayList<>();
            companyList.add(company);

            Customer customer = new Customer();
            customer.setEmail("customerEmail");

            when(companyRepository.findCompaniesByPibContainingIgnoreCase("companyPib")).thenReturn(companyList);
            when(customerRepository.findCustomerByEmail("customerEmail")).thenThrow(CustomerNotFoundException.class);

            assertThrows(CustomerNotFoundException.class, () -> companyService.joinCompany(joinCompanyDto));
        }
    }

    @Nested
    class GetBankCompanyTests {
        @Test
        void shouldGetBank() {
            Company company = new Company();
            company.setCompanyName("company name");
            company.setPib("123456789");
            company.setAdress("company address");
            company.setJobId("jobId");
            company.setFaxNumber("faxNumber");
            company.setIdNumber("idNumber");
            company.setRegistrationNumber("regNumber");
            company.setTelephoneNumber("telephoneNumber");


            CompanyDto companyDto = new CompanyDto();
            companyDto.setCompanyName("company name");
            companyDto.setPib("123456789");
            companyDto.setAddress("company address");
            companyDto.setJobId("jobId");
            companyDto.setFaxNumber("faxNumber");
            companyDto.setIdNumber("idNumber");
            companyDto.setRegistrationNumber("regNumber");
            companyDto.setTelephoneNumber("telephoneNumber");

            BankAccount bankAccount = new BankAccount();
            bankAccount.setCompany(company);

            when(bankAccountRepository.findBankByCurrencyCode(anyString())).thenReturn(Optional.of(bankAccount));

            CompanyDto result = companyService.getBankCompany();

            assertEquals(companyDto, result);

            verify(bankAccountRepository).findBankByCurrencyCode(eq(Constants.DEFAULT_CURRENCY));
        }
    }

}