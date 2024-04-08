package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_bankaccount_id", referencedColumnName = "id")
    private BankAccount senderBankAccount;

//    private String senderAccountNumber;
    private String recipientName;
    private String recipientAccountNumber;
    private Double amount;
    private Double convertedAmount;
    //Konvertovani iznos sa prvog računa
    private Double exchange;
    //trenutni kurs izmedju valute
    private Double commision;
    //
    private TransactionStatus status;
    private String paymentCode;
    private String model;
    private String referenceNumber;
    private String channel;
    private Long transferDate;


}
