package rs.edu.raf.banka1.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDto {
    private Long id;
    private String companyName;
    private String telephoneNumber;
    private String faxNumber;
    private String pib;
    //  Matični broj
    private String idNumber;
    //  Šifra delatnosti
    private String jobId;
    //  Registarski broj
    private String registrationNumber;
}
