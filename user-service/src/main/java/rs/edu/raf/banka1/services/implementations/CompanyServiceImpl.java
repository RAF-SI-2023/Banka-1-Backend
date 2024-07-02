package rs.edu.raf.banka1.services.implementations;

import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.CompanyDto;
import rs.edu.raf.banka1.dtos.CreateCompanyDto;
import rs.edu.raf.banka1.dtos.JoinCompanyDto;
import rs.edu.raf.banka1.exceptions.BankAccountNotFoundException;
import rs.edu.raf.banka1.exceptions.CompanyNotFoundException;
import rs.edu.raf.banka1.exceptions.CustomerNotFoundException;
import rs.edu.raf.banka1.mapper.BankAccountMapper;
import rs.edu.raf.banka1.mapper.CapitalMapper;
import rs.edu.raf.banka1.mapper.CompanyMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CompanyService;
import rs.edu.raf.banka1.utils.Constants;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CustomerRepository customerRepository;
    private final BankAccountMapper bankAccountMapper;
    private final CurrencyRepository currencyRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CapitalMapper capitalMapper;
    private final CapitalRepository capitalRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository,
                              CompanyMapper companyMapper,
                              CustomerRepository customerRepository,
                              BankAccountMapper bankAccountMapper,
                              CurrencyRepository currencyRepository,
                              BankAccountRepository bankAccountRepository,
                              CapitalMapper capitalMapper,
                              CapitalRepository capitalRepository) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.customerRepository = customerRepository;
        this.bankAccountMapper = bankAccountMapper;
        this.currencyRepository = currencyRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.capitalMapper = capitalMapper;
        this.capitalRepository = capitalRepository;
    }

    @Override
    public Company getCompanyById(final Long id) {
        return companyRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<Company> getCompanies(final String name, final String idNumber, final String pib) {
        if(name!=null){
            return companyRepository.findCompaniesByCompanyNameContainingIgnoreCase(name);
        }else if(idNumber!=null){
            return companyRepository.findCompaniesByIdNumberContainingIgnoreCase(idNumber);
        }else if(pib!=null){
            return companyRepository.findCompaniesByPibContainingIgnoreCase(pib);
        }
        return companyRepository.findAll();
    }

    @Override
    public Company createCompany(CreateCompanyDto createCompanyDto) {
        Company company = companyMapper.createCompany(createCompanyDto);
        companyRepository.save(company);

        // generate default bank account for company
        Currency defaultCurrency = currencyRepository.findCurrencyByCurrencyCode(Constants.DEFAULT_CURRENCY).get();
        BankAccount bankAccount = bankAccountMapper.generateBankAccountCompany(company, defaultCurrency);
        bankAccountRepository.save(bankAccount);

        return company;
    }

    @Override
    public Boolean joinCompany(JoinCompanyDto joinCompanyDto) {
        Company company;
        try {
            company = companyRepository.findCompaniesByPibContainingIgnoreCase(joinCompanyDto.getCompanyPib()).getFirst();
        } catch (NoSuchElementException e) {
            throw new CompanyNotFoundException(joinCompanyDto.getCompanyPib());
        }
        if(company == null) return false;
        Customer customer = customerRepository.findCustomerByEmail(joinCompanyDto.getCustomerEmail()).orElseThrow(CustomerNotFoundException::new);
        customer.setCompany(company);
        customerRepository.save(customer);
        return true;
    }

    @Override
    public CompanyDto getBankCompany() {
        return companyMapper.toDto(bankAccountRepository.findBankByCurrencyCode(Constants.DEFAULT_CURRENCY).orElseThrow(BankAccountNotFoundException::new).getCompany());
    }
}
