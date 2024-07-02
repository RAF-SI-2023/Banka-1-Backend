package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.CompanyDto;
import rs.edu.raf.banka1.dtos.CreateCompanyDto;
import rs.edu.raf.banka1.dtos.JoinCompanyDto;
import rs.edu.raf.banka1.model.Company;

import java.util.List;

public interface CompanyService {
    Company getCompanyById(final Long id);
    List<Company> getCompanies(final String name, final String idNumber, final String pib);
    Company createCompany(CreateCompanyDto createCompanyDto);
    Boolean joinCompany(JoinCompanyDto joinCompanyDto);
    CompanyDto getBankCompany();
}
