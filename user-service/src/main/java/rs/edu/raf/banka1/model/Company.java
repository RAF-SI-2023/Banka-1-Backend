package rs.edu.raf.banka1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
