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
    private Integer pib;
    //  Matični broj
    private Integer idNumber;
    //  Šifra delatnosti
    private Integer jobId;
    //  Registarski broj
    private Integer registrationNumber;
}
