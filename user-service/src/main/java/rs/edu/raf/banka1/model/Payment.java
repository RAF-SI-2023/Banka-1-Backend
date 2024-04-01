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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    //broj naloga
    private String senderAccountNumber;
    private Double amount;
    private String recipientAccountNumber;
    private String recipient;
    //primalac -> ime i prezime i adresa
    private Integer paymentCode;
    //sifra placanja
    private String referenceNumber;
    //poziv na broj
    private String purposeOfPayment;
    private Long paymentDate;

}
