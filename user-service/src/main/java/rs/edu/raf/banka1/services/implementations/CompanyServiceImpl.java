package rs.edu.raf.banka1.services.implementations;

import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.CreateCompanyDto;
import rs.edu.raf.banka1.dtos.JoinCompanyDto;
import rs.edu.raf.banka1.exceptions.CompanyNotFoundException;
import rs.edu.raf.banka1.exceptions.CustomerNotFoundException;
import rs.edu.raf.banka1.mapper.CompanyMapper;
import rs.edu.raf.banka1.model.Company;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.repositories.CompanyRepository;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.services.CompanyService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CustomerRepository customerRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository,
                              CompanyMapper companyMapper,
                              CustomerRepository customerRepository) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.customerRepository = customerRepository;
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
        return companyRepository.save(company);
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
}
