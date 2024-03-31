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
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transferId;

    private String senderAccountNumber;
    private Double amount;
    private String recipientAccountNumber;
    private Double convertedAmount;
    //Konvertovani iznos sa prvog računa
    private Double exchange;
    //trenutni kurs izmedju valute
    private Double commision;
    //provizija
    private Long transferDate;

}
