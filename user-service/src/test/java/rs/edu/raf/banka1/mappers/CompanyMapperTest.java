package rs.edu.raf.banka1.mappers;
import org.junit.jupiter.api.Test;
import rs.edu.raf.banka1.dtos.CompanyDto;
import rs.edu.raf.banka1.model.Company;
import rs.edu.raf.banka1.mapper.CompanyMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompanyMapperTest {

    private final CompanyMapper companyMapper = new CompanyMapper();

    @Test
    void testToDto() {
        // Arrange
        Company company = new Company();
        company.setId(1L);
        company.setCompanyName("Company Name");
        company.setTelephoneNumber("123456789");
        company.setFaxNumber("987654321");
        company.setPib("1234567890123");
        company.setIdNumber("ID123456");
        company.setJobId("Job123");
        company.setRegistrationNumber("Reg123");

        // Act
        CompanyDto companyDto = companyMapper.toDto(company);

        // Assert
        assertEquals(company.getId(), companyDto.getId());
        assertEquals(company.getCompanyName(), companyDto.getCompanyName());
        assertEquals(company.getTelephoneNumber(), companyDto.getTelephoneNumber());
        assertEquals(company.getFaxNumber(), companyDto.getFaxNumber());
        assertEquals(company.getPib(), companyDto.getPib());
        assertEquals(company.getIdNumber(), companyDto.getIdNumber());
        assertEquals(company.getJobId(), companyDto.getJobId());
        assertEquals(company.getRegistrationNumber(), companyDto.getRegistrationNumber());
    }
}
