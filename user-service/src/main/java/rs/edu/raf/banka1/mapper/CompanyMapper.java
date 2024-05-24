package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.CompanyDto;
import rs.edu.raf.banka1.dtos.CreateCompanyDto;
import rs.edu.raf.banka1.model.Company;

@Component
public class CompanyMapper {
    public CompanyDto toDto(Company company) {
        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(company.getId());
        companyDto.setCompanyName(company.getCompanyName());
        companyDto.setTelephoneNumber(company.getTelephoneNumber());
        companyDto.setFaxNumber(company.getFaxNumber());
        companyDto.setPib(company.getPib());
        companyDto.setIdNumber(company.getIdNumber());
        companyDto.setJobId(company.getJobId());
        companyDto.setRegistrationNumber(company.getRegistrationNumber());
        companyDto.setAddress(company.getAdress());
        return companyDto;
    }

    public Company createCompany(CreateCompanyDto createCompanyDto) {
        Company company = new Company();
        company.setCompanyName(createCompanyDto.getCompanyName());
        company.setTelephoneNumber(createCompanyDto.getTelephoneNumber());
        company.setFaxNumber(createCompanyDto.getFaxNumber());
        company.setPib(createCompanyDto.getPib());
        company.setIdNumber(createCompanyDto.getIdNumber());
        company.setJobId(createCompanyDto.getJobId());
        company.setRegistrationNumber(createCompanyDto.getRegistrationNumber());
        company.setAdress(createCompanyDto.getAddress());
        return company;
    }
}
