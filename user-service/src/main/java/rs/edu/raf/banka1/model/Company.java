package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String companyName;

    @Column
    private String telephoneNumber;

    @Column
    private String faxNumber;

    @Column
    private String pib;

//  Matični broj
    @Column
    private String idNumber;

//  Šifra delatnosti
    @Column
    private String jobId;

//  Registarski broj
    @Column
    private String registrationNumber;
}
