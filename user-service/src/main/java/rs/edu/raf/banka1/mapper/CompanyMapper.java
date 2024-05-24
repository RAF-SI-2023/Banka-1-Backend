package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.CompanyDto;
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
}
