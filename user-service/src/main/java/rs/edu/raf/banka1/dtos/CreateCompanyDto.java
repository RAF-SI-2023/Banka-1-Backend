package rs.edu.raf.banka1.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CreateCompanyDto {
    @NotBlank
    private String companyName;
    private String telephoneNumber;
    private String faxNumber;
    @NotBlank
    private String pib;
    private String idNumber;
    private String jobId;
    private String registrationNumber;
    @NotBlank
    private String address;
}
