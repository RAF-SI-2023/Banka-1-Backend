package rs.edu.raf.banka1.services.implementations;

import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.Company;
import rs.edu.raf.banka1.repositories.CompanyRepository;
import rs.edu.raf.banka1.services.CompanyService;

import java.util.NoSuchElementException;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }


    @Override
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
}
